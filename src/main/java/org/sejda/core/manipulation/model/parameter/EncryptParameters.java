/*
 * Created on 17/set/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.manipulation.model.parameter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.sejda.core.manipulation.model.pdf.PdfAccessPermission;
import org.sejda.core.manipulation.model.pdf.PdfEncryption;

/**
 * Parameters for the encrypt manipulation. Accepts a list of {@link org.sejda.core.manipulation.model.input.PdfSource} that will be encrypted using the same parameters.
 * 
 * @author Andrea Vacondio
 * 
 */
public class EncryptParameters extends PdfSourceListParameters {

    private static final long serialVersionUID = 611696382902461114L;

    private String outputPrefix = "";
    private String ownerPassword = "";
    private String userPassword = "";
    @NotNull
    private PdfEncryption encryptionAlgorithm = PdfEncryption.STANDARD_ENC_40;
    private Set<PdfAccessPermission> permissions = new HashSet<PdfAccessPermission>();

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public String getOwnerPassword() {
        return ownerPassword;
    }

    public void setOwnerPassword(String ownerPassword) {
        this.ownerPassword = ownerPassword;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public PdfEncryption getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(PdfEncryption encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    /**
     * @return an unmodifiable view of the permissions
     */
    public Set<PdfAccessPermission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    /**
     * clear permissions
     */
    public void clearPermissions() {
        permissions.clear();
    }

    /**
     * adds a permission to the permissions set
     * 
     * @param permission
     */
    public void addPermission(PdfAccessPermission permission) {
        permissions.add(permission);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(outputPrefix).append(userPassword).append(
                ownerPassword).append(encryptionAlgorithm).append(permissions).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof EncryptParameters)) {
            return false;
        }
        EncryptParameters parameter = (EncryptParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(outputPrefix, parameter.getOutputPrefix())
                .append(userPassword, parameter.getUserPassword()).append(ownerPassword, parameter.getOwnerPassword())
                .append(encryptionAlgorithm, parameter.getEncryptionAlgorithm()).append(permissions,
                        parameter.getPermissions()).isEquals();
    }
}
