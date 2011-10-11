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
 * Possible values for the page layout to be used when the document is opened.<br>
 * Pdf reference 1.7, TABLE 3.25 Entries in the catalog dictionary
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfPageLayout implements MinRequiredVersion, DisplayNamedEnum {
    SINGLE_PAGE("singlepage", PdfVersion.VERSION_1_2),
    ONE_COLUMN("onecolumn", PdfVersion.VERSION_1_2),
    TWO_COLUMN_LEFT("twocolumnl", PdfVersion.VERSION_1_2),
    TWO_COLUMN_RIGHT("twocolumnr", PdfVersion.VERSION_1_2),
    TWO_PAGE_LEFT("twopagel", PdfVersion.VERSION_1_5),
    TWO_PAGE_RIGHT("twopager", PdfVersion.VERSION_1_5);

    private PdfVersion minVersion;
    private String displayName;

    private PdfPageLayout(String displayName, PdfVersion minVersion) {
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
