/*
 * Created on 13/nov/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.pdf;

import org.sejda.common.FriendlyNamed;
import static org.sejda.model.pdf.UnicodeType0Font.*;

/**
 * Standard font type 1 fonts.<br>
 * Pdf reference 1.7, section 5.5.1
 * 
 * @author Andrea Vacondio
 * 
 */
public enum StandardType1Font implements FriendlyNamed {

    TIMES_ROMAN("Times-Roman", SERIF),
    TIMES_BOLD("Times-Bold", SERIF_BOLD),
    TIMES_ITALIC("Times-Italic", SERIF_ITALIC),
    TIMES_BOLD_ITALIC("Times-BoldItalic", SERIF_BOLD_ITALIC),
    HELVETICA("Helvetica", SANS),
    HELVETICA_BOLD("Helvetica-Bold", SANS_BOLD),
    HELVETICA_OBLIQUE("Helvetica-Oblique", SANS_OBLIQUE),
    HELVETICA_BOLD_OBLIQUE("Helvetica-BoldOblique", SANS_BOLD_OBLIQUE),
    CURIER("Courier", MONO),
    CURIER_BOLD("Courier-Bold", MONO_BOLD),
    CURIER_OBLIQUE("Courier-Oblique", MONO_OBLIQUE),
    CURIER_BOLD_OBLIQUE("Courier-BoldOblique", MONO_BOLD_OBLIQUE),
    SYMBOL("Symbol", null),
    ZAPFDINGBATS("ZapfDingbats", null);

    private String displayName;
    private UnicodeType0Font alternative;

    private StandardType1Font(String displayName, UnicodeType0Font alternative) {
        this.displayName = displayName;
        this.alternative = alternative;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }

    public UnicodeType0Font getUnicodeAlternative() {
        return alternative;
    }
}
