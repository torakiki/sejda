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

import org.sejda.model.pdf.MinRequiredVersion;
import org.sejda.model.pdf.PdfVersion;

/**
 * Possible Layout entries with boolean value.<br>
 * Pdf reference 1.7, TABLE 8.1 Entries in a viewer preferences dictionary
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfBooleanPreference implements MinRequiredVersion {
    HIDE_TOOLBAR(PdfVersion.VERSION_1_2),
    HIDE_MENUBAR(PdfVersion.VERSION_1_2),
    HIDE_WINDOW_UI(PdfVersion.VERSION_1_2),
    FIT_WINDOW(PdfVersion.VERSION_1_2),
    CENTER_WINDOW(PdfVersion.VERSION_1_2),
    DISPLAY_DOC_TITLE(PdfVersion.VERSION_1_4);

    private PdfVersion minVersion;

    private PdfBooleanPreference(PdfVersion minVersion) {
        this.minVersion = minVersion;
    }

    @Override
    public PdfVersion getMinVersion() {
        return minVersion;
    }
}
