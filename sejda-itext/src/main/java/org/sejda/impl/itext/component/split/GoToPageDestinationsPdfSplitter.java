/*
 * Created on 09/ago/2011
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
import org.sejda.model.outline.OutlineGoToPageDestinations;
import org.sejda.model.parameter.SplitByGoToActionLevelParameters;
import org.sejda.model.pdf.PdfVersion;

import com.lowagie.text.pdf.PdfReader;

/**
 * Splitter implementation to split at pages that have a GoTo action pointing to them.
 * 
 * @author Andrea Vacondio
 * 
 */
public class GoToPageDestinationsPdfSplitter extends AbstractPdfSplitter<SplitByGoToActionLevelParameters> {

    private SplitPages splitPages;
    private OutlineGoToPageDestinations outlineDestinations;

    /**
     * 
     * @param reader
     *            reader opened on the target pdf document.
     * @param parameters
     * @param outlineDestinations
     *            holder for the outline destinations the splitter has to split at.
     */
    public GoToPageDestinationsPdfSplitter(PdfReader reader, SplitByGoToActionLevelParameters parameters,
            OutlineGoToPageDestinations outlineDestinations) {
        super(reader);
        setParameters(parameters);
        this.splitPages = new SplitPages(outlineDestinations.getPages());
        this.outlineDestinations = outlineDestinations;
    }

    @Override
    NameGenerationRequest enrichNameGenerationRequest(NameGenerationRequest request) {
        return request.bookmark(outlineDestinations.getTitle(request.getPage()));
    }

    @Override
    PdfCopier openCopier(PdfReader reader, File outputFile, PdfVersion version) throws TaskException {
        return new DefaultPdfCopier(reader, outputFile, version);
    }

    @Override
    NextOutputStrategy nextOutputStrategy() {
        return splitPages;
    }
}
