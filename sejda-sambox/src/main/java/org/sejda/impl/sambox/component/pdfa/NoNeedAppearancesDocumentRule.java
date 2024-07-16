/*
 * Created on 24/06/24
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
import org.sejda.model.exception.TaskIOException;
import org.sejda.sambox.pdmodel.PDDocument;

import java.io.IOException;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;

/**
 * Rule 6.9 and 6.6.2 of ISO 19005-1: The NeedAppearances flag of the interactive form dictionary shall either not be present or shall be false
 *
 * @author Andrea Vacondio
 */
public class NoNeedAppearancesDocumentRule extends BaseRule<PDDocument, TaskException> {

    public NoNeedAppearancesDocumentRule(ConversionContext conversionContext) {
        super(conversionContext);
    }

    @Override
    public void accept(PDDocument document) throws TaskException {
        var form = document.getDocumentCatalog().getAcroForm();
        if (form.isNeedAppearances()) {
            conversionContext().maybeFailOnInvalidElement(() -> new TaskExecutionException(
                    "The NeedAppearances flag of the interactive form dictionary shall either not be present or shall be false"));
            try {
                form.refreshAppearances();
            } catch (IOException e) {
                throw new TaskIOException("Unable to refresh form fields appearance", e);
            }
            form.setNeedAppearances(false);
            notifyEvent(conversionContext().notifiableMetadata()).taskWarning("Regenerated form appearance");
        }
    }
}
