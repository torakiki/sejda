/*
 * Created on 26/jul/2011
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
package org.sejda.impl.itext.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sejda.model.exception.TaskException;
import org.sejda.model.outline.OutlineSubsetProvider;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

/**
 * iText implementation of a bookmarks subset provider.
 * 
 * @author Andrea Vacondio
 * 
 */
public class ITextOutlineSubsetProvider implements OutlineSubsetProvider<Map<String, Object>> {

    private int totalNumberOfPages;
    private List<Object> bookmarks;
    private int startPage = -1;

    public ITextOutlineSubsetProvider(PdfReader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("Unable to retrieve bookmarks from a null reader.");
        }

        this.totalNumberOfPages = reader.getNumberOfPages();
        bookmarks = getBookmarksOrEmpty(reader);
    }

    private List<Object> getBookmarksOrEmpty(PdfReader reader) {
        @SuppressWarnings("unchecked")
        List<Object> documentBookmarks = SimpleBookmark.getBookmark(reader);
        if (documentBookmarks != null) {
            return Collections.unmodifiableList(documentBookmarks);
        }
        return Collections.emptyList();
    }

    public void startPage(int startPage) {
        this.startPage = startPage;
    }

    public Collection<Map<String, Object>> getOutlineUntillPage(int endPage) throws TaskException {
        return getOutlineUntillPageWithOffset(endPage, 0);
    }

    public Collection<Map<String, Object>> getOutlineUntillPageWithOffset(int endPage, int offset) throws TaskException {
        if (startPage < 0 || startPage > endPage) {
            throw new TaskException(
                    "Unable to process document bookmarks: start page is negative or higher then end page.");
        }
        if (bookmarks.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> books = getDeepCopyBookmarks(bookmarks);
        if (endPage < totalNumberOfPages) {
            SimpleBookmark.eliminatePages(books, new int[] { endPage + 1, totalNumberOfPages });
        }
        if (startPage > 1) {
            SimpleBookmark.eliminatePages(books, new int[] { 1, startPage - 1 });
            SimpleBookmark.shiftPageNumbers(books, -(startPage - 1), null);
        }
        if (offset != 0) {
            SimpleBookmark.shiftPageNumbers(books, offset, null);
        }
        return books;
    }

    /**
     * The purpose here is to create a deep copy of the bookmarks structure instead of retrieving bookmarks from the reader every time that is much slower. The code here is ugly
     * but I'm dependent on the underlying library that returns a non generalized List. Digging into the code it turns out it's a List<HashMap<String, Object>>.
     * 
     * @param inputBook
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getDeepCopyBookmarks(List<?> inputBook) {
        List<Map<String, Object>> retVal = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> item : (List<Map<String, Object>>) inputBook) {
            retVal.add(getCopyMap(item));
        }
        return retVal;
    }

    private Map<String, Object> getCopyMap(Map<String, Object> map) {
        Map<String, Object> retVal = new HashMap<String, Object>();
        if (map != null) {
            for (Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof List) {
                    retVal.put(entry.getKey(), getDeepCopyBookmarks((List<?>) entry.getValue()));
                } else {
                    retVal.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return retVal;
    }
}
