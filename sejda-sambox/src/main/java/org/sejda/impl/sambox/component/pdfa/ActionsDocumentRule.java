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
package org.sejda.impl.sambox.component.pdfa;

import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;

import static java.util.Objects.nonNull;

/**
 * Rule 6.6.1 of ISO 19005-1: Some actions types and named actions are not permitted.
 *
 * @author Andrea Vacondio
 */
public class ActionsDocumentRule extends BaseRule<PDDocument, TaskException> {

    public ActionsDocumentRule(ConversionContext conversionContext) {
        super(conversionContext);
    }

    @Override
    public void accept(PDDocument document) throws TaskExecutionException {

        conversionContext().maybeRemoveForbiddenAction(document.getDocumentCatalog().getCOSObject(), "Catalog",
                COSName.OPEN_ACTION);
        var outline = document.getDocumentCatalog().getDocumentOutline();
        if (nonNull(outline)) {
            for (var item : outline.nodes()) {
                conversionContext().maybeRemoveForbiddenAction(item.getCOSObject(), "Outline item", COSName.A);
            }
        }
    }

}