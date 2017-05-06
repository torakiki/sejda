/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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
package org.sejda.impl.sambox.component.split;

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import java.io.File;

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.core.support.util.HumanReadableSize;
import org.sejda.impl.sambox.component.PagesExtractor;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.parameter.base.AbstractPdfOutputParameters;
import org.sejda.model.split.NextOutputStrategy;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract component providing a skeletal implementation of the split execution.
 * 
 * @author Andrea Vacondio
 * @param <T>
 *            the type of parameters the splitter needs to have all the information necessary to perform the split.
 */
public abstract class AbstractPdfSplitter<T extends AbstractPdfOutputParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPdfSplitter.class);

    private PDDocument document;
    private T parameters;
    private int totalPages;
    private MultipleOutputWriter outputWriter;
    private boolean optimize = false;
    private boolean discardOutline = false;

    public AbstractPdfSplitter(PDDocument document, T parameters, boolean optimize, boolean discardOutline) {
        this.document = document;
        this.parameters = parameters;
        this.totalPages = document.getNumberOfPages();
        this.optimize = optimize;
        this.discardOutline = discardOutline;
    }

    public void split(TaskExecutionContext executionContext, String outputPrefix, PdfSource<?> source) throws TaskException {
        nextOutputStrategy().ensureIsValid();

        this.outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy(),
                executionContext);
        try (PagesExtractor extractor = supplyPagesExtractor(document)) {
            File tmpFile = null;
            for (int page = 1; page <= totalPages; page++) {
                executionContext.assertTaskNotCancelled();
                if (nextOutputStrategy().isOpening(page)) {
                    LOG.debug("Starting split at page {} of the original document", page);
                    onOpen(page);
                    tmpFile = createTemporaryBuffer(parameters.getOutput());
                    LOG.debug("Created output temporary buffer {}", tmpFile);
                    String outName = nameGenerator(outputPrefix).generate(enrichNameGenerationRequest(
                            nameRequest().page(page).originalName(source.getName())
                                    .fileNumber(executionContext.incrementAndGetOutputDocumentsCounter())));
                    outputWriter.addOutput(file(tmpFile).name(outName));
                }
                LOG.trace("Retaining page {} of the original document", page);
                onRetain(page);
                extractor.retain(page, executionContext);
                notifyEvent(executionContext.notifiableTaskMetadata()).stepsCompleted(page).outOf(totalPages);
                if (nextOutputStrategy().isClosing(page) || page == totalPages) {
                    onClose(page);
                    extractor.setVersion(parameters.getVersion());
                    extractor.setCompress(parameters.isCompress());
                    if (optimize) {
                        extractor.optimize();
                    }
                    extractor.save(tmpFile, discardOutline);
                    extractor.reset();
                    LOG.debug("Ending split at page {} of the original document, generated document size is {}", page,
                            HumanReadableSize.toString(tmpFile.length()));
                }
            }
        }
        parameters.getOutput().accept(outputWriter);
    }

    abstract NameGenerationRequest enrichNameGenerationRequest(NameGenerationRequest request);

    /**
     * @return the strategy to use to know if it's time to open a new document or close the current one.
     */
    abstract NextOutputStrategy nextOutputStrategy();

    /**
     * Called when an output document is going to be opened. Extending classes can plug some logic here.
     * 
     * @param page
     *            the page number which is going to be added
     */
    protected void onOpen(int page) throws TaskException {
        // nothing
    }

    /**
     * Called when the given page is going to be added . Extending classes can plug some logic here.
     * 
     * @param page
     *            the page number which is going to be added
     */
    protected void onRetain(int page) throws TaskException {
        // nothing
    }

    /**
     * Called when an output document is going to be closed. Extending classes can plug some logic here.
     * 
     * @param page
     *            the last added page number
     */
    protected void onClose(int page) throws TaskException {
        // nothing
    }

    /**
     * Creates the {@link PagesExtractor} to be used by this {@link AbstractPdfSplitter}
     */
    protected PagesExtractor supplyPagesExtractor(PDDocument document) {
        return new PagesExtractor(document);
    }
}
