/*
 * Created on 29/giu/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
    private String text = null;

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
        this.bookmark = StringUtils.trim(bookmark);
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

    /**
     * Fluently sets the text. Called when splitting by text content, passes in the text found in the rectangle
     * box area on the page right before the split boundary.
     *
     * @param text strings found in page areas when splitting by text content
     * @return the current instance
     */

    public NameGenerationRequest text(String text) {
        this.text = text;
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

    public String getText() {
        return text;
    }

}
