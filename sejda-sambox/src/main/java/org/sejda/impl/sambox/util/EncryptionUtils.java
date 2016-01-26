/*
 * Created on 11 gen 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.util;

import java.util.Set;

import org.sejda.model.parameter.EncryptParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.pdf.encryption.PdfEncryption;
import org.sejda.sambox.encryption.StandardSecurity;
import org.sejda.sambox.encryption.StandardSecurityEncryption;
import org.sejda.sambox.pdmodel.encryption.AccessPermission;

/**
 * Utility methods related to PDF encryption
 * 
 * @author Andrea Vacondio
 *
 */
public final class EncryptionUtils {
    private EncryptionUtils() {
        // hide
    }

    /**
     * @param params
     * @return the {@link StandardSecurity} corresponding to the {@link EncryptParameters}
     */
    public static StandardSecurity securityFromParams(EncryptParameters params) {
        return new StandardSecurity(params.getOwnerPassword(), params.getUserPassword(),
                getEncryptionFrom(params.getEncryptionAlgorithm()), getPermissionsFrom(params.getPermissions()), true);
    }

    private static AccessPermission getPermissionsFrom(Set<PdfAccessPermission> permissions) {
        int perm = 0b11111111111111111111001000000000;
        for (PdfAccessPermission permission : permissions) {
            perm |= permission.bits;
        }
        return new AccessPermission(perm);
    }

    private static StandardSecurityEncryption getEncryptionFrom(PdfEncryption encryptionAlgorithm) {
        switch (encryptionAlgorithm) {
        case STANDARD_ENC_40:
        case STANDARD_ENC_128:
            return StandardSecurityEncryption.ARC4_128;
        case AES_ENC_128:
            return StandardSecurityEncryption.AES_128;
        default:
            return StandardSecurityEncryption.AES_256;
        }
    }
}
