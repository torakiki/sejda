/*
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.component;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;

import java.io.IOException;

import org.sejda.common.ComponentsUtility;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.exception.TaskPermissionsException;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.input.PdfMixInput.PdfMixInputProcessStatus;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.task.NotifiableTaskMetadata;
import org.sejda.sambox.pdmodel.PDPageTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component providing functionalities to perform an alternate mix on two {@link PdfMixInput}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfAlternateMixer extends PDDocumentHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PdfAlternateMixer.class);

    private PdfMixInput firstInput;
    private PdfMixInput secondInput;
    private PDDocumentHandler firstDocumentHandler;
    private PDDocumentHandler secondDocumentHandler;
    private PdfSourceOpener<PDDocumentHandler> documentLoader = new DefaultPdfSourceOpener();

    public PdfAlternateMixer(PdfMixInput firstInput, PdfMixInput secondInput) {
        this.firstInput = firstInput;
        this.secondInput = secondInput;
    }

    /**
     * Perform the alternate mix on the given {@link PdfMixInput}s.
     * 
     * @param taskMetadata
     *            metadata of the task executing the mix.
     * @throws TaskException
     */
    @SuppressWarnings("unchecked")
    public void mix(NotifiableTaskMetadata taskMetadata) throws TaskException {
        firstDocumentHandler = openInput(firstInput);
        secondDocumentHandler = openInput(secondInput);
        setCreatorOnPDDocument();

        PDPageTree firstSourcePages = firstDocumentHandler.getUnderlyingPDDocument().getDocumentCatalog()
                .getPages();
        PDPageTree secondSourcePages = secondDocumentHandler.getUnderlyingPDDocument().getDocumentCatalog()
                .getPages();

        PdfMixInputProcessStatus firstDocStatus = firstInput.newProcessingStatus(firstSourcePages.getCount());
        PdfMixInputProcessStatus secondDocStatus = secondInput.newProcessingStatus(secondSourcePages.getCount());

        int currentStep = 0;
        int totalSteps = firstSourcePages.getCount() + secondSourcePages.getCount();
        while (firstDocStatus.hasNextPage() || secondDocStatus.hasNextPage()) {
            for (int i = 0; i < firstInput.getStep() && firstDocStatus.hasNextPage(); i++) {
                addPage(firstSourcePages.get(firstDocStatus.nextPage() - 1));
                notifyEvent(taskMetadata).stepsCompleted(++currentStep).outOf(totalSteps);
            }
            for (int i = 0; i < secondInput.getStep() && secondDocStatus.hasNextPage(); i++) {
                addPage(secondSourcePages.get(secondDocStatus.nextPage() - 1));
                notifyEvent(taskMetadata).stepsCompleted(++currentStep).outOf(totalSteps);
            }
        }

    }

    private PDDocumentHandler openInput(PdfMixInput input) throws TaskIOException, TaskPermissionsException {
        LOG.debug("Opening input {} ", input.getSource());
        PDDocumentHandler documentHandler = input.getSource().open(documentLoader);
        documentHandler.getPermissions().ensurePermission(PdfAccessPermission.ASSEMBLE);
        return documentHandler;
    }

    @Override
    public void close() throws IOException {
        super.close();
        ComponentsUtility.nullSafeCloseQuietly(firstDocumentHandler);
        ComponentsUtility.nullSafeCloseQuietly(secondDocumentHandler);
    }

}
