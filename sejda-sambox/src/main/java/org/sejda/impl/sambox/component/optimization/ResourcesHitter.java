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
package org.sejda.impl.sambox.component.optimization;

import static java.util.Objects.isNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import org.sejda.sambox.pdmodel.interactive.annotation.PDAppearanceEntry;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that parses the page content steam and the page annotations appearance stream, wraps any image xobject (type xobject, subtype image) found in an instance of
 * {@link ReadOnlyFilteredCOSStream}, every font in an instance of {@link InUseFontDictionary} and puts them back into the resource dictionary. It's later easy to identify xobjects
 * and fonts in use by the page/s and what can be discarded.
 * 
 * @author Andrea Vacondio
 *
 */
public class ResourcesHitter extends PDFStreamEngine implements Consumer<PDPage> {

    private static final Logger LOG = LoggerFactory.getLogger(ResourcesHitter.class);

    public ResourcesHitter() {
        addOperator(new XObjectOperator());
        addOperator(new FontHitterOperator());
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
                Optional<COSDictionary> xobjects = ofNullable(getContext().getResources())
                        .map(r -> r.getCOSObject().getDictionaryObject(COSName.XOBJECT, COSDictionary.class))
                        .filter(Objects::nonNull);

                COSBase existing = xobjects.map(d -> d.getDictionaryObject(objectName))
                        .orElseThrow(() -> new MissingResourceException("Missing XObject: " + objectName.getName()));

                if (existing instanceof COSStream) {
                    if (!(existing instanceof ReadOnlyFilteredCOSStream)) {
                        COSStream imageStream = (COSStream) existing;
                        LOG.trace("Hit image with name {}", objectName.getName());
                        // we wrap the existing so we can identify it later as "in use" and already processed
                        xobjects.get().setItem(objectName, ReadOnlyFilteredCOSStream.readOnly(imageStream));
                        if (COSName.FORM.getName().equals(imageStream.getNameAsString(COSName.SUBTYPE))) {
                            PDXObject xobject = PDXObject.createXObject(imageStream, getContext().getResources());
                            if (xobject instanceof PDTransparencyGroup) {
                                getContext().showTransparencyGroup((PDTransparencyGroup) xobject);
                            } else if (xobject instanceof PDFormXObject) {
                                getContext().showForm((PDFormXObject) xobject);
                            }
                        }
                    }
                } else {
                    LOG.warn("Unexpected type {} for xObject {}", existing.getClass(), objectName.getName());
                }
            }
        }

        @Override
        public String getName() {
            return "Do";
        }
    }

    private class FontHitterOperator extends OperatorProcessor {
        @Override
        public void process(Operator operator, List<COSBase> operands) throws IOException {
            if (operands.size() < 2) {
                throw new MissingOperandException(operator, operands);
            }
            COSBase operand = operands.get(0);
            if (operand instanceof COSName) {
                COSName fontName = (COSName) operand;
                Optional<COSDictionary> fonts = ofNullable(getContext().getResources())
                        .map(r -> r.getCOSObject().getDictionaryObject(COSName.FONT, COSDictionary.class))
                        .filter(Objects::nonNull);

                COSBase existing = fonts.map(d -> d.getDictionaryObject(fontName)).orElseThrow(
                        () -> new MissingResourceException("Missing font resource: " + fontName.getName()));

                if (!(existing instanceof InUseFontDictionary)) {
                    COSDictionary fontDictionary = of(existing).filter(e -> e instanceof COSDictionary)
                            .map(e -> (COSDictionary) e)
                            .orElseThrow(() -> new IllegalArgumentException("Font resource unexpected type"));

                    LOG.trace("Hit font with name {}", fontName.getName());
                    // we wrap the existing so we can identify it later as "in use" and already processed
                    fonts.get().setItem(fontName, new InUseFontDictionary(fontDictionary));

                    // type 3 fonts glyphs are content stream and they may refer to named resource. If the font resource dictionary is not present
                    // the page resource dictionary is used instead so we have to make sure those resource are hit
                    if (COSName.TYPE3.equals(fontDictionary.getCOSName(COSName.SUBTYPE))
                            && isNull(fontDictionary.getItem(COSName.RESOURCES))) {
                        LOG.trace("Found type3 font with no resource dictionary {}", fontName.getName());
                        PDType3Font font = new PDType3Font(fontDictionary);
                        Collection<COSBase> glyphStreams = ofNullable(
                                fontDictionary.getDictionaryObject(COSName.CHAR_PROCS, COSDictionary.class))
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
            }
        }

        @Override
        public String getName() {
            return "Tf";
        }
    }

    private void processAnnotation(PDAnnotation annotation) throws IOException {
        List<PDAppearanceEntry> appreaceEntries = ofNullable(annotation.getAppearance())
                .map(d -> d.getCOSObject().getValues()).filter(Objects::nonNull).orElse(Collections.emptyList())
                .stream().map(a -> a.getCOSObject()).map(PDAppearanceEntry::new).collect(Collectors.toList());
        for (PDAppearanceEntry entry : appreaceEntries) {
            if (entry.isStream()) {
                processStream(entry.getAppearanceStream());
            } else {
                for (PDAppearanceStream stream : entry.getSubDictionary().values()) {
                    // TODO investigate this case with named dictionary
                    processStream(stream);
                }
            }
        }
    }

    @Override
    public void accept(PDPage page) {
        try {
            this.processPage(page);
            for (PDAnnotation annotation : page.getAnnotations()) {
                processAnnotation(annotation);
            }
        } catch (IOException e) {
            LOG.warn("Failed parse page, skipping and continuing with next.", e);
        }
    }

}
