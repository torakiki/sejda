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

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.core.manipulation.model.image.ImageColorType;
import org.sejda.core.manipulation.model.image.ImageType;
import org.sejda.core.manipulation.model.input.PdfSource;
import org.sejda.core.manipulation.model.parameter.base.AbstractParameters;
import org.sejda.core.manipulation.model.parameter.base.SinglePdfSourceTaskParameters;

/**
 * Base class for a parameter meant to convert an existing pdf source to an image of a specified type.
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractPdfToImageParameters extends AbstractParameters implements SinglePdfSourceTaskParameters {

    public static final int DEFAULT_DPI = 72;

    @Min(1)
    private int resolutionInDpi = DEFAULT_DPI;
    @NotNull
    private ImageColorType outputImageColorType;
    @Valid
    @NotNull
    private PdfSource source;

    public PdfSource getSource() {
        return source;
    }

    public void setSource(PdfSource source) {
        this.source = source;
    }

    AbstractPdfToImageParameters(ImageColorType outputImageColorType) {
        this.outputImageColorType = outputImageColorType;
    }

    public ImageColorType getOutputImageColorType() {
        return outputImageColorType;
    }

    /**
     * @return the type of image the task executing this parameter will convert the pdf source to.
     */
    @NotNull
    public abstract ImageType getOutputImageType();

    public int getResolutionInDpi() {
        return resolutionInDpi;
    }

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
