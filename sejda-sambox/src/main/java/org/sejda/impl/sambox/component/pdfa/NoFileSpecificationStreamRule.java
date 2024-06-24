package org.sejda.impl.sambox.component.pdfa;
/*
 * Created on 19/06/24
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

import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSNull;
import org.sejda.sambox.cos.COSStream;

import java.io.IOException;

import static java.util.Objects.isNull;
import static org.sejda.commons.util.RequireUtils.requireIOCondition;

/**
 * Rule 6.1.7 of ISO 19005-1: A stream object dictionary shall not contain the F, FFilter, or FDecodeParams keys.
 * In case the value of the keys is {@link COSNull} we remove the key and consider the stream valid.
 *
 * @author Andrea Vacondio
 */
public class NoFileSpecificationStreamRule extends BaseCOSObjectRule<COSStream> {

    public NoFileSpecificationStreamRule(ConversionContext conversionContext) {
        super(conversionContext);
    }

    @Override
    public void accept(COSStream stream) throws IOException {
        var f = stream.getItem(COSName.F);
        requireIOCondition(isNull(f) || f instanceof COSNull, "A stream object dictionary shall not contain the F key");
        var ffilter = stream.getItem(COSName.F_FILTER);
        requireIOCondition(isNull(ffilter) || ffilter instanceof COSNull,
                "A stream object dictionary shall not contain the FFilter key");
        var fDecodeParams = stream.getItem(COSName.F_DECODE_PARMS);
        requireIOCondition(isNull(fDecodeParams) || fDecodeParams instanceof COSNull,
                "A stream object dictionary shall not contain the FDecodeParams key");

        stream.removeItems(COSName.F, COSName.F_FILTER, COSName.F_DECODE_PARMS);
    }
}
