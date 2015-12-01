/*
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


import java.io.InputStream;

public enum UnicodeType0Font {
    SERIF("/fonts/free-serif/FreeSerif.ttf"),
    SERIF_BOLD("/fonts/free-serif/FreeSerifBold.ttf"),
    SERIF_ITALIC("/fonts/free-serif/FreeSerifItalic.ttf"),
    SERIF_BOLD_ITALIC("/fonts/free-serif/FreeSerifBoldItalic.ttf"),
    SANS("/fonts/free-sans/FreeSans.ttf"),
    SANS_BOLD("/fonts/free-sans/FreeSansBold.ttf"),
    SANS_OBLIQUE("/fonts/free-sans/FreeSansOblique.ttf"),
    SANS_BOLD_OBLIQUE("/fonts/free-sans/FreeSansBoldOblique.ttf"),
    MONO("/fonts/free-mono/FreeMono.ttf"),
    MONO_BOLD("/fonts/free-mono/FreeMonoBold.ttf"),
    MONO_OBLIQUE("/fonts/free-mono/FreeMonoOblique.ttf"),
    MONO_BOLD_OBLIQUE("/fonts/free-mono/FreeMonoBoldOblique.ttf")
    ;

    private String resource;

    private UnicodeType0Font(String resource) {
        this.resource = resource;
    }

    public InputStream getResourceStream() {
        return this.getClass().getResourceAsStream(resource);
    }
}
