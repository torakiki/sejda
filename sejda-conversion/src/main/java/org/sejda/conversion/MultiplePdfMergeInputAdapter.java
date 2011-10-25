/*
 * Created on Sep 4, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.conversion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sejda.core.manipulation.model.input.PdfFileSource;
import org.sejda.core.manipulation.model.input.PdfMergeInput;
import org.sejda.core.manipulation.model.pdf.page.PageRange;

/**
 * Adapter for a list of {@link PdfMergeInput}s. Provides initialization from a list of {@link PdfFileSource}s and a iterator on a list of {@link PageRange} sets
 * 
 * @author Eduard Weissmann
 * 
 */
public class MultiplePdfMergeInputAdapter {

    private final List<PdfMergeInput> pdfMergeInputs = new ArrayList<PdfMergeInput>();

    public MultiplePdfMergeInputAdapter(List<PdfFileSource> inputFiles, Iterator<Set<PageRange>> pageRangeSetIterator) {

        for (PdfFileSource eachFile : inputFiles) {
            PdfMergeInput pdfMergeInput = new PdfMergeInput(eachFile);
            if (pageRangeSetIterator.hasNext()) {
                pdfMergeInput.addAllPageRanges(pageRangeSetIterator.next());
            }

            pdfMergeInputs.add(pdfMergeInput);
        }
    }

    public List<PdfMergeInput> getPdfMergeInputs() {
        return pdfMergeInputs;
    }
}
