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

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.image.ImageColorType;
import org.sejda.model.image.ImageType;
import org.sejda.model.input.PdfSource;
import org.sejda.model.parameter.base.AbstractParameters;
import org.sejda.model.parameter.base.SinglePdfSourceTaskParameters;

/**
 * Base class for a parameter meant to convert an existing pdf source to an image of a specified type.
 *
 * @author Andrea Vacondio
 */
public abstract class AbstractPdfToImageParameters extends AbstractParameters
        implements SinglePdfSourceTaskParameters, PdfToImageParameters {

    public static final int DEFAULT_DPI = 72;

    @Min(1)
    private int resolutionInDpi = DEFAULT_DPI;
    @NotNull
    private ImageColorType outputImageColorType;
    @Valid
    @NotNull
    private PdfSource<?> source;

    @Override
    public PdfSource<?> getSource() {
        return source;
    }

    @Override
    public void setSource(PdfSource<?> source) {
        this.source = source;
    }

    AbstractPdfToImageParameters(ImageColorType outputImageColorType) {
        this.outputImageColorType = outputImageColorType;
    }

    @Override
    public ImageColorType getOutputImageColorType() {
        return outputImageColorType;
    }

    @Override
    public void setOutputImageColorType(ImageColorType outputImageColorType) {
        this.outputImageColorType = outputImageColorType;
    }

    /**
     * @return the type of image the task executing this parameter will convert the pdf source to.
     */
    @NotNull
    public abstract ImageType getOutputImageType();

    @Override
    public int getResolutionInDpi() {
        return resolutionInDpi;
    }

    @Override
    public void setResolutionInDpi(int resolutionInDpi) {
        this.resolutionInDpi = resolutionInDpi;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(resolutionInDpi).append(outputImageColorType)
                .append(getOutputImageType()).append(source).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractPdfToImageParameters)) {
            return false;
        }
        AbstractPdfToImageParameters parameter = (AbstractPdfToImageParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other))
                .append(resolutionInDpi, parameter.getResolutionInDpi())
                .append(outputImageColorType, parameter.getOutputImageColorType())
                .append(getOutputImageType(), parameter.getOutputImageType()).append(source, parameter.getSource())
                .isEquals();
    }
}
