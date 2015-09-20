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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.component;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.impl.sambox.component.Annotations.processAnnotations;
import static org.sejda.impl.sambox.component.SignatureClipper.clipSignatures;

import java.io.IOException;

import org.sejda.common.ComponentsUtility;
import org.sejda.common.LookupTable;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.exception.TaskPermissionsException;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.input.PdfMixInput.PdfMixInputProcessStatus;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.task.NotifiableTaskMetadata;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
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
    public void mix(NotifiableTaskMetadata taskMetadata) throws TaskException {
        firstDocumentHandler = openInput(firstInput);
        secondDocumentHandler = openInput(secondInput);
        setCreatorOnPDDocument();

        int firstDocPages = firstDocumentHandler.getNumberOfPages();
        int secondDocPages = secondDocumentHandler.getNumberOfPages();

        PdfMixInputProcessStatus firstDocStatus = firstInput.newProcessingStatus(firstDocPages);
        PdfMixInputProcessStatus secondDocStatus = secondInput.newProcessingStatus(secondDocPages);

        int currentStep = 0;
        int totalSteps = firstDocPages + secondDocPages;
        LookupTable<PDPage> lookupFirst = new LookupTable<>();
        LookupTable<PDPage> lookupSecond = new LookupTable<>();
        while (firstDocStatus.hasNextPage() || secondDocStatus.hasNextPage()) {
            for (int i = 0; i < firstInput.getStep() && firstDocStatus.hasNextPage(); i++) {
                PDPage current = firstDocumentHandler.getPage(firstDocStatus.nextPage());
                lookupFirst.addLookupEntry(current, importPage(current));
                notifyEvent(taskMetadata).stepsCompleted(++currentStep).outOf(totalSteps);
            }
            for (int i = 0; i < secondInput.getStep() && secondDocStatus.hasNextPage(); i++) {
                PDPage current = secondDocumentHandler.getPage(secondDocStatus.nextPage());
                lookupSecond.addLookupEntry(current, importPage(current));
                notifyEvent(taskMetadata).stepsCompleted(++currentStep).outOf(totalSteps);
            }
        }
        LookupTable<PDAnnotation> annotationsLookup = processAnnotations(lookupFirst,
                firstDocumentHandler.getUnderlyingPDDocument());
        clipSignatures(annotationsLookup.values());
        annotationsLookup = processAnnotations(lookupSecond, secondDocumentHandler.getUnderlyingPDDocument());
        clipSignatures(annotationsLookup.values());
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
