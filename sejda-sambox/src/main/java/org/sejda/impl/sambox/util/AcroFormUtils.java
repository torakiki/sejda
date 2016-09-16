/*
 * Created on 16 set 2016
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
package org.sejda.impl.sambox.util;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.interactive.form.PDAcroForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods related to acroforms
 * 
 * @author Andrea Vacondio
 */
public final class AcroFormUtils {
    private static final Logger LOG = LoggerFactory.getLogger(AcroFormUtils.class);

    private AcroFormUtils() {
        // hide
    }

    /**
     * Merge default values of two acro form dictionaries
     * 
     * @param mergeThis
     * @param intoThis
     */
    public static void mergeDefaults(PDAcroForm mergeThis, PDAcroForm intoThis) {
        if (!intoThis.isNeedAppearances() && mergeThis.isNeedAppearances()) {
            intoThis.setNeedAppearances(true);
        }
        String da = mergeThis.getDefaultAppearance();
        if (isBlank(intoThis.getDefaultAppearance()) && !isBlank(da)) {
            intoThis.setDefaultAppearance(da);
        }
        int quadding = mergeThis.getCOSObject().getInt(COSName.Q);
        if ((quadding >= 0 && quadding <= 2) && !intoThis.getCOSObject().containsKey(COSName.Q)) {
            intoThis.setQuadding(quadding);
        }
        final COSDictionary formResources = ofNullable(
                intoThis.getCOSObject().getDictionaryObject(COSName.DR, COSDictionary.class))
                        .orElseGet(COSDictionary::new);
        ofNullable(mergeThis.getCOSObject().getDictionaryObject(COSName.DR, COSDictionary.class)).ifPresent(dr -> {
            for (COSName currentKey : dr.keySet()) {
                ofNullable(dr.getDictionaryObject(currentKey)).ifPresent(value -> {
                    if (value instanceof COSDictionary) {
                        mergeResourceDictionaryValue(formResources, (COSDictionary) value, currentKey);
                    } else if (value instanceof COSArray) {
                        mergeResourceArrayValue(formResources, (COSArray) value, currentKey);
                    } else {
                        LOG.warn("Unsupported resource dictionary type {}", value);
                    }
                });
            }
        });
        intoThis.getCOSObject().setItem(COSName.DR, formResources);
        LOG.debug("Merged AcroForm dictionary");
    }

    private static void mergeResourceArrayValue(COSDictionary formResources, COSArray value, COSName currentKey) {
        COSArray currentItem = ofNullable(formResources.getDictionaryObject(currentKey, COSArray.class))
                .orElseGet(COSArray::new);
        value.stream().filter(i -> !currentItem.contains(i)).forEach(currentItem::add);
        formResources.setItem(currentKey, currentItem);
    }

    private static void mergeResourceDictionaryValue(final COSDictionary formResources, COSDictionary value,
            COSName currentKey) {
        COSDictionary currentItem = ofNullable(formResources.getDictionaryObject(currentKey, COSDictionary.class))
                .orElseGet(COSDictionary::new);
        currentItem.mergeWithoutOverwriting(value);
        formResources.setItem(currentKey, currentItem);
    }
}
