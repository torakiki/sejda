/*
 * Created on 03/ago/2011
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
package org.sejda.impl.itext.component.split;

import java.io.File;

import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.impl.itext.component.CountingPdfCopier;
import org.sejda.impl.itext.component.PdfCopier;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.parameter.SplitBySizeParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.split.NextOutputStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

/**
 * Splitter implementation to split a pdf document when the output reaches a given size.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SizePdfSplitter extends AbstractPdfSplitter<SplitBySizeParameters> {

    private OutputSizeStrategy nextOutputStrategy;

    public SizePdfSplitter(PdfReader reader, SplitBySizeParameters parameters) {
        super(reader, parameters);
        nextOutputStrategy = new OutputSizeStrategy(parameters.getSizeToSplitAt());
    }

    @Override
    NameGenerationRequest enrichNameGenerationRequest(NameGenerationRequest request) {
        return request;
    }

    @Override
    PdfCopier openCopier(PdfReader reader, File outputFile, PdfVersion version) throws TaskException {
        CountingPdfCopier countingPdfCopier = new CountingPdfCopier(reader, outputFile, version);
        nextOutputStrategy.setCopier(countingPdfCopier);
        return countingPdfCopier;
    }

    @Override
    NextOutputStrategy nextOutputStrategy() {
        return nextOutputStrategy;
    }

    /**
     * Strategy to decide when to open/close a new document based on the size of the output written.
     * 
     * @author Andrea Vacondio
     * 
     */
    static class OutputSizeStrategy implements NextOutputStrategy {

        private static final Logger LOG = LoggerFactory.getLogger(OutputSizeStrategy.class);

        private long sizeLimit;
        private CountingPdfCopier copier;

        OutputSizeStrategy(long sizeLimit) {
            this.sizeLimit = sizeLimit;
        }

        private void setCopier(CountingPdfCopier copier) {
            this.copier = copier;
        }

        public void ensureIsValid() throws TaskExecutionException {
            if (sizeLimit < 1) {
                throw new TaskExecutionException(String.format("Unable to split at %d, a positive size is required.",
                        sizeLimit));
            }
        }

        public boolean isOpening(Integer page) {
            return copier == null || copier.isClosed();
        }

        public boolean isClosing(Integer page) {
            long estimatedSizeAfterNextPage = copier.getEstimatedSizeAfterNextPage();
            if (estimatedSizeAfterNextPage > sizeLimit) {
                LOG.debug("Estimated size if a new page is added is {} bytes.", estimatedSizeAfterNextPage);
                return true;
            }
            return false;
        }
    }
}
