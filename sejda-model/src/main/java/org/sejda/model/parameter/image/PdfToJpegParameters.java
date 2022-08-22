/*
 * Created on 01/mar/2013
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.parameter.image;

import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.image.ImageType;

import jakarta.validation.constraints.Max;

/**
 * Parameter meant to convert an existing pdf source to JPEG images.
 *
 * @author Andrea Vacondio
 */
public class PdfToJpegParameters extends AbstractPdfToMultipleImageParameters {

    @Min(0)
    @Max(100)
    private int quality = 100;

    public PdfToJpegParameters(ImageColorType type) {
        super(type);
    }

    @Override
    public ImageType getOutputImageType() {
        return ImageType.JPEG;
    }

    public int getQuality() {
        return quality;
    }

    /**
     * The quality of the generated images where 0 is lowest (high compression) and 100 is highest (low compression)
     * 
     * @param quality
     */
    public void setQuality(int quality) {
        this.quality = quality;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(quality).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PdfToJpegParameters)) {
            return false;
        }
        PdfToJpegParameters parameter = (PdfToJpegParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(quality, parameter.quality).isEquals();
    }
}
