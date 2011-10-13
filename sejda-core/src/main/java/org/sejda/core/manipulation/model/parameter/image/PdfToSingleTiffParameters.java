/*
 * Created on 18/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.manipulation.model.parameter.image;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.core.Sejda;
import org.sejda.core.manipulation.model.image.ImageColorType;
import org.sejda.core.manipulation.model.image.ImageType;
import org.sejda.core.manipulation.model.image.TiffCompressionType;
import org.sejda.core.validation.constraint.SingleOutputAllowedExtensions;

/**
 * Parameter meant to convert an existing pdf source to a single TIFF image with multiple pages.
 * 
 * @author Andrea Vacondio
 * 
 */
// TODO validate combinations of imagecolortype and tiff compression
@SingleOutputAllowedExtensions(extensions = { Sejda.TIFF_EXTENSION, Sejda.TIF_EXTENSION })
public class PdfToSingleTiffParameters extends AbstractPdfToSingleImageParameters implements PdfToTiffParameters {

    @NotNull
    private TiffCompressionType compressionType = TiffCompressionType.NONE;

    public PdfToSingleTiffParameters(ImageColorType outputImageColorType) {
        super(outputImageColorType);
    }

    @Override
    public ImageType getOutputImageType() {
        return ImageType.TIFF;
    }

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
        if (this == other) {
            return true;
        }
        if (!(other instanceof PdfToSingleTiffParameters)) {
            return false;
        }
        PdfToSingleTiffParameters parameter = (PdfToSingleTiffParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other))
                .append(compressionType, parameter.getCompressionType()).isEquals();
    }
}
