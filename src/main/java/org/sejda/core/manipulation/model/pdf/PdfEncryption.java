/*
 * Created on 16/set/2010
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
package org.sejda.core.manipulation.model.pdf;

/**
 * Possible encryption algorithm to use during pdf encryption.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfEncryption implements MinRequiredVersion {
    STANDARD_ENC_40(PdfVersion.VERSION_1_2),
    STANDARD_ENC_128(PdfVersion.VERSION_1_2),
    AES_ENC_128(PdfVersion.VERSION_1_6);

    private PdfVersion minVersion;

    private PdfEncryption(PdfVersion minVersion) {
        this.minVersion = minVersion;
    }

    public PdfVersion getMinVersion() {
        return minVersion;
    }
}
