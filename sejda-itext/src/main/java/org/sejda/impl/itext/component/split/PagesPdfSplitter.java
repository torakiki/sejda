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
package org.sejda.impl.itext.component.split;

import java.io.File;

import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.impl.itext.component.DefaultPdfCopier;
import org.sejda.impl.itext.component.PdfCopier;
import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.AbstractSplitByPageParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.split.NextOutputStrategy;
import org.sejda.model.split.SplitPages;

import com.lowagie.text.pdf.PdfReader;

/**
 * Splitter implementation to split at a given set of page numbers.
 * 
 * @author Andrea Vacondio
 * @param <T>
 *            type of the parameter the splitter needs to perform the split.
 */
public class PagesPdfSplitter<T extends AbstractSplitByPageParameters> extends AbstractPdfSplitter<T> {

    private SplitPages splitPages;

    public PagesPdfSplitter(PdfReader reader, T parameters) {
        super(reader, parameters);
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
}
