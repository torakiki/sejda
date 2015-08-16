/*
 * Created on Sep 4, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.conversion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.pdf.page.PageRange;

/**
 * Adapter for a list of {@link PdfMergeInput}s. Provides initialization from a list of {@link PdfFileSource}s and a iterator on a list of {@link PageRange} sets
 * 
 * @author Eduard Weissmann
 * 
 */
public class MultiplePdfMergeInputAdapter {

    private final List<PdfMergeInput> pdfMergeInputs = new ArrayList<PdfMergeInput>();

    public MultiplePdfMergeInputAdapter(List<PdfFileSource> inputFiles, List<Set<PageRange>> pageRanges) {
        Iterator<Set<PageRange>> pageRangeSetIterator = pageRanges.iterator();
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
