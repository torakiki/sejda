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
package org.sejda.impl.sambox.component.optimization;

import org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.OperatorProcessor;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.cos.IndirectCOSObjectIdentifier;
import org.sejda.sambox.pdmodel.graphics.pattern.PDTilingPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.sejda.sambox.contentstream.operator.OperatorName.NON_STROKING_COLOR_N;
import static org.sejda.sambox.contentstream.operator.OperatorName.STROKING_COLOR_N;

/**
 * Set color operator that detects if the color set is a tiling pattern and in case process its stream.
 * This operator doesn't use the colorspaces in the context graphic state so that we don't need to set them in the graphic state.
 * Setting the colorspace with the cs and CS operator may involve colorspace parsing and caching, so we try to avoid it.
 *
 * @author Andrea Vacondio
 */
public class TilingPatternHitterOperator extends OperatorProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(TilingPatternHitterOperator.class);

    private final Map<IndirectCOSObjectIdentifier, ReadOnlyFilteredCOSStream> hitPatternsById;

    private final String name;

    public TilingPatternHitterOperator(String name,
            Map<IndirectCOSObjectIdentifier, ReadOnlyFilteredCOSStream> hitPatternsById) {
        this.name = name;
        this.hitPatternsById = ofNullable(hitPatternsById).orElseGet(HashMap::new);
    }

    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        //If the last element is a Name, it should be a Pattern, either pattername or [c1... cn pattername]
        if (nonNull(arguments) && arguments.getLast() instanceof COSName patternName) {
            var patterns = ofNullable(getContext().getResources()).map(
                    r -> r.getCOSObject().getDictionaryObject(COSName.PATTERN, COSDictionary.class));
            var pattern = patterns.map(d -> d.getDictionaryObject(patternName, COSStream.class)).orElse(null);
            // it's a pattern and it's a stream, it should be a tiling pattern, type == 1
            if (nonNull(pattern) && !(pattern instanceof ReadOnlyFilteredCOSStream)
                    && pattern.getInt(COSName.PATTERN_TYPE) == 1) {
                LOG.trace("Hit pattern with name {}", patternName.getName());
                var hitPattern = ReadOnlyFilteredCOSStream.readOnly(pattern);

                // we wrap the one found in the resource dictionary so we can identify it later as "in use" and already processed
                if (pattern.hasId()) {
                    var existingHit = hitPatternsById.putIfAbsent(pattern.id(), hitPattern);
                    if (nonNull(existingHit)) {
                        //we already hit the pattern in another resource dictionary
                        patterns.get().setItem(patternName, existingHit);
                    } else {
                        patterns.get().setItem(patternName, hitPattern);
                        getContext().processStream(new PDTilingPattern(pattern));
                    }
                } else {
                    //Streams must be indirect (with id) so this is a new COSStream that was added, not something read from the document
                    patterns.get().setItem(patternName, hitPattern);
                    getContext().processStream(new PDTilingPattern(pattern));
                }
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public static OperatorProcessor tilingPatternSetStrokingColor(
            Map<IndirectCOSObjectIdentifier, ReadOnlyFilteredCOSStream> hitPatternsById) {
        return new TilingPatternHitterOperator(STROKING_COLOR_N, hitPatternsById);
    }

    public static OperatorProcessor tilingPatternSetNonStrokingColor(
            Map<IndirectCOSObjectIdentifier, ReadOnlyFilteredCOSStream> hitPatternsById) {
        return new TilingPatternHitterOperator(NON_STROKING_COLOR_N, hitPatternsById);
    }
}
