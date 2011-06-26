/*
 * Created on 29/mag/2010
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
package org.sejda.core.manipulation.model.rotation;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Model representing a rotation to be applyed to a single page or to a class of pages (even, odd, all)
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PageRotation {

    private int pageNumber;
    @NotNull
    private final Rotation rotation;
    @NotNull
    private final RotationType rotationType;

    private PageRotation(int pageNumber, Rotation rotation, RotationType rotationType) {
        this.pageNumber = pageNumber;
        this.rotation = rotation;
        this.rotationType = rotationType;
    }

    /**
     * @return the pageNumber
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * @return the rotation
     */
    public Rotation getRotation() {
        return rotation;
    }

    /**
     * @return the rotationType
     */
    public RotationType getRotationType() {
        return rotationType;
    }

    /**
     * @param page
     * @return true if this rotation accept the given page number
     */
    public boolean accept(int page) {
        if (RotationType.ALL_PAGES.equals(rotationType)) {
            return true;
        }
        if (RotationType.ODD_PAGES.equals(rotationType) && (page % 2 != 0)) {
            return true;
        }
        if (RotationType.EVEN_PAGES.equals(rotationType) && (page % 2 == 0)) {
            return true;
        }
        // single page
        return page == pageNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(pageNumber).append(rotation.getDegrees()).append(rotationType)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(pageNumber).append(rotation).append(rotationType).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PageRotation)) {
            return false;
        }
        PageRotation oRotation = (PageRotation) other;
        return new EqualsBuilder().append(pageNumber, oRotation.getPageNumber())
                .append(rotation, oRotation.getRotation()).append(rotationType, oRotation.getRotationType()).isEquals();
    }

    /**
     * Creates a single page rotation for the given pageNumber and the given {@link Rotation}
     * 
     * @param pageNumber
     * @param rotation
     * @return the created instance
     * @throws IllegalStateException
     *             if the page number is not positive.
     */
    public static PageRotation createSinglePageRotation(int pageNumber, Rotation rotation) {
        if (pageNumber <= 0) {
            throw new IllegalStateException("Page number must be positive.");
        }
        return new PageRotation(pageNumber, rotation, RotationType.SINGLE_PAGE);
    }

    /**
     * Creates a non single page rotation
     * 
     * @param rotation
     * @param rotationType
     * @return the created instance
     * @throws IllegalStateException
     *             if the rotation type is single page rotation.
     */
    public static PageRotation createMultiplePagesRotation(Rotation rotation, RotationType rotationType) {
        if (RotationType.SINGLE_PAGE.equals(rotationType)) {
            throw new IllegalStateException("Rotation type cannot be single page.");
        }
        return new PageRotation(0, rotation, rotationType);
    }
}
