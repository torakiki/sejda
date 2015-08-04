/*
 * Created on 15/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
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
