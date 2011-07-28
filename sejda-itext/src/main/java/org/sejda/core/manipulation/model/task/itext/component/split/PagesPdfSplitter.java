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

import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.model.parameter.SinglePdfSourceParameters;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.manipulation.model.task.itext.component.DefaultPdfCopier;
import org.sejda.core.manipulation.model.task.itext.component.PdfCopier;
import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

//TODO javadoc

/**
 * Split by page numbers
 * 
 * @author Andrea Vacondio
 * 
 */
public class PagesPdfSplitter extends AbstractPdfSplitter {
    private static final Logger LOG = LoggerFactory.getLogger(PagesPdfSplitter.class);

    private SplitPages splitPages;

    public PagesPdfSplitter(PdfReader reader) {
        super(reader);
        this.splitPages = new SplitPages(super.getTotalNumberOfPages());
    }

    @Override
    PdfCopier openCopier(PdfReader reader, File outputFile, PdfVersion version) throws TaskException {
        return new DefaultPdfCopier(reader, outputFile, version);
    }

    @Override
    NextOutputStrategy nextOutputStrategy() {
        return splitPages;
    }

    /**
     * @throws TaskException
     *             if the resulting collection of pages is empty.
     */
    @Override
    public void split() throws TaskException {
        splitPages.ensureIsValid();
        super.split();
    }

    /**
     * Sets the parameters to use on this splitter.
     * 
     * @param parameters
     * @return the splitter.
     */
    public PagesPdfSplitter parameters(SinglePdfSourceParameters parameters) {
        setParameters(parameters);
        return this;
    }

    /**
     * Sets the prefix to use for this splitter.
     * 
     * @param outputPrefix
     * @return the splitter.
     */
    public PagesPdfSplitter prefix(String outputPrefix) {
        setPrefix(outputPrefix);
        return this;
    }

    /**
     * Stores the pages in a sorted set removing those pages that are not in the input document.
     * 
     * @param pages
     */
    public PagesPdfSplitter pages(Collection<Integer> pages) {
        for (Integer page : pages) {
            if (page > 0 && page <= super.getTotalNumberOfPages()) {
                splitPages.add(page);
            } else {
                LOG.warn("Cannot split at page {}. Page not found in the input document.");
            }
        }
        return this;
    }

    @Override
    NameGenerationRequest enrichNameGenerationRequest(NameGenerationRequest request) {
        return request;
    }
}
