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
import org.sejda.sambox.contentstream.operator.OperatorName;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.sejda.commons.util.RequireUtils.requireIOCondition;

/**
 * @author Andrea Vacondio
 */
public class InlineImageOperator extends PdfAContentStreamOperator {

    private static final Logger LOG = LoggerFactory.getLogger(InlineImageOperator.class);

    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        var dictionary = operator.getImageParameters();
        if (nonNull(dictionary)) {
            conversionContext().maybeAddDefaultColorSpaceFor(
                    dictionary.getDictionaryObject(COSName.CS, COSName.COLORSPACE), csResources());
            COSBase filters = dictionary.getDictionaryObject(COSName.F, COSName.FILTER);
            //Rule 6.1.10 of ISO 19005-1: The LZWDecode filter shall not be permitted.
            //We don't convert inline images to FlateDecode at the moment
            if (filters instanceof COSName filterName) {
                requireIOCondition(
                        !(COSName.LZW_DECODE.equals(filterName) || COSName.LZW_DECODE_ABBREVIATION.equals(filterName)),
                        "The LZWDecode filter is not permitted");
            }
            if (filters instanceof COSArray filtersArray) {
                requireIOCondition(!(filtersArray.contains(COSName.LZW_DECODE) || filtersArray.contains(
                        COSName.LZW_DECODE_ABBREVIATION)), "The LZWDecode filter is not permitted");

            }
        }
    }

    @Override
    public String getName() {
        return OperatorName.BEGIN_INLINE_IMAGE;
    }

    private COSDictionary csResources() {
        return getContext().getResources().getCOSObject()
                .computeIfAbsent(COSName.COLORSPACE, k -> new COSDictionary(), COSDictionary.class);
    }
}
