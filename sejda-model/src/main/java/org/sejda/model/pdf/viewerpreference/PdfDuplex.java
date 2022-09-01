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
 * Possible values for the paper handling option to use when printing the file from the print dialog.<br>
 * Pdf reference 1.7, TABLE 8.1 Entries in a viewer preferences dictionary
 *
 * @author Andrea Vacondio
 *
 */
public enum PdfDuplex implements MinRequiredVersion, FriendlyNamed {
    SIMPLEX("simplex", PdfVersion.VERSION_1_7),
    DUPLEX_FLIP_SHORT_EDGE("duplex_flip_short_edge", PdfVersion.VERSION_1_7),
    DUPLEX_FLIP_LONG_EDGE("duplex_flip_long_edge", PdfVersion.VERSION_1_7);

    private final PdfVersion minVersion;
    private final String displayName;

    PdfDuplex(String displayName, PdfVersion minVersion) {
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
