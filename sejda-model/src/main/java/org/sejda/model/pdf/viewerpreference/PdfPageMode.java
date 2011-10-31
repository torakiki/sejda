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

import org.sejda.common.DisplayNamedEnum;
import org.sejda.model.pdf.MinRequiredVersion;
import org.sejda.model.pdf.PdfVersion;

/**
 * Possible values to specify how the document should be displayed when opened.<br>
 * Pdf reference 1.7, TABLE 3.25 Entries in the catalog dictionary
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfPageMode implements MinRequiredVersion, DisplayNamedEnum {
    USE_NONE("none", PdfVersion.VERSION_1_2),
    USE_OUTLINES("outlines", PdfVersion.VERSION_1_2),
    USE_THUMBS("thumbs", PdfVersion.VERSION_1_2),
    FULLSCREEN("fullscreen", PdfVersion.VERSION_1_2),
    USE_OC("ocontent", PdfVersion.VERSION_1_5),
    USE_ATTACHMENTS("attachments", PdfVersion.VERSION_1_6);

    private PdfVersion minVersion;
    private String displayName;

    private PdfPageMode(String displayName, PdfVersion minVersion) {
        this.displayName = displayName;
        this.minVersion = minVersion;
    }

    public String getDisplayName() {
        return displayName;
    }

    public PdfVersion getMinVersion() {
        return minVersion;
    }
}
