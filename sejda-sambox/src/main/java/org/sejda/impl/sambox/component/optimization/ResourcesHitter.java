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

import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sejda.impl.sambox.component.ContentStreamProcessor;
import org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream;
import org.sejda.sambox.contentstream.operator.MissingOperandException;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.OperatorProcessor;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.cos.IndirectCOSObjectIdentifier;
import org.sejda.sambox.pdmodel.MissingResourceException;
import org.sejda.sambox.pdmodel.font.PDType3CharProc;
import org.sejda.sambox.pdmodel.font.PDType3Font;
import org.sejda.sambox.pdmodel.graphics.PDXObject;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.graphics.form.PDTransparencyGroup;
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
public class ResourcesHitter extends ContentStreamProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ResourcesHitter.class);

    public ResourcesHitter() {
        addOperator(new XObjectHitterOperator());
        addOperator(new FontsHitterOperator());
    }

    public static class XObjectHitterOperator extends OperatorProcessor {
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

    /**
     * Tf operator that wraps a font dictionary with an {@link InUseFontDictionary} and puts it back to the resource dictionary so that we can later identify fonts that are
     * actually used
     * 
     * @author Andrea Vacondio
     *
     */
    public static class FontsHitterOperator extends OperatorProcessor {

        private final Map<IndirectCOSObjectIdentifier, InUseFontDictionary> hitFontsById = new HashMap<>();

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

                COSDictionary fontDictionary = fonts.map(d -> d.getDictionaryObject(fontName, COSDictionary.class))
                        .orElseThrow(() -> new MissingResourceException(
                                "Font resource '" + fontName.getName() + "' missing or unexpected type"));

                if (!(fontDictionary instanceof InUseFontDictionary)) {

                    // we wrap the existing so we can identify it later as "in use" and already processed
                    if (fontDictionary.hasId()) {
                        LOG.trace("Hit font with name {} id {}", fontName.getName(), fontDictionary.id().toString());
                        // we wrap reuse the InUseFont if we hit it before
                        fonts.get().setItem(fontName,
                                ofNullable(hitFontsById.get(fontDictionary.id())).orElseGet(() -> {
                                    InUseFontDictionary font = new InUseFontDictionary(fontDictionary);
                                    hitFontsById.put(fontDictionary.id(), font);
                                    return font;
                                }));
                    } else {
                        // not even sure we can have a font that's not an indirect ref (so without id), anyway better safe then sorry
                        LOG.trace("Hit font with name {}", fontName.getName());
                        fonts.get().setItem(fontName, new InUseFontDictionary(fontDictionary));
                    }

                    // type 3 fonts glyphs are content stream and they may refer to named resource.
                    // If the font resource dictionary is not present the page resource dictionary is used instead AND
                    // we cannot exclude the font resource is an indirect ref to the page resource dictionary
                    // => so we have to make sure those resource are hit
                    if (COSName.TYPE3.equals(fontDictionary.getCOSName(COSName.SUBTYPE))) {
                        PDType3Font font = new PDType3Font(fontDictionary);
                        Collection<COSBase> glyphStreams = ofNullable(
                                fontDictionary.getDictionaryObject(COSName.CHAR_PROCS, COSDictionary.class))
                                        .map(chars -> chars.getValues()).filter(v -> !v.isEmpty())
                                        .orElseGet(Collections::emptyList);
                        List<PDType3CharProc> pdStreams = glyphStreams.stream().map(COSBase::getCOSObject)
                                .filter(s -> s instanceof COSStream).map(s -> (COSStream) s)
                                .map(s -> new PDType3CharProc(font, s)).collect(Collectors.toList());
                        LOG.trace("Found type3 font {} with {} streams to parse", fontName.getName(), pdStreams.size());
                        for (PDType3CharProc glyph : pdStreams) {
                            getContext().processStream(glyph);
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
}
