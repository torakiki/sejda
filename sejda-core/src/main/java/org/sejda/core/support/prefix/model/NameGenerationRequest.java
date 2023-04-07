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

import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.RequireUtils.requireNotBlank;
import static org.sejda.commons.util.RequireUtils.requireNotNullArg;

/**
 * Request for a new name generation.
 *
 * @author Andrea Vacondio
 */
public final class NameGenerationRequest {

    private static final String EXTENSION_KEY = "extension";
    private static final String BOOKMARK_KEY = "bookmark";
    private static final String ORIGINAL_NAME_KEY = "originalName";
    private static final String TEXT_KEY = "text";
    private static final String PAGE_KEY = "page";
    private static final String FILENUMBER_KEY = "fileNumber";
    private Map<String, Object> values = new HashMap<>();

    private NameGenerationRequest(String extension) {
        requireNotBlank(extension, "Extension cannot be blank");
        setValue(EXTENSION_KEY, extension);
    }

    /**
     * @return a newly created {@link NameGenerationRequest} where the generated name will have a pdf extension.
     */
    public static NameGenerationRequest nameRequest() {
        return new NameGenerationRequest(SejdaFileExtensions.PDF_EXTENSION);
    }

    /**
     * @param extension the extension applied to the generated name, cannot be blank (Ex. pdf, txt).
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
        setValue(PAGE_KEY, page);
        return this;
    }

    /**
     * Fluently sets the file number
     *
     * @param fileNumber
     * @return the current instance
     */
    public NameGenerationRequest fileNumber(int fileNumber) {
        setValue(FILENUMBER_KEY, fileNumber);
        return this;
    }

    /**
     * Fluently sets the bookmark
     *
     * @param bookmark
     * @return the current instance
     */
    public NameGenerationRequest bookmark(String bookmark) {
        setValue(BOOKMARK_KEY, StringUtils.trim(bookmark));
        return this;
    }

    /**
     * Fluently sets the original file name
     *
     * @param originalName
     * @return the current instance
     */
    public NameGenerationRequest originalName(String originalName) {
        requireNotBlank(originalName, "Original name cannot be blank");
        // check if the filename contains '.' and it's at least in second position (Ex. a.pdf)
        if (originalName.lastIndexOf('.') >= 1) {
            setValue(ORIGINAL_NAME_KEY, originalName.substring(0, originalName.lastIndexOf('.')));
        } else {
            setValue(ORIGINAL_NAME_KEY, originalName);
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
        setValue(TEXT_KEY, text);
        return this;
    }

    public Integer getPage() {
        return getValue(PAGE_KEY, Integer.class);
    }

    public Integer getFileNumber() {
        return getValue(FILENUMBER_KEY, Integer.class);
    }

    public String getBookmark() {
        return getValue(BOOKMARK_KEY, String.class);
    }

    public String getOriginalName() {
        return getValue(ORIGINAL_NAME_KEY, String.class);
    }

    public String getExtension() {
        return getValue(EXTENSION_KEY, String.class);
    }

    public String getText() {
        return getValue(TEXT_KEY, String.class);
    }

    /**
     * @return the value associated with the given key and of the given type or null if not found or not of the given type
     */
    public <T> T getValue(String key, Class<T> type) {
        return ofNullable(values.get(key)).filter(type::isInstance).map(type::cast).orElse(null);
    }

    /**
     * Sets a value associated with the given key. It allows null values
     *
     * @param key
     * @param value
     */
    public void setValue(String key, Object value) {
        requireNotNullArg(key, "Key cannot be null");
        values.put(key, value);
    }
}
