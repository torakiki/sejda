/*
 * Created on 29/giu/2010
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
package org.sejda.core.support.prefix.model;

import org.apache.commons.lang3.StringUtils;
import org.sejda.model.SejdaFileExtensions;

/**
 * Request for a new name generation.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class NameGenerationRequest {

    private Integer page = null;
    private Integer fileNumber = null;
    private String bookmark = null;
    private String originalName = null;
    private String extension = null;

    private NameGenerationRequest(String extension) {
        if (StringUtils.isBlank(extension)) {
            throw new IllegalArgumentException("Extension cannot be blank.");
        }
        this.extension = extension;
    }

    /**
     * @return a newly created {@link NameGenerationRequest} where the generated name will have a pdf extension.
     */
    public static NameGenerationRequest nameRequest() {
        return new NameGenerationRequest(SejdaFileExtensions.PDF_EXTENSION);
    }

    /**
     * 
     * @param extension
     *            the extension applied to the generated name, cannot be blank (Ex. pdf, txt).
     * @return a newly created {@link NameGenerationRequest}
     */
    public static NameGenerationRequest nameRequest(String extension) {
        return new NameGenerationRequest(extension);
    }

    /**
     * Fluently sets the page
     * 
     * @param page
     * @return the current instance
     */
    public NameGenerationRequest page(int page) {
        this.page = page;
        return this;
    }

    /**
     * Fluently sets the file number
     * 
     * @param fileNumber
     * @return the current instance
     */
    public NameGenerationRequest fileNumber(int fileNumber) {
        this.fileNumber = fileNumber;
        return this;
    }

    /**
     * Fluently sets the bookmark
     * 
     * @param bookmark
     * @return the current instance
     */
    public NameGenerationRequest bookmark(String bookmark) {
        this.bookmark = bookmark;
        return this;
    }

    /**
     * Fluently sets the original file name
     * 
     * @param originalName
     * @return the current instance
     */
    public NameGenerationRequest originalName(String originalName) {
        if (StringUtils.isBlank(originalName)) {
            throw new IllegalArgumentException("Original name cannot be blank");
        }
        // check if the filename contains '.' and it's at least in second position (Ex. a.pdf)
        if (originalName.lastIndexOf('.') >= 1) {
            this.originalName = originalName.substring(0, originalName.lastIndexOf('.'));
        } else {
            this.originalName = originalName;
        }
        return this;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getFileNumber() {
        return fileNumber;
    }

    public String getBookmark() {
        return bookmark;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getExtension() {
        return extension;
    }

}
