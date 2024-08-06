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

import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDPage;

import static java.util.Objects.nonNull;

/**
 * Rule 6.4 and 6.6.2 of ISO 19005-1, TECHNICAL CORRIGENDUM 2: A Group object with an S key with a value of Transparency shall not be included in a page dictionary
 *
 * @author Andrea Vacondio
 */
public class NoTransparencyGroupPageRule extends BaseRule<PDPage, TaskException> {

    public NoTransparencyGroupPageRule(ConversionContext conversionContext) {
        super(conversionContext);
    }

    @Override
    public void accept(PDPage page) throws TaskException {
        var group = page.getCOSObject().getDictionaryObject(COSName.GROUP, COSDictionary.class);
        if (nonNull(group) && COSName.TRANSPARENCY.equals(group.getCOSName(COSName.S))) {
            conversionContext().maybeRemoveForbiddenKeys(page.getCOSObject(), "page", s -> new TaskExecutionException(
                            "A Group object with an S key with a value of Transparency shall not be included in a page dictionary"),
                    COSName.GROUP);
        }
    }
}
