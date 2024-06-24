package org.sejda.impl.sambox.component.pdfa;
/*
 * Created on 30/05/24
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
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;

import static java.util.Objects.nonNull;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;

/**
 * Rule 6.6.2 of ISO 19005-1: Trigger events.
 *
 * @author Andrea Vacondio
 */
public class NoAAEntry extends BaseRule<PDDocument, TaskException> {

    public NoAAEntry(ConversionContext conversionContext) {
        super(conversionContext);
    }

    @Override
    public void accept(PDDocument document) throws TaskExecutionException {
        if (nonNull(document.getDocumentCatalog().getCOSObject().getDictionaryObject(COSName.AA))) {
            conversionContext().maybeFailOnInvalidElement(() -> new TaskExecutionException(
                    "The document catalog dictionary shall not include an AA entry for an additional-actions dictionary"));
            document.getDocumentCatalog().getCOSObject().removeItem(COSName.AA);
            notifyEvent(conversionContext().notifiableMetadata()).taskWarning(
                    "Removed AA key from the document catalog");
        }
    }
}
