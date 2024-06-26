package org.sejda.impl.sambox.component.pdfa;
/*
 * Created on 25/06/24
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

import org.sejda.model.exception.TaskException;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDPage;

import static java.util.Objects.nonNull;

/**
 * Rule 6.6.1 of ISO 19005-1: Some actions types and named actions are not permitted.
 *
 * @author Andrea Vacondio
 */
public class ActionsPageRule extends BaseRule<PDPage, TaskException> {

    public ActionsPageRule(ConversionContext conversionContext) {
        super(conversionContext);
    }

    @Override
    public void accept(PDPage page) throws TaskException {
        var actions = page.getCOSObject().getDictionaryObject(COSName.AA, COSDictionary.class);
        if (nonNull(actions)) {
            for (var key : actions.keySet()) {
                conversionContext().maybeRemoveForbiddenAction(actions, "Page additional-action", key);
            }
        }
    }
}
