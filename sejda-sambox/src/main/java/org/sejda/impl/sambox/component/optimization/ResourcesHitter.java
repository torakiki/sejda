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

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.RequireUtils.require;
import static org.sejda.sambox.contentstream.operator.OperatorName.DRAW_OBJECT;
import static org.sejda.sambox.contentstream.operator.OperatorName.SET_FONT_AND_SIZE;
import static org.sejda.sambox.contentstream.operator.OperatorName.SET_GRAPHICS_STATE_PARAMS;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sejda.impl.sambox.component.ContentStreamProcessor;
import org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream;
import org.sejda.sambox.contentstream.operator.MissingOperandException;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.OperatorName;
import org.sejda.sambox.contentstream.operator.OperatorProcessor;
import org.sejda.sambox.contentstream.operator.color.SetNonStrokingColorSpace;
import org.sejda.sambox.contentstream.operator.color.SetStrokingColorSpace;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.cos.IndirectCOSObjectIdentifier;
import org.sejda.sambox.pdmodel.MissingResourceException;
import org.sejda.sambox.pdmodel.font.PDType3CharProc;
import org.sejda.sambox.pdmodel.font.PDType3Font;
import org.sejda.sambox.pdmodel.graphics.PDXObject;
import org.sejda.sambox.pdmodel.graphics.color.PDColorSpace;
import org.sejda.sambox.pdmodel.graphics.color.PDPattern;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.graphics.form.PDTransparencyGroup;
import org.sejda.sambox.pdmodel.graphics.pattern.PDTilingPattern;
import org.sejda.sambox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that parses the page content steam and the page annotations appearance stream, wraps any image xobject (type xobject, subtype image) found in an instance of
 * {@link ReadOnlyFilteredCOSStream}, every font and every extended graphic state in an instance of {@link InUseDictionary} and puts them back into the resource dictionary. It's
 * later easy to identify xobjects, fonts and extgstate in use by the page/s and what can be discarded.
 * 
 * @author Andrea Vacondio
 */
public class ResourcesHitter extends ContentStreamProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ResourcesHitter.class);

    public ResourcesHitter() {
        addOperator(new XObjectHitterOperator());
        addOperator(new FontsHitterOperator());
        addOperator(new SetGraphicState());
        addOperator(new SetNonStrokingColorSpace());
        addOperator(new SetStrokingColorSpace());
        addOperator(new TilingPatternHitterSetStrokingColor(OperatorName.STROKING_COLOR_N));
        addOperator(new TilingPatternHitterSetNonStrokingColor(OperatorName.NON_STROKING_COLOR_N));
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
                        .map(r -> r.getCOSObject().getDictionaryObject(COSName.XOBJECT, COSDictionary.class));

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
            return DRAW_OBJECT;
        }
    }

    /**
     * Tf operator that wraps a font dictionary with an {@link InUseDictionary} and puts it back to the resource dictionary so that we can later identify fonts that are actually
     * used
     * 
     * @author Andrea Vacondio
     */
    public static class FontsHitterOperator extends OperatorProcessor {

        private final Map<IndirectCOSObjectIdentifier, InUseDictionary> hitFontsById = new HashMap<>();

        @Override
        public void process(Operator operator, List<COSBase> operands) throws IOException {
            if (operands.size() < 2) {
                throw new MissingOperandException(operator, operands);
            }
            COSBase operand = operands.get(0);
            if (operand instanceof COSName) {
                COSName fontName = (COSName) operand;
                Optional<COSDictionary> fonts = ofNullable(getContext().getResources())
                        .map(r -> r.getCOSObject().getDictionaryObject(COSName.FONT, COSDictionary.class));

                COSDictionary fontDictionary = fonts.map(d -> d.getDictionaryObject(fontName, COSDictionary.class))
                        .orElseThrow(() -> new MissingResourceException(
                                "Font resource '" + fontName.getName() + "' missing or unexpected type"));

                if (!(fontDictionary instanceof InUseDictionary)) {

                    // we wrap the existing so we can identify it later as "in use" and already processed
                    if (fontDictionary.hasId()) {
                        LOG.trace("Hit font with name {} id {}", fontName.getName(), fontDictionary.id());
                        // we wrap reuse the InUseDictionary if we hit it before
                        fonts.get().setItem(fontName,
                                ofNullable(hitFontsById.get(fontDictionary.id())).orElseGet(() -> {
                                    InUseDictionary font = new InUseDictionary(fontDictionary);
                                    hitFontsById.put(fontDictionary.id(), font);
                                    return font;
                                }));
                    } else {
                        // not even sure we can have a font that's not an indirect ref (so without id), anyway better safe then sorry
                        LOG.trace("Hit font with name {}", fontName.getName());
                        fonts.get().setItem(fontName, new InUseDictionary(fontDictionary));
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
            return SET_FONT_AND_SIZE;
        }
    }

    public static class SetGraphicState extends OperatorProcessor {
        // reuse the same dictionary in case the same indirect ref is used in two resource dictionaries in the original document
        private final Map<IndirectCOSObjectIdentifier, InUseDictionary> hitGSById = new HashMap<>();

        @Override
        public void process(Operator operator, List<COSBase> operands) throws IOException {

            require(!operands.isEmpty(), () -> new MissingOperandException(operator, operands));

            COSBase operand = operands.get(0);
            if (operand instanceof COSName) {
                COSName gsName = (COSName) operand;

                Optional<COSDictionary> states = ofNullable(getContext().getResources())
                        .map(r -> r.getCOSObject().getDictionaryObject(COSName.EXT_G_STATE, COSDictionary.class));

                COSDictionary gsDictionary = states.map(d -> d.getDictionaryObject(gsName, COSDictionary.class))
                        .orElseGet(() -> {
                            LOG.warn("Graphic state resource '{}' missing or unexpected type", gsName.getName());
                            return null;
                        });

                if (nonNull(gsDictionary)) {

                    new PDExtendedGraphicsState(gsDictionary).copyIntoGraphicsState(getContext().getGraphicsState());
                    if (!(gsDictionary instanceof InUseDictionary)) {
                        // we wrap the existing so we can identify it later as "in use" and already processed
                        if (gsDictionary.hasId()) {
                            LOG.trace("Hit ExtGState with name {} id {}", gsName.getName(), gsDictionary.id());
                            // we wrap reuse the InUseFont if we hit it before
                            states.get().setItem(gsName, ofNullable(hitGSById.get(gsDictionary.id())).orElseGet(() -> {
                                InUseDictionary gs = new InUseDictionary(gsDictionary);
                                hitGSById.put(gsDictionary.id(), gs);
                                return gs;
                            }));
                        } else {
                            // not an indirect ref (so without id)
                            LOG.trace("Hit ExtGState with name {}", gsName.getName());
                            states.get().setItem(gsName, new InUseDictionary(gsDictionary));
                        }

                        // ExtGState can contain a softmask dictionary in the form of a transparency group XObject. If present we process its stream to hit used resources
                        Optional<COSStream> softMask = ofNullable(
                                gsDictionary.getDictionaryObject(COSName.SMASK, COSDictionary.class))
                                        .map(d -> d.getDictionaryObject(COSName.G, COSStream.class))
                                        .filter(s -> COSName.FORM.getName().equals(s.getNameAsString(COSName.SUBTYPE)));
                        if (softMask.isPresent()) {
                            PDXObject xobject = PDXObject.createXObject(softMask.get(), getContext().getResources());
                            // should always be transparency
                            if (xobject instanceof PDTransparencyGroup) {
                                getContext().showTransparencyGroup((PDTransparencyGroup) xobject);
                            } else if (xobject instanceof PDFormXObject) {
                                getContext().showForm((PDFormXObject) xobject);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public String getName() {
            return SET_GRAPHICS_STATE_PARAMS;
        }
    }

    /**
     * Set Color operator that considers only tiling pattern colors and processes the corresponding pattern color space stream
     * 
     * @author Andrea Vacondio
     */
    abstract static class BaseTilingPatternHitterSetColor extends OperatorProcessor {

        private final String name;

        BaseTilingPatternHitterSetColor(String name) {
            this.name = name;
        }

        @Override
        public void process(Operator operator, List<COSBase> arguments) throws IOException {
            if (nonNull(arguments) && !arguments.isEmpty()) {
                PDColorSpace colorSpace = this.colorSpace();
                if (colorSpace instanceof PDPattern) {
                    COSBase base = arguments.get(arguments.size() - 1);
                    if (base instanceof COSName) {
                        COSName patternName = (COSName) base;
                        COSStream pattern = ofNullable(getContext().getResources())
                                .map(r -> r.getCOSObject().getDictionaryObject(COSName.PATTERN, COSDictionary.class))
                                .map(d -> d.getDictionaryObject(patternName, COSStream.class)).orElse(null);
                        // it's a pattern and it's a stream, it should be a tiling pattern, type == 1
                        if (nonNull(pattern) && pattern.getInt(COSName.PATTERN_TYPE) == 1) {
                            LOG.trace("Hit tiling pattern with name {}", patternName.getName());
                            getContext().processStream(new PDTilingPattern(pattern));
                        }
                    }
                }
            }
        }

        @Override
        public String getName() {
            return name;
        }

        abstract PDColorSpace colorSpace();
    }

    public static class TilingPatternHitterSetStrokingColor extends BaseTilingPatternHitterSetColor {

        public TilingPatternHitterSetStrokingColor(String name) {
            super(name);
        }

        @Override
        PDColorSpace colorSpace() {
            return this.getContext().getGraphicsState().getStrokingColorSpace();
        }
    }

    public static class TilingPatternHitterSetNonStrokingColor extends BaseTilingPatternHitterSetColor {

        public TilingPatternHitterSetNonStrokingColor(String name) {
            super(name);
        }

        @Override
        PDColorSpace colorSpace() {
            return this.getContext().getGraphicsState().getNonStrokingColorSpace();
        }
    }
}
