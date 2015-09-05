/* 
 * This file is part of the Sejda source code
 * Created on 31/Jul/2015
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.component.split;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.sejda.impl.sambox.component.PdfTextExtractorByArea;
import org.sejda.model.TopLeftRectangularBox;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.split.NextOutputStrategy;
import org.sejda.model.split.SplitPages;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.slf4j.LoggerFactory;

public class SplitByTextChangesOutputStrategy implements NextOutputStrategy {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SplitByTextChangesOutputStrategy.class);

    private SplitPages delegate;
    private Collection<Integer> pages;
    private Map<Integer, String> textByPage = new HashMap<Integer, String>();

    public SplitByTextChangesOutputStrategy(PDDocument document, TopLeftRectangularBox area) throws TaskIOException {
        this.pages = findPageToSplitAt(document, area);
        this.delegate = new SplitPages(pages);
    }

    Collection<Integer> findPageToSplitAt(PDDocument document, TopLeftRectangularBox area) throws TaskIOException {
        Collection<Integer> pagesToSplitAt = new HashSet<Integer>();
        String prevPageText = null;

        for (int pageNumber = 1; pageNumber <= document.getNumberOfPages(); pageNumber++) {
            PDPage page = document.getDocumentCatalog().getPages().get(pageNumber - 1);

            String pageText = extractTextFromPageArea(page, area);
            boolean noChanges = (prevPageText == null || // no previous
                                    isBlank(pageText) || // if there's no text in the area, include in the prev document
                                    prevPageText.equals(pageText));

            boolean someChanges = !noChanges;

            if (someChanges) {
                LOG.debug("Text changed from {} to {} on page {} in area: {}", prevPageText, pageText, pageNumber, area);
                // decrementing with 1 because the splitter expects page X if X+1 should start a new document
                pagesToSplitAt.add(pageNumber - 1);
            }

            if(isNotBlank(pageText)) {
                prevPageText = pageText;
            }

            textByPage.put(pageNumber, pageText);
        }

        return pagesToSplitAt;
    }

    String extractTextFromPageArea(PDPage page, TopLeftRectangularBox area) throws TaskIOException {
        String text = new PdfTextExtractorByArea().extractTextFromArea(page, area.asRectangle());
        String result = defaultIfBlank(text, "");
        result = StringUtils.strip(result);
        return result;
    }

    Collection<Integer> getPages() {
        return pages;
    }

    @Override
    public void ensureIsValid() throws TaskExecutionException {
        delegate.ensureIsValid();
    }

    @Override
    public boolean isOpening(Integer page) {
        return delegate.isOpening(page);
    }

    @Override
    public boolean isClosing(Integer page) {
        return delegate.isClosing(page);
    }

    public String getTextByPage(int page) {
        return textByPage.get(page);
    }
}
