/*
 * Created on 23/07/24
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
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.pdmodel.graphics.pattern.PDTilingPattern;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.sejda.sambox.contentstream.operator.OperatorName.NON_STROKING_COLOR_N;
import static org.sejda.sambox.contentstream.operator.OperatorName.STROKING_COLOR_N;

/**
 * Set color operator that detects if the color set is a tiling pattern and in case process its stream.
 * This operator doesn't use the colorspaces in the context graphic state so that we don't need to set them in the graphic state.
 * Setting the colorpsace with the cs and CS operator may involve colospace parsing and caching so we try to avoid it.
 *
 * @author Andrea Vacondio
 */
class TilingPatternSetColor extends OperatorProcessor {

    private final String name;

    TilingPatternSetColor(String name) {
        this.name = name;
    }

    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        //it's an array with at least 2 elements and the last is a name, it's a Pattern [c1... cn name]
        if (nonNull(arguments) && arguments.size() > 1 && arguments.getLast() instanceof COSName patternName) {
            var pattern = patternResources().getDictionaryObject(patternName, COSStream.class);
            // it's a pattern and it's a stream, it should be a tiling pattern, type == 1
            if (nonNull(pattern) && pattern.getInt(COSName.PATTERN_TYPE) == 1) {
                getContext().processStream(new PDTilingPattern(pattern));
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    private COSDictionary patternResources() {
        return getContext().getResources().getCOSObject()
                .computeIfAbsent(COSName.PATTERN, k -> new COSDictionary(), COSDictionary.class);
    }

    static OperatorProcessor tilingPatternSetStrokingColor() {
        return new TilingPatternSetColor(STROKING_COLOR_N);
    }

    static OperatorProcessor tilingPatternSetNonStrokingColor() {
        return new TilingPatternSetColor(NON_STROKING_COLOR_N);
    }
}
