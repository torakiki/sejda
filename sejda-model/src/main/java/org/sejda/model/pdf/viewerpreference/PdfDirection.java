/*
 * Created on 20/set/2010
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
package org.sejda.model.pdf.viewerpreference;

import org.sejda.common.FriendlyNamed;
import org.sejda.model.pdf.MinRequiredVersion;
import org.sejda.model.pdf.PdfVersion;

/**
 * Possible values for the predominant reading order for text. <br>
 * Pdf reference 1.7, TABLE 8.1 Entries in a viewer preferences dictionary
 * 
 * @author Andrea Vacondio
 */
public enum PdfDirection implements MinRequiredVersion, FriendlyNamed {
    LEFT_TO_RIGHT("l2r", PdfVersion.VERSION_1_3),
    RIGHT_TO_LEFT("r2l", PdfVersion.VERSION_1_3);

    private PdfVersion minVersion;
    private String displayName;

    private PdfDirection(String displayName, PdfVersion minVersion) {
        this.displayName = displayName;
        this.minVersion = minVersion;
    }

    public String getFriendlyName() {
        return displayName;
    }

    public PdfVersion getMinVersion() {
        return minVersion;
    }
}
