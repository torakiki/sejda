/*
 * Created on 03/ago/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.itext.component.split;

import java.io.File;

import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskExecutionException;
import org.sejda.core.manipulation.model.parameter.SplitBySizeParameters;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.impl.itext.component.CountingPdfCopier;
import org.sejda.impl.itext.component.PdfCopier;
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
        super(reader);
        setParameters(parameters);
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
