/*
 * Created on 17/set/2010
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.input.AbstractPdfSource;

/**
 * Base parameters class for manipulations with a single {@link AbstractPdfSource}
 * 
 * @author Andrea Vacondio
 * 
 */
abstract class SinglePdfSourceParameters extends AbstractPdfOutputParameters implements SinglePdfSourceTaskParameters {

    @Valid
    @NotNull
    private AbstractPdfSource source;

    public AbstractPdfSource getSource() {
        return source;
    }

    public void setSource(AbstractPdfSource source) {
        this.source = source;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(source).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SinglePdfSourceParameters)) {
            return false;
        }
        SinglePdfSourceParameters parameter = (SinglePdfSourceParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(source, parameter.getSource()).isEquals();
    }

}
