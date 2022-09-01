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
package org.sejda.model.parameter.base;

import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.sejda.model.output.ExistingOutputPolicy;

/**
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractParameters implements TaskParameters {

    @NotNull
    private ExistingOutputPolicy existingOutputPolicy = ExistingOutputPolicy.FAIL;
    private boolean lenient = false;

    @Override
    public ExistingOutputPolicy getExistingOutputPolicy() {
        return existingOutputPolicy;
    }

    @Override
    public void setExistingOutputPolicy(ExistingOutputPolicy existingOutputPolicy) {
        this.existingOutputPolicy = existingOutputPolicy;
    }

    @Override
    public boolean isLenient() {
        return lenient;
    }

    @Override
    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(existingOutputPolicy).append(lenient).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractParameters parameter)) {
            return false;
        }
        return new EqualsBuilder().append(existingOutputPolicy, parameter.existingOutputPolicy)
                .append(lenient, parameter.lenient).isEquals();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}