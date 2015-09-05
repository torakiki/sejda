/*
 * Created on Jul 11, 2011
 * Copyright 2011 by Nero Couvalli (angelthepunisher@gmail.com).
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
package org.sejda.impl.pdfbox;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;

import java.io.File;
import java.util.Map.Entry;

import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.pdfbox.component.DefaultPdfSourceOpener;
import org.sejda.impl.pdfbox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SetMetadataParameters;
import org.sejda.model.pdf.PdfMetadataKey;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PDFBox implementation of a task setting metadata on an input {@link PdfSource}.
 * 
 * @author Nero Couvalli
 * 
 */
public class SetMetadataTask extends BaseTask<SetMetadataParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetMetadataTask.class);

    private PDDocumentHandler documentHandler = null;
    private SingleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    @Override
    public void before(SetMetadataParameters parameters) {
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.isOverwrite());
    }

    @Override
    public void execute(SetMetadataParameters parameters) throws TaskException {
        notifyEvent(getNotifiableTaskMetadata()).progressUndetermined();

        PdfSource<?> source = parameters.getSource();
        LOG.debug("Opening {}", source);
        documentHandler = source.open(documentLoader);
        documentHandler.setCreatorOnPDDocument();

        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {}", tmpFile);

        LOG.debug("Setting metadata on temporary document.");
        PDDocumentInformation actualMeta = documentHandler.getUnderlyingPDDocument().getDocumentInformation();
        for (Entry<PdfMetadataKey, String> meta : parameters.entrySet()) {
            LOG.trace("'{}' -> '{}'", meta.getKey().getKey(), meta.getValue());
            actualMeta.setCustomMetadataValue(meta.getKey().getKey(), meta.getValue());
        }

        documentHandler.setVersionOnPDDocument(parameters.getVersion());
        documentHandler.compressXrefStream(parameters.isCompress());
        documentHandler.saveDecryptedPDDocument(tmpFile);
        nullSafeCloseQuietly(documentHandler);

        outputWriter.setOutput(file(tmpFile).name(parameters.getOutputName()));
        parameters.getOutput().accept(outputWriter);

        LOG.debug("Metadata set on {}", parameters.getOutput());

    }

    @Override
    public void after() {
        nullSafeCloseQuietly(documentHandler);
    }

}