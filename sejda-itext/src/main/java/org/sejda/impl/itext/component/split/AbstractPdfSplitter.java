/*
 * Created on 02/jul/2011
 *
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.itext.component.split;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.impl.itext.component.ITextOutlineSubsetProvider;
import org.sejda.impl.itext.component.PdfCopier;
import org.sejda.model.exception.TaskException;
import org.sejda.model.outline.OutlineSubsetProvider;
import org.sejda.model.parameter.base.SinglePdfSourceMultipleOutputParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.split.NextOutputStrategy;
import org.sejda.model.task.NotifiableTaskMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

/**
 * Abstract component providing a skeletal implementation of the split execution.
 * 
 * @author Andrea Vacondio
 * @param <T>
 *            the type of parameters the splitter needs to have all the information necessary to perform the split.
 */
abstract class AbstractPdfSplitter<T extends SinglePdfSourceMultipleOutputParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPdfSplitter.class);

    private PdfReader reader;
    private T parameters;
    private int totalPages;
    private OutlineSubsetProvider<List<Map<String, Object>>> outlineSubsetProvider;
    private MultipleOutputWriter outputWriter;

    /**
     * Creates a new splitter using the given reader.
     * 
     * @param reader
     */
    AbstractPdfSplitter(PdfReader reader, T parameters) {
        this.reader = reader;
        this.parameters = parameters;
        this.totalPages = reader.getNumberOfPages();
        this.outlineSubsetProvider = new ITextOutlineSubsetProvider(reader);
        this.outputWriter = OutputWriters.newMultipleOutputWriter(parameters.isOverwrite());
    }

    int getTotalNumberOfPages() {
        return totalPages;
    }

    public void split(NotifiableTaskMetadata taskMetadata) throws TaskException {
        nextOutputStrategy().ensureIsValid();
        PdfCopier pdfCopier = null;
        try {
            int outputDocumentsCounter = 0;
            for (int page = 1; page <= totalPages; page++) {
                if (nextOutputStrategy().isOpening(page)) {
                    LOG.debug("Starting split at page {} of the original document", page);
                    outputDocumentsCounter++;
                    pdfCopier = open(page, outputDocumentsCounter);
                }
                pdfCopier.addPage(reader, page);
                notifyEvent(taskMetadata).stepsCompleted(page).outOf(totalPages);
                if (nextOutputStrategy().isClosing(page) || page == totalPages) {
                    LOG.trace("Adding bookmarks to the temporary buffer");
                    pdfCopier.setOutline(outlineSubsetProvider.getOutlineUntillPage(page));
                    nullSafeCloseQuietly(pdfCopier);
                    LOG.debug("Ending split at page {} of the original document", page);
                }
            }
        } finally {
            nullSafeCloseQuietly(pdfCopier);
        }
        parameters.getOutput().accept(outputWriter);
    }

    private PdfCopier open(int page, int outputDocumentsCounter) throws TaskException {
        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {}", tmpFile);

        PdfCopier pdfCopier = openCopier(reader, tmpFile, parameters.getVersion());
        pdfCopier.setCompression(parameters.isCompress());

        String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                enrichNameGenerationRequest(nameRequest().page(page).originalName(parameters.getSource().getName())
                        .fileNumber(outputDocumentsCounter)));
        outputWriter.addOutput(file(tmpFile).name(outName));
        outlineSubsetProvider.startPage(page);
        return pdfCopier;
    }

    /**
     * @param request
     * @return the input request enriched by an splitter extending class with specific values.
     */
    abstract NameGenerationRequest enrichNameGenerationRequest(NameGenerationRequest request);

    abstract PdfCopier openCopier(PdfReader reader, File outputFile, PdfVersion version) throws TaskException;

    /**
     * @return the strategy to use to know if it's time to open a new document or close the current one.
     */
    abstract NextOutputStrategy nextOutputStrategy();

}
