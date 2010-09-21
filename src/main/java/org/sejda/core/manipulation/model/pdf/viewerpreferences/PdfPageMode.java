/*
 * Created on 20/set/2010
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
package org.sejda.core.manipulation.model.pdf.viewerpreferences;

import org.sejda.core.manipulation.model.pdf.MinRequiredVersion;
import org.sejda.core.manipulation.model.pdf.PdfVersion;

/**
 * Possible values to specify how the document should be displayed when opened.<br>
 * Pdf reference 1.7, TABLE 3.25 Entries in the catalog dictionary
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfPageMode implements MinRequiredVersion {
    USE_NONE(PdfVersion.VERSION_1_2),
    USE_OUTLINES(PdfVersion.VERSION_1_2),
    USE_THUMBS(PdfVersion.VERSION_1_2),
    FULLSCREEN(PdfVersion.VERSION_1_2),
    USE_OC(PdfVersion.VERSION_1_5),
    USE_ATTACHMENTS(PdfVersion.VERSION_1_6);

    private PdfVersion minVersion;

    private PdfPageMode(PdfVersion minVersion) {
        this.minVersion = minVersion;
    }

    public PdfVersion getMinVersion() {
        return minVersion;
    }
}
