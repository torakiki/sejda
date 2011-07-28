/*
 * Created on 03/jul/2011
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
package org.sejda.core.manipulation.model.task.itext.component;

import java.util.List;

import org.sejda.core.exception.TaskException;
import org.sejda.core.exception.TaskIOException;

import com.lowagie.text.pdf.PdfPageLabels;
import com.lowagie.text.pdf.PdfReader;

/**
 * Prodvides functionalities to copy pages from a given {@link PdfReader}.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface PdfCopier {

    /**
     * Adds the given page extracted from the input reader.
     * 
     * @param reader
     * @param pageNumber
     * @throws TaskException
     */
    void addPage(PdfReader reader, int pageNumber) throws TaskException;

    /**
     * Adds all the pages from the input reader
     * 
     * @param reader
     * @throws TaskException
     */
    void addAllPages(PdfReader reader) throws TaskException;

    /**
     * Enables compression if compress is true.
     * 
     * @param compress
     */
    void setCompression(boolean compress);

    /**
     * sets the input page labels to the underlying pdf copy.
     * 
     * @param labels
     */
    void setPageLabels(PdfPageLabels labels);

    /**
     * Closes the copier.
     */
    void close();

    /**
     * Frees the reader on the underlying pdf copy.
     * 
     * @param reader
     * @throws TaskIOException
     */
    void freeReader(PdfReader reader) throws TaskIOException;

    /**
     * Sets the input list of bookmarks to the copier.
     * 
     * @param bookmarks
     */
    void setBookmarks(List<?> bookmarks);

}
