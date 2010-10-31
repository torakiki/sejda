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
package org.sejda.core.support.perfix.model;

import java.io.Serializable;

/**
 * Request for a new name generation.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class NameGenerationRequest implements Serializable {

    private static final long serialVersionUID = -7262824895395860407L;

    private Integer page = null;
    private Integer fileNumber = null;
    private String bookmark = null;
    private String originalName = null;

    private NameGenerationRequest() {
        super();
    }

    /**
     * @return a newly created {@link NameGenerationRequest}
     */
    public static NameGenerationRequest nameRequest() {
        return new NameGenerationRequest();
    }

    /**
     * Fluently sets the page
     * 
     * @param page
     * @return the current instance
     */
    public NameGenerationRequest page(Integer page) {
        this.page = page;
        return this;
    }

    /**
     * Fluently sets the file number
     * 
     * @param fileNumber
     * @return the current instance
     */
    public NameGenerationRequest fileNumber(Integer fileNumber) {
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
        this.originalName = originalName;
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

}
