/*
 * Created on 17/07/24
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

import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;

import java.io.IOException;

import static java.util.Objects.nonNull;
import static org.sejda.commons.util.RequireUtils.require;

/**
 * Rule 6.4 of ISO 19005-1: A Group object with an S key with a value of Transparency shall not be included in a form XObject.
 *
 * @author Andrea Vacondio
 */
public class NoTransparencyGroupStreamRule extends BaseCOSObjectRule<COSStream> {

    public NoTransparencyGroupStreamRule(ConversionContext conversionContext) {
        super(conversionContext);
    }

    @Override
    public void accept(COSStream stream) throws IOException {
        if (COSName.XOBJECT.equals(stream.getCOSName(COSName.TYPE)) && COSName.FORM.equals(
                stream.getCOSName(COSName.SUBTYPE))) {
            var group = stream.getDictionaryObject(COSName.GROUP, COSDictionary.class);
            if (nonNull(group)) {
                require(!COSName.TRANSPARENCY.equals(group.getCOSName(COSName.S)), () -> new IOException(
                        "A Group object with an S key with a value of Transparency shall not be included in a form XObject"));
            }
        }
    }
}
