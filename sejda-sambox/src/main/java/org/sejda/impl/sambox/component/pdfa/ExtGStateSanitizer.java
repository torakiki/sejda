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

import org.sejda.core.notification.dsl.ApplicationEventsNotifier;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class ExtGStateSanitizer {
    private final ConversionContext conversionContext;

    public ExtGStateSanitizer(ConversionContext conversionContext) {
        this.conversionContext = conversionContext;
    }

    void sanitizeExtGState(COSDictionary extGState) throws IOException {
        sanitizeCAValues(extGState);
        conversionContext.sanitizeRenderingIntents(extGState);
        conversionContext.maybeRemoveForbiddenKeys(extGState, "Extended graphics state", IOException::new, COSName.TR);
        var tr2 = extGState.getDictionaryObject(COSName.TR2);
        if (Objects.nonNull(tr2) && !COSName.getPDFName("Default").equals(tr2)) {
            conversionContext.maybeRemoveForbiddenKeys(extGState, "Extended graphics state", IOException::new,
                    COSName.TR2);
        }
        var smask = extGState.getDictionaryObject(COSName.SMASK);
        if (Objects.nonNull(smask) && !COSName.NONE.equals(smask)) {
            conversionContext.maybeRemoveForbiddenKeys(extGState, "Extended graphics state", IOException::new,
                    COSName.SMASK);
        }
        sanitizeBlendMode(extGState);
    }

    private void validateBlendMode(COSDictionary extGState, COSBase current) throws IOException {
        var valid = Set.of(COSName.NORMAL, COSName.COMPATIBLE);
        if (!valid.contains(current)) {
            conversionContext.maybeFailOnInvalidElement(
                    () -> new IOException("Found a blend mode in ExtGState with invalid value"));
            extGState.setItem(COSName.BM, COSName.NORMAL);
            ApplicationEventsNotifier.notifyEvent(conversionContext.notifiableMetadata())
                    .taskWarning("Invalid blend value " + current + " in ExtGState overridden with Normal");
        }
    }

    private void sanitizeCAValues(COSDictionary extGState) throws IOException {
        for (var ca : Arrays.asList(COSName.CA, COSName.CA_NS)) {
            float constantOpacity = extGState.getFloat(ca, 1.0f);
            if (constantOpacity != 1.0f) {
                conversionContext.maybeFailOnInvalidElement(
                        () -> new IOException("Found an alpha constant in ExtGState with invalid value"));
                extGState.setFloat(ca, 1.0f);
                ApplicationEventsNotifier.notifyEvent(conversionContext.notifiableMetadata()).taskWarning(
                        String.format("Invalid alpha constant value %f in ExtGState overridden with 1.0",
                                constantOpacity));
            }
        }
    }

    private void sanitizeBlendMode(COSDictionary extGState) throws IOException {
        var bm = extGState.getDictionaryObject(COSName.BM);
        if (bm instanceof COSName bmName) {
            validateBlendMode(extGState, bmName);
        }
        if (bm instanceof COSArray bmArray) {
            for (var current : bmArray) {
                validateBlendMode(extGState, current);
            }
        }
    }
}