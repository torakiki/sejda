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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.sejda.core.manipulation.model.rotation.PageRotation;

/**
 * Parameter class for the rotation manipulation. Accepts a list of {@link org.sejda.core.manipulation.model.input.PdfSource} where the {@link PageRotation} will be applied.
 * 
 * @author Andrea Vacondio
 * 
 */
public class RotateParameters extends PdfSourceListParameters {

    private static final long serialVersionUID = 8834767589689764537L;

    private String outputPrefix = "";
    @Valid
    @NotNull
    private PageRotation rotation = null;

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public PageRotation getRotation() {
        return rotation;
    }

    public void setRotation(PageRotation rotation) {
        this.rotation = rotation;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(outputPrefix).append(rotation).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof RotateParameters)) {
            return false;
        }
        RotateParameters parameter = (RotateParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(outputPrefix, parameter.getOutputPrefix())
                .append(rotation, parameter.getRotation()).isEquals();
    }
}
