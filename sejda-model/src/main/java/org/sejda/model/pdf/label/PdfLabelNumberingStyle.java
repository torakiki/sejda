/*
 * Created on 02/gen/2011
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
package org.sejda.model.pdf.label;

import org.sejda.model.FriendlyNamed;

/**
 * Possible values for a numbering style to be used for page labels.<br>
 * Pdf reference 1.7, TABLE 8.10 Entries in a page label dictionary
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfLabelNumberingStyle implements FriendlyNamed {
    ARABIC("arabic"),
    UPPERCASE_ROMANS("uroman"),
    LOWERCASE_ROMANS("lroman"),
    UPPERCASE_LETTERS("uletter"),
    LOWERCASE_LETTERS("lletter"),
    EMPTY("empty");

    private final String displayName;

    PdfLabelNumberingStyle(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }
}
