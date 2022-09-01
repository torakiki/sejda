/*
 * Created on 16/set/2010
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
package org.sejda.model.pdf.encryption;

import org.sejda.model.FriendlyNamed;

/**
 * Access permissions correspond to various operations that can be allowed/disallowed when encrypting a pdf document.<br>
 * Pdf reference 1.7, section 3.5.2
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfAccessPermission implements FriendlyNamed {
    PRINT("print", 0b00000000000000000000000000000100),
    MODIFY("modify", 0b00000000000000000000000000001000),
    COPY_AND_EXTRACT("copy", 0b00000000000000000000000000010000),
    ANNOTATION("modifyannotations", 0b00000000000000000000000000100000),
    FILL_FORMS("fill", 0b00000000000000000000000100000000),
    @Deprecated // as of PDF2.0 this is always 1
    EXTRACTION_FOR_DISABLES("screenreaders", 0b00000000000000000000001000000000),
    ASSEMBLE("assembly", 0b00000000000000000000010000000000),
    DEGRADATED_PRINT("degradedprinting", 0b00000000000000000000100000000000);

    private final String displayName;
    public final int bits;

    PdfAccessPermission(String displayName, int bits) {
        this.displayName = displayName;
        this.bits = bits;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }
}
