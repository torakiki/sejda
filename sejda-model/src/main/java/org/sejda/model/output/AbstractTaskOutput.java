/*
 * Copyright 2019 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.model.output;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.encryption.EncryptionAtRestPolicy;
import org.sejda.model.encryption.NoEncryptionAtRest;

public abstract class AbstractTaskOutput implements TaskOutput {

    private EncryptionAtRestPolicy encryptionAtRestPolicy = NoEncryptionAtRest.INSTANCE;

    @Override
    public EncryptionAtRestPolicy getEncryptionAtRestPolicy() {
        return encryptionAtRestPolicy;
    }

    @Override
    public void setEncryptionAtRestPolicy(EncryptionAtRestPolicy encryptionAtRestSecurity) {
        this.encryptionAtRestPolicy = encryptionAtRestSecurity;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(getEncryptionAtRestPolicy()).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getEncryptionAtRestPolicy()).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractTaskOutput output)) {
            return false;
        }
        return new EqualsBuilder().append(encryptionAtRestPolicy, output.encryptionAtRestPolicy).isEquals();
    }
}
