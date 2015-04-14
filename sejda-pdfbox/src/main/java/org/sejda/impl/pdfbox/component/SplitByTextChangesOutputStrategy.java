/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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
package org.sejda.impl.pdfbox.component;

import static org.apache.commons.lang3.StringUtils.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.sejda.model.TopLeftRectangularBox;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.split.NextOutputStrategy;
import org.sejda.model.split.SplitPages;
import org.slf4j.LoggerFactory;

import java.util.*;

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
            PDPage page = (PDPage) document.getDocumentCatalog().getAllPages().get(pageNumber - 1);

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

    public void ensureIsValid() throws TaskExecutionException {
        delegate.ensureIsValid();
    }

    public boolean isOpening(Integer page) {
        return delegate.isOpening(page);
    }

    public boolean isClosing(Integer page) {
        return delegate.isClosing(page);
    }

    public String getTextByPage(int page) {
        return textByPage.get(page);
    }
}
