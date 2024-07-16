/*
 * Created on 20/06/24
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

import java.io.IOException;

import static java.util.Objects.nonNull;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;

/**
 * Rule 6.1.11 of ISO 19005-1: A file specification dictionary shall not contain the EF key
 *
 * @author Andrea Vacondio
 */
public class NoEFDictionaryRule extends BaseCOSObjectRule<COSDictionary> {

    public NoEFDictionaryRule(ConversionContext conversionContext) {
        super(conversionContext);
    }

    @Override
    public void accept(COSDictionary dictionary) throws IOException {
        if (COSName.FILESPEC.equals(dictionary.getCOSName(COSName.TYPE))) {
            if (nonNull(dictionary.getDictionaryObject(COSName.EF, COSDictionary.class))) {
                conversionContext().maybeFailOnInvalidElement(
                        () -> new IOException("A file specification dictionary shall not contain the EF key"));
                dictionary.removeItem(COSName.EF);
                notifyEvent(conversionContext().notifiableMetadata()).taskWarning(
                        "Removed EF key from the name dictionary");
            }
        }
    }
}
