/*
 * Created on 12/08/24
 * Copyright 2024 Sober Lemur S.r.l. and Sejda BV
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
package org.sejda.impl.sambox.component.pdfa;

import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.OperatorProcessor;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.sejda.sambox.contentstream.operator.OperatorName.NON_STROKING_COLOR_N;
import static org.sejda.sambox.contentstream.operator.OperatorName.STROKING_COLOR_N;

/**
 * @author Andrea Vacondio
 */
public class ShadingPatternSetColor extends OperatorProcessor {

    private final ConversionContext conversionContext;
    private final String name;

    public ShadingPatternSetColor(String name, ConversionContext conversionContext) {
        this.name = name;
        this.conversionContext = conversionContext;
    }

    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        //If the last element is a Name, it should be a Pattern, either pattername or [c1... cn pattername]
        if (nonNull(arguments) && arguments.getLast() instanceof COSName patternName) {
            var patterns = ofNullable(getContext().getResources()).map(
                    r -> r.getCOSObject().getDictionaryObject(COSName.PATTERN, COSDictionary.class));
            var shading = patterns.map(d -> d.getDictionaryObject(patternName, COSDictionary.class)).orElse(null);
            // it's a pattern and it's a dictionary and type == 2
            if (nonNull(shading) && shading.getInt(COSName.PATTERN_TYPE) == 2) {
                var extGState = shading.getDictionaryObject(COSName.EXT_G_STATE, COSDictionary.class);
                if (nonNull(extGState)) {
                    new ExtGStateSanitizer(conversionContext).sanitizeExtGState(extGState);
                }
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public static OperatorProcessor shadingPatternSetStrokingColor(ConversionContext conversionContext) {
        return new ShadingPatternSetColor(STROKING_COLOR_N, conversionContext);
    }

    public static OperatorProcessor shadingPatternSetNonStrokingColor(ConversionContext conversionContext) {
        return new ShadingPatternSetColor(NON_STROKING_COLOR_N, conversionContext);
    }
}
