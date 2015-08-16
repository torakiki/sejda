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
package org.sejda.impl.sambox;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import java.io.File;

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.PdfTextExtractor;
import org.sejda.model.SejdaFileExtensions;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.ExtractTextParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAMBox implementation of a task extracting text from a list of {@link PdfSource}
 * 
 * @author Andrea Vacondio
 */
public class ExtractTextTask extends BaseTask<ExtractTextParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(ExtractTextTask.class);

    private int totalSteps;
    private PDDocumentHandler documentHandler = null;
    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;
    private PdfTextExtractor textExtractor;

    public void before(ExtractTextParameters parameters) throws TaskException {
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
        textExtractor = new PdfTextExtractor(parameters.getTextEncoding());
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.isOverwrite());
    }

    public void execute(ExtractTextParameters parameters) throws TaskException {
        int currentStep = 0;
        for (PdfSource<?> source : parameters.getSourceList()) {
            currentStep++;
            LOG.debug("Opening {}", source);
            documentHandler = source.open(documentLoader);
            documentHandler.getPermissions().ensurePermission(PdfAccessPermission.COPY_AND_EXTRACT);

            File tmpFile = createTemporaryBuffer();
            LOG.debug("Created output on temporary buffer {}", tmpFile);

            textExtractor.extract(documentHandler.getUnderlyingPDDocument(), tmpFile);
            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                    nameRequest(SejdaFileExtensions.TXT_EXTENSION).originalName(source.getName()).fileNumber(
                            currentStep));
            outputWriter.addOutput(file(tmpFile).name(outName));

            closeResources();

            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Text extracted from input documents and written to {}", parameters.getOutput());

    }

    public void after() {
        closeResources();
    }

    private void closeResources() {
        nullSafeCloseQuietly(documentHandler);
        nullSafeCloseQuietly(textExtractor);
    }
}
