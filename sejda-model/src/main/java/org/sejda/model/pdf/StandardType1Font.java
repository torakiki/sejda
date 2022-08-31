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

import org.sejda.model.FriendlyNamed;

/**
 * Standard font type 1 fonts.<br>
 * Pdf reference 1.7, section 5.5.1
 *
 * @author Andrea Vacondio
 *
 */
public enum StandardType1Font implements FriendlyNamed {

    TIMES_ROMAN("Times-Roman"),
    TIMES_BOLD("Times-Bold"),
    TIMES_ITALIC("Times-Italic"),
    TIMES_BOLD_ITALIC("Times-BoldItalic"),
    HELVETICA("Helvetica"),
    HELVETICA_BOLD("Helvetica-Bold"),
    HELVETICA_OBLIQUE("Helvetica-Oblique"),
    HELVETICA_BOLD_OBLIQUE("Helvetica-BoldOblique"),
    CURIER("Courier"),
    CURIER_BOLD("Courier-Bold"),
    CURIER_OBLIQUE("Courier-Oblique"),
    CURIER_BOLD_OBLIQUE("Courier-BoldOblique"),
    SYMBOL("Symbol"),
    ZAPFDINGBATS("ZapfDingbats");

    private String displayName;

    private StandardType1Font(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }
}
