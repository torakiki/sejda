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
package org.sejda.core.manipulation.model.parameter;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.sejda.core.manipulation.model.output.AbstractPdfOutput;
import org.sejda.core.manipulation.model.pdf.PdfVersion;

/**
 * Abstract parameter implementation with attributes commonly used by all the parameters implementation
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractParameters implements Serializable, TaskParameters {

    private static final long serialVersionUID = -6100370016710146349L;

    @Valid
    @NotNull
    private AbstractPdfOutput output;
    private boolean overwrite = false;
    private boolean compress = false;
    private PdfVersion version;

    public boolean isOverwrite() {
        return overwrite;
    }

    /**
     * Set if the output should be overwritten if already exists
     * 
     * @param overwrite
     */
    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

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

    public AbstractPdfOutput getOutput() {
        return output;
    }

    public void setOutput(AbstractPdfOutput output) {
        this.output = output;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(overwrite).append(compress).append(version).append(output).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AbstractParameters)) {
            return false;
        }
        AbstractParameters parameter = (AbstractParameters) other;
        return new EqualsBuilder().append(overwrite, parameter.isOverwrite()).append(compress, parameter.isCompress())
                .append(version, parameter.getVersion()).append(output, parameter.getOutput()).isEquals();
    }
}
