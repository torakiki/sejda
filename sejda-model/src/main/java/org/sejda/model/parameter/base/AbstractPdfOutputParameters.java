/*
 * Created on 30/mag/2010
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
package org.sejda.model.parameter.base;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.validation.constraint.ValidPdfVersion;

/**
 * Abstract parameters implementation with attributes commonly used by all the parameters implementation having single or multiple pdf output as result of the task manipulation.
 * 
 * @author Andrea Vacondio
 * 
 */
@ValidPdfVersion
public abstract class AbstractPdfOutputParameters extends AbstractParameters {

    private boolean compress = false;
    private PdfVersion version;

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public PdfVersion getVersion() {
        return version;
    }

    /**
     * Set the pdf version for the output document/s
     * 
     * @param version
     */
    public void setVersion(PdfVersion version) {
        this.version = version;
    }

    /**
     * @return the min output pdf version required by this parameter object depending on its attributes. Each extending class is responsible for the implementation of this method.
     */
    public PdfVersion getMinRequiredPdfVersion() {
        return isCompress() ? PdfVersion.VERSION_1_5 : PdfVersion.VERSION_1_0;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(compress).append(version).append(getOutput())
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractPdfOutputParameters)) {
            return false;
        }
        AbstractPdfOutputParameters parameter = (AbstractPdfOutputParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(compress, parameter.isCompress())
                .append(version, parameter.getVersion()).append(getOutput(), parameter.getOutput()).isEquals();
    }
}
