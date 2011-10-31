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
package org.sejda.model.parameter.image;

import javax.validation.constraints.NotNull;

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
// TODO validate combinations of imagecolortype and tiff compression
public class PdfToMultipleTiffParameters extends AbstractPdfToMultipleImageParameters implements PdfToTiffParameters {

    @NotNull
    private TiffCompressionType compressionType = TiffCompressionType.NONE;

    public PdfToMultipleTiffParameters(ImageColorType outputImageColorType) {
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
        if (!(other instanceof PdfToMultipleTiffParameters)) {
            return false;
        }
        PdfToMultipleTiffParameters parameter = (PdfToMultipleTiffParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other))
                .append(compressionType, parameter.getCompressionType()).isEquals();
    }
}
