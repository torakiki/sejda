/*
 * Created on 30/mag/2010
 *
 * Copyright 2010 Sober Lemur S.r.l. and Sejda BV.
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

import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.output.CompressionPolicy;
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

    @NotNull
    private CompressionPolicy compressionPolicy = CompressionPolicy.COMPRESS;
    private PdfVersion version;

    /**
     * @deprecated use {@link #compressionPolicy()}
     */
    @Deprecated
    public boolean isCompress() {
        return CompressionPolicy.COMPRESS == compressionPolicy;
    }

    /**
     * @deprecated use {@link #setCompressionPolicy(CompressionPolicy)}
     */
    @Deprecated
    public void setCompress(boolean compress) {
        if (compress) {
            this.compressionPolicy = CompressionPolicy.COMPRESS;
        } else {
            this.compressionPolicy = CompressionPolicy.NEUTRAL;
        }
    }

    public void setCompressionPolicy(CompressionPolicy compressionPolicy) {
        this.compressionPolicy = compressionPolicy;
    }

    public CompressionPolicy compressionPolicy() {
        return compressionPolicy;
    }

    public PdfVersion getVersion() {
        return version;
    }

    /**
     * Set the pdf version for the output document/s
     */
    public void setVersion(PdfVersion version) {
        this.version = version;
    }

    /**
     * @return the min output pdf version required by this parameter object depending on its attributes. Each extending class is responsible for the implementation of this method.
     */
    public PdfVersion getMinRequiredPdfVersion() {
        if (compressionPolicy() == CompressionPolicy.COMPRESS) {
            return PdfVersion.VERSION_1_5;
        }
        return PdfVersion.VERSION_1_0;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(compressionPolicy).append(version)
                .append(getOutput()).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractPdfOutputParameters parameter)) {
            return false;
        }
        return new EqualsBuilder().appendSuper(super.equals(other))
                .append(compressionPolicy, parameter.compressionPolicy()).append(version, parameter.getVersion())
                .append(getOutput(), parameter.getOutput()).isEquals();
    }
}
