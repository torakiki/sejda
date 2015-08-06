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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.pdf.encryption;

import org.sejda.common.FriendlyNamed;

/**
 * Access permissions correspond to various operations that can be allowed/disallowed when encrypting a pdf document.<br>
 * Pdf reference 1.7, section 3.5.2
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfAccessPermission implements FriendlyNamed {
    MODIFY("modify"),
    COPY_AND_EXTRACT("copy"),
    ANNOTATION("modifyannotations"),
    PRINT("print"),
    FILL_FORMS("fill"),
    ASSEMBLE("assembly"),
    DEGRADATED_PRINT("degradedprinting"),
    EXTRACTION_FOR_DISABLES("screenreaders");

    private String displayName;

    private PdfAccessPermission(String displayName) {
        this.displayName = displayName;
    }

    public String getFriendlyName() {
        return displayName;
    }
}
