/*
 * Created on 20/set/2010
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
package org.sejda.model.pdf.viewerpreference;

import org.sejda.model.FriendlyNamed;

/**
 * Possible values for the document’s page mode, specifying how to display the document on exiting full-screen mode.<br>
 * According to the reference: <i>This entry is meaningful only if the value of the PageMode entry in the catalog dictionary (see Section 3.6.1, “Document Catalog”) is FullScreen;
 * it is ignored otherwise. Default value: UseNone.</i><br>
 * Pdf reference 1.7, TABLE 8.1 Entries in a viewer preferences dictionary
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfNonFullScreenPageMode implements FriendlyNamed {
    USE_NONE("nfsnone"),
    USE_OUTLINES("nfsoutlines"),
    USE_THUMNS("nfsthumbs"),
    USE_OC("nfsocontent");

    private String displayName;

    private PdfNonFullScreenPageMode(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }
}
