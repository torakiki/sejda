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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractParameters implements TaskParameters {

    private boolean overwrite = false;

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

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(overwrite).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractParameters)) {
            return false;
        }
        AbstractParameters parameter = (AbstractParameters) other;
        return new EqualsBuilder().append(overwrite, parameter.isOverwrite()).isEquals();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}