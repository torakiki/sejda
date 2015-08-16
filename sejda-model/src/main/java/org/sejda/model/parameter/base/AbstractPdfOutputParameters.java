/*
 * Created on 30/mag/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

    private boolean compress = true;
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
