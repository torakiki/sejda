/*
 * Created on 16/set/2010
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
package org.sejda.model.pdf.encryption;

import org.sejda.common.FriendlyNamed;
import org.sejda.model.pdf.MinRequiredVersion;
import org.sejda.model.pdf.PdfVersion;

/**
 * Possible encryption algorithm to use during pdf encryption.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfEncryption implements MinRequiredVersion, FriendlyNamed {
    STANDARD_ENC_40("rc4_40", PdfVersion.VERSION_1_2),
    STANDARD_ENC_128("rc4_128", PdfVersion.VERSION_1_2),
    AES_ENC_128("aes_128", PdfVersion.VERSION_1_6);

    private PdfVersion minVersion;
    private String displayName;

    private PdfEncryption(String displayName, PdfVersion minVersion) {
        this.displayName = displayName;
        this.minVersion = minVersion;
    }

    public PdfVersion getMinVersion() {
        return minVersion;
    }

    public String getFriendlyName() {
        return displayName;
    }
}
