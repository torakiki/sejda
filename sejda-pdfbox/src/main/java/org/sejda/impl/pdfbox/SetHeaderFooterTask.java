/*
 * Created on 02/nov/2010
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.pdfbox;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import java.io.File;

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.pdfbox.component.DefaultPdfSourceOpener;
import org.sejda.impl.pdfbox.component.PDDocumentHandler;
import org.sejda.impl.pdfbox.component.PdfHeaderFooterWriter;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SetHeaderFooterParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PDFBox implementation of a task adding header or footer labels to pages
 * 
 * @author Eduard Weissmann
 * 
 */
public class SetHeaderFooterTask extends BaseTask<SetHeaderFooterParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetHeaderFooterTask.class);

    private int totalSteps;
    private PDDocumentHandler documentHandler = null;
    private MultipleOutputWriter outputWriter;

    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    public void before(SetHeaderFooterParameters parameters) {
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.isOverwrite());
    }

    public void execute(SetHeaderFooterParameters parameters) throws TaskException {
        int currentStep = 0;

        for(PdfSource<?> source: parameters.getSourceList()) {
            currentStep++;

            LOG.debug("Opening {}", source);
            documentHandler = source.open(documentLoader);
            documentHandler.getPermissions().ensurePermission(PdfAccessPermission.MODIFY);
            documentHandler.setCreatorOnPDDocument();

            File tmpFile = createTemporaryPdfBuffer();
            LOG.debug("Created output on temporary buffer {}", tmpFile);

            documentHandler.setVersionOnPDDocument(parameters.getVersion());
            documentHandler.compressXrefStream(parameters.isCompress());

            PdfHeaderFooterWriter footerWriter = new PdfHeaderFooterWriter(documentHandler);
            footerWriter.write(parameters, currentStep);

            documentHandler.saveDecryptedPDDocument(tmpFile);

            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                    nameRequest().originalName(source.getName()).fileNumber(currentStep));
            outputWriter.addOutput(file(tmpFile).name(outName));

            nullSafeCloseQuietly(documentHandler);

            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        parameters.getOutput().accept(outputWriter);
    }

    public void after() {
        nullSafeCloseQuietly(documentHandler);
    }

}
