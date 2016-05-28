/*
 * Created on 30 gen 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.component.optimizaton;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream;
import org.sejda.sambox.contentstream.PDFStreamEngine;
import org.sejda.sambox.contentstream.operator.MissingOperandException;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.OperatorProcessor;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.pdmodel.MissingResourceException;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.font.PDType3CharProc;
import org.sejda.sambox.pdmodel.font.PDType3Font;
import org.sejda.sambox.pdmodel.graphics.PDXObject;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.graphics.form.PDTransparencyGroup;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that parses the page content steam and the page annotations appearance stream, wraps any image xobject (type xobject, subtype image) found in an instance of
 * {@link ReadOnlyFilteredCOSStream} and puts it back to the resource dictionary. It's then easy to identify later xobjects in use by the page/s and what can be discarded.
 * 
 * @author Andrea Vacondio
 *
 */
public class ImagesHitter extends PDFStreamEngine implements Consumer<PDPage> {

    private static final Logger LOG = LoggerFactory.getLogger(ImagesHitter.class);
    private Set<COSName> processedFonts = new HashSet<>();

    public ImagesHitter() {
        addOperator(new XObjectOperator());
        addOperator(new Type3FontHitterOperator());
    }

    private class XObjectOperator extends OperatorProcessor {
        @Override
        public void process(Operator operator, List<COSBase> operands) throws IOException {
            if (operands.isEmpty()) {
                throw new MissingOperandException(operator, operands);
            }
            COSBase operand = operands.get(0);
            if (operand instanceof COSName) {

                COSName objectName = (COSName) operand;

                Optional<COSDictionary> xobjects = ofNullable(context.getResources())
                        .map(r -> r.getCOSObject().getDictionaryObject(COSName.XOBJECT, COSDictionary.class))
                        .filter(Objects::nonNull);

                COSBase existing = xobjects.map(d -> d.getDictionaryObject(objectName))
                        .orElseThrow(() -> new MissingResourceException("Missing XObject: " + objectName.getName()));

                if (!(existing instanceof ReadOnlyFilteredCOSStream)) {
                    COSStream imageStream = ofNullable(existing).filter(e -> e instanceof COSStream)
                            .map(e -> (COSStream) e)
                            .orElseThrow(() -> new IllegalArgumentException("External object unexpected type"));

                    String subtype = imageStream.getNameAsString(COSName.SUBTYPE);
                    if (COSName.IMAGE.getName().equals(subtype)) {
                        LOG.trace("Hit image with name {}", objectName.getName());
                        // we wrap the existing so we can identify it later as "in use" and already processed
                        ReadOnlyFilteredCOSStream optimizedImage = ReadOnlyFilteredCOSStream.readOnly(imageStream);
                        COSDictionary resources = context.getResources().getCOSObject();
                        xobjects.orElseGet(() -> {
                            COSDictionary ret = new COSDictionary();
                            resources.setItem(COSName.XOBJECT, ret);
                            return ret;
                        }).setItem(objectName, optimizedImage);
                    } else if (COSName.FORM.getName().equals(subtype)) {
                        PDXObject xobject = PDXObject.createXObject(imageStream, context.getResources());
                        if (xobject instanceof PDTransparencyGroup) {
                            context.showTransparencyGroup((PDTransparencyGroup) xobject);
                        } else if (xobject instanceof PDFormXObject) {
                            context.showForm((PDFormXObject) xobject);
                        }
                    }
                }
            }
        }

        @Override
        public String getName() {
            return "Do";
        }
    }

    private class Type3FontHitterOperator extends OperatorProcessor {
        @Override
        public void process(Operator operator, List<COSBase> operands) throws IOException {
            if (operands.size() < 2) {
                throw new MissingOperandException(operator, operands);
            }
            COSBase operand = operands.get(0);
            if (operand instanceof COSName) {
                COSName fontName = (COSName) operand;
                if (!processedFonts.contains(fontName)) {
                    Optional<COSDictionary> fonts = ofNullable(context.getResources())
                            .map(r -> r.getCOSObject().getDictionaryObject(COSName.FONT, COSDictionary.class))
                            .filter(Objects::nonNull);

                    COSDictionary existing = fonts.map(d -> d.getDictionaryObject(fontName, COSDictionary.class))
                            .orElseThrow(
                                    () -> new MissingResourceException("Missing font resource: " + fontName.getName()));
                    // type 3 fonts glyphs are content stream and they may refer to named resource. If the font resource dictionary is not present
                    // the page resource dictionary is used instead so we have to make sure those resource are hit
                    if (COSName.TYPE3.equals(existing.getCOSName(COSName.SUBTYPE))
                            && isNull(existing.getItem(COSName.RESOURCES))) {
                        LOG.trace("Found type3 font with no resource dictionary {}", fontName.getName());
                        PDType3Font font = new PDType3Font(existing);
                        Collection<COSBase> glyphStreams = ofNullable(
                                existing.getDictionaryObject(COSName.CHAR_PROCS, COSDictionary.class))
                                        .map(chars -> chars.getValues()).filter(v -> !v.isEmpty())
                                        .orElseGet(Collections::emptyList);
                        List<PDType3CharProc> pdStreams = glyphStreams.stream().map(COSBase::getCOSObject)
                                .filter(s -> s instanceof COSStream).map(s -> (COSStream) s)
                                .map(s -> new PDType3CharProc(font, s)).collect(Collectors.toList());
                        for (PDType3CharProc glyph : pdStreams) {
                            processStream(glyph);
                        }
                    }
                }
                processedFonts.add(fontName);
            }
        }

        @Override
        public String getName() {
            return "Tf";
        }
    }

    @Override
    public void accept(PDPage page) {
        processedFonts.clear();
        try {
            this.processPage(page);
            for (PDAnnotation annotation : page.getAnnotations()) {
                this.showAnnotation(annotation);
            }
        } catch (IOException e) {
            LOG.warn("Failed parse page, skipping and continuing with next.", e);
        }
    }

}
