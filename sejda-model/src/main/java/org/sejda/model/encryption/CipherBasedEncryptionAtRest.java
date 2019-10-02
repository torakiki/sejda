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
package org.sejda.model.encryption;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CipherBasedEncryptionAtRest implements EncryptionAtRestPolicy {
    private CipherSupplier cipherSupplier;

    public CipherBasedEncryptionAtRest(CipherSupplier cipherSupplier) {
        this.cipherSupplier = cipherSupplier;
    }

    @Override
    public InputStream decrypt(InputStream in) {
        return new CipherInputStream(in, cipherSupplier.get(Cipher.DECRYPT_MODE));
    }

    @Override
    public OutputStream encrypt(OutputStream out) {
        return new CipherOutputStream(out, cipherSupplier.get(Cipher.ENCRYPT_MODE));
    }
}
