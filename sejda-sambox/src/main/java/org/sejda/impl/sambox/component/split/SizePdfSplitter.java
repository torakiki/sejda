/*
 * Created on 24/ago/2015
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.sambox.component.split;

import org.sejda.commons.util.IOUtils;
import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.impl.sambox.component.PagesExtractor;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.SplitBySizeParameters;
import org.sejda.model.split.NextOutputStrategy;
import org.sejda.sambox.output.ExistingPagesSizePredictor;
import org.sejda.sambox.output.WriteOption;
import org.sejda.sambox.pdmodel.PDDocument;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Splitter implementation that tries to split a document at roughly a given size
 * 
 * @author Andrea Vacondio
 */
public class SizePdfSplitter extends AbstractPdfSplitter<SplitBySizeParameters> {

    private static final WriteOption[] COMPRESSED_OPTS = new WriteOption[] { WriteOption.COMPRESS_STREAMS,
            WriteOption.XREF_STREAM };

    private static final int PDF_HEADER_SIZE = 15;
    // euristic trailer ID size
    private static final int ID_VALUE_SIZE = 70;
    // euristic overhead per page (ex. page ref in the page tree)
    private static final int PAGE_OVERHEAD = 10;

    private OutputSizeStrategy nextOutputStrategy;

    public SizePdfSplitter(PDDocument document, SplitBySizeParameters parameters, boolean optimize) {
        super(document, parameters, optimize, parameters.discardOutline());
        this.nextOutputStrategy = new OutputSizeStrategy(document, parameters, optimize);
    }

    @Override
    public NameGenerationRequest enrichNameGenerationRequest(NameGenerationRequest request) {
        return request;
    }

    @Override
    public NextOutputStrategy nextOutputStrategy() {
        return nextOutputStrategy;
    }

    @Override
    protected void onOpen(int page) throws TaskIOException {
        nextOutputStrategy.newPredictor();
        nextOutputStrategy.addPage(page);
    }

    @Override
    protected void onRetain(int page) throws TaskIOException {
        nextOutputStrategy.addPage(page + 1);
    }

    @Override
    protected void onClose(int page) {
        nextOutputStrategy.closePredictor();

    }

    @Override
    protected PagesExtractor supplyPagesExtractor(PDDocument document) {
        return new PagesExtractor(document) {
            @Override
            public void setCompress(boolean compress) {
                if (compress) {
                    destinationDocument().addWriteOption(COMPRESSED_OPTS);
                } else {
                    destinationDocument().removeWriteOption(COMPRESSED_OPTS);
                }
            }
        };
    }

    static class OutputSizeStrategy implements NextOutputStrategy {
        private long sizeLimit;
        private PDDocument document;
        private ExistingPagesSizePredictor predictor;
        private Supplier<ExistingPagesSizePredictor> predictorSupplier = ExistingPagesSizePredictor::instance;
        private PageCopier copier;

        OutputSizeStrategy(PDDocument document, SplitBySizeParameters parameters, boolean optimize) {
            this.sizeLimit = parameters.getSizeToSplitAt();
            this.document = document;
            this.copier = new PageCopier(optimize);
            if (parameters.isCompress()) {
                predictorSupplier = () -> ExistingPagesSizePredictor.instance(WriteOption.COMPRESS_STREAMS,
                        WriteOption.XREF_STREAM);
            }

        }

        public void newPredictor() throws TaskIOException {
            try {
                predictor = predictorSupplier.get();
                predictor.addIndirectReferenceFor(document.getDocumentInformation());
                predictor.addIndirectReferenceFor(document.getDocumentCatalog().getViewerPreferences());
            } catch (IOException e) {
                throw new TaskIOException("Unable to initialize the pages size predictor", e);
            }
        }

        public void addPage(int page) throws TaskIOException {
            try {
                if (page <= document.getNumberOfPages()) {
                    predictor.addPage(copier.copyOf(document.getPage(page - 1)));
                }
            } catch (IOException e) {
                throw new TaskIOException("Unable to simulate page " + page + " addition", e);
            }
        }

        public void closePredictor() {
            IOUtils.closeQuietly(predictor);
            this.predictor = null;
        }

        @Override
        public void ensureIsValid() throws TaskExecutionException {
            if (sizeLimit < 1) {
                throw new TaskExecutionException(
                        String.format("Unable to split at %d, a positive size is required.", sizeLimit));
            }
        }

        @Override
        public boolean isOpening(Integer page) {
            return predictor == null || !predictor.hasPages();
        }

        @Override
        public boolean isClosing(Integer page) throws TaskIOException {
            try {
                long currentPageSize = predictor.predictedPagesSize();
                return (PDF_HEADER_SIZE + ID_VALUE_SIZE + currentPageSize + predictor.predictedXrefTableSize()
                        + documentFooterSize(currentPageSize) + (predictor.pages() * PAGE_OVERHEAD)) > sizeLimit;
            } catch (IOException e) {
                throw new TaskIOException("Unable to simulate page " + page + " addition", e);
            }
        }

        private int documentFooterSize(long documentSize) {
            // startxref + %%EOF + few EOL
            return 17 + Long.toString(documentSize).length();
        }
    }
}
