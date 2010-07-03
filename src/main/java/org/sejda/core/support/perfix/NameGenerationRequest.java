/*
 * Created on 29/giu/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.support.perfix;

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
