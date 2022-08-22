/*
 * Created on 18/set/2011
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

import jakarta.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.image.ImageType;
import org.sejda.model.image.TiffCompressionType;

/**
 * Parameter meant to convert an existing pdf source to multiple TIFF images.
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfToMultipleTiffParameters extends AbstractPdfToMultipleImageParameters implements PdfToTiffParameters {

    @NotNull
    private TiffCompressionType compressionType = TiffCompressionType.NONE;

    public PdfToMultipleTiffParameters(ImageColorType outputImageColorType) {
        this(outputImageColorType, TiffCompressionType.NONE);
    }

    public PdfToMultipleTiffParameters(ImageColorType outputImageColorType, TiffCompressionType compressionType) {
        super(outputImageColorType);
        this.compressionType = compressionType;
    }

    @Override
    public ImageType getOutputImageType() {
        return ImageType.TIFF;
    }

    @Override
    public TiffCompressionType getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(TiffCompressionType compressionType) {
        this.compressionType = compressionType;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(compressionType).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PdfToMultipleTiffParameters)) {
            return false;
        }
        PdfToMultipleTiffParameters parameter = (PdfToMultipleTiffParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other))
                .append(compressionType, parameter.getCompressionType()).isEquals();
    }
}
