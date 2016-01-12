/*
 * Created on 17/set/2010
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
package org.sejda.model.parameter;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.pdf.encryption.PdfEncryption;
import org.sejda.model.validation.constraint.HasAPassword;

/**
 * Parameters for the encrypt manipulation. Accepts a list of {@link org.sejda.model.input.PdfSource} that will be encrypted using the same parameters.
 * 
 * @author Andrea Vacondio
 * 
 */
@HasAPassword
public class EncryptParameters extends MultiplePdfSourceMultipleOutputParameters {

    private String ownerPassword = "";
    private String userPassword = "";
    @NotNull
    private PdfEncryption encryptionAlgorithm = PdfEncryption.AES_ENC_256;
    private final Set<PdfAccessPermission> permissions = EnumSet.noneOf(PdfAccessPermission.class);

    public EncryptParameters(PdfEncryption encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
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
    public PdfVersion getMinRequiredPdfVersion() {
        return PdfVersion.getMax(super.getMinRequiredPdfVersion(), encryptionAlgorithm.getMinVersion());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(userPassword).append(ownerPassword)
                .append(encryptionAlgorithm).append(permissions).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EncryptParameters)) {
            return false;
        }
        EncryptParameters parameter = (EncryptParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(userPassword, parameter.getUserPassword())
                .append(ownerPassword, parameter.getOwnerPassword())
                .append(encryptionAlgorithm, parameter.getEncryptionAlgorithm())
                .append(permissions, parameter.getPermissions()).isEquals();
    }
}
