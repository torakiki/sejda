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
package org.sejda.core.manipulation.model.pdf.viewerpreferences;

import org.sejda.core.manipulation.model.pdf.MinRequiredVersion;
import org.sejda.core.manipulation.model.pdf.PdfVersion;
import org.sejda.core.support.DisplayNamedEnum;

/**
 * Possible values for the page scaling option to be selected when a print dialog is displayed for this document.<br>
 * Pdf reference 1.7, TABLE 8.1 Entries in a viewer preferences dictionary
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfPrintScaling implements MinRequiredVersion, DisplayNamedEnum {
    NONE(PdfVersion.VERSION_1_6, "none"),
    APP_DEFAULT(PdfVersion.VERSION_1_6, "app_default");

    private PdfVersion minVersion;
    private String displayName;

    private PdfPrintScaling(PdfVersion minVersion, String displayName) {
        this.minVersion = minVersion;
        this.displayName = displayName;
    }

    public PdfVersion getMinVersion() {
        return minVersion;
    }

    public String getDisplayName() {
        return displayName;
    }
}
