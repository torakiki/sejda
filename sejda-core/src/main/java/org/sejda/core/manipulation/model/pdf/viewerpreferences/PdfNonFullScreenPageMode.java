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

import org.sejda.core.support.DisplayNamedEnum;

/**
 * Possible values for the document’s page mode, specifying how to display the document on exiting full-screen mode.<br>
 * According to the reference: <i>This entry is meaningful only if the value of the PageMode entry in the catalog dictionary (see Section 3.6.1, “Document Catalog”) is FullScreen;
 * it is ignored otherwise. Default value: UseNone.</i><br>
 * Pdf reference 1.7, TABLE 8.1 Entries in a viewer preferences dictionary
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfNonFullScreenPageMode implements DisplayNamedEnum {
    USE_NONE("nfsnone"),
    USE_OUTLINES("nfsoutlines"),
    USE_THUMNS("nfsthumbs"),
    USE_OC("nfsocontent");

    private String displayName;

    private PdfNonFullScreenPageMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
