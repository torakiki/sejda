/*
 * Created on 04/jul/2011
 *
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
package org.sejda.core.manipulation.model.task.itext.component.split;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskExecutionException;
import org.sejda.core.manipulation.model.parameter.AbstractSplitByPageParameters;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.manipulation.model.task.itext.component.DefaultPdfCopier;
import org.sejda.core.manipulation.model.task.itext.component.PdfCopier;
import org.sejda.core.support.prefix.model.NameGenerationRequest;

import com.lowagie.text.pdf.PdfReader;

/**
 * Splitter implementation that split at a given set of page numbers.
 * 
 * @author Andrea Vacondio
 * @param <T>
 *            type of the parameter the splitter needs to perform the split.
 */
public class PagesPdfSplitter<T extends AbstractSplitByPageParameters> extends AbstractPdfSplitter<T> {

    private SplitPages splitPages;

    public PagesPdfSplitter(PdfReader reader, T parameters) {
        super(reader);
        setParameters(parameters);
        this.splitPages = new SplitPages(parameters.getPages(super.getTotalNumberOfPages()));
    }

    @Override
    PdfCopier openCopier(PdfReader reader, File outputFile, PdfVersion version) throws TaskException {
        return new DefaultPdfCopier(reader, outputFile, version);
    }

    @Override
    NextOutputStrategy nextOutputStrategy() {
        return splitPages;
    }

    @Override
    NameGenerationRequest enrichNameGenerationRequest(NameGenerationRequest request) {
        return request;
    }

    /**
     * Strategy that holds the page numbers where the split process has to split.
     * 
     * @author Andrea Vacondio
     * 
     */
    static class SplitPages implements NextOutputStrategy {

        private Set<Integer> closingPages = new HashSet<Integer>();
        private Set<Integer> openingPages = new HashSet<Integer>();

        SplitPages(Collection<Integer> pages) {
            openingPages.add(1);
            for (Integer page : pages) {
                add(page);
            }
        }

        private void add(Integer page) {
            closingPages.add(page);
            openingPages.add(page + 1);
        }

        public void ensureIsValid() throws TaskExecutionException {
            if (closingPages.size() <= 0) {
                throw new TaskExecutionException("Unable to split, no page number given.");
            }
        }

        /**
         * @param page
         * @return true if the given page is an opening page (a page where the split process should start a new document).
         */
        public boolean isOpening(Integer page) {
            return openingPages.contains(page);
        }

        /**
         * @param page
         * @return true if the given page is an closing page (a page where the split process should close the document).
         */
        public boolean isClosing(Integer page) {
            return closingPages.contains(page);
        }
    }
}
