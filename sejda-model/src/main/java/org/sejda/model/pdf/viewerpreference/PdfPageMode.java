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
import org.sejda.model.pdf.MinRequiredVersion;
import org.sejda.model.pdf.PdfVersion;

/**
 * Possible values to specify how the document should be displayed when opened.<br>
 * Pdf reference 1.7, TABLE 3.25 Entries in the catalog dictionary
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfPageMode implements MinRequiredVersion, FriendlyNamed {
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

    @Override
    public String getFriendlyName() {
        return displayName;
    }

    @Override
    public PdfVersion getMinVersion() {
        return minVersion;
    }
}
