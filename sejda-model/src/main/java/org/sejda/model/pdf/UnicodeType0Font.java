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
    NOTO_SANS_REGULAR("/fonts/sans/NotoSans-Regular.ttf");

    private String resource;

    UnicodeType0Font(String resource) {
        this.resource = resource;
    }

    public InputStream getResourceStream() {
        return this.getClass().getResourceAsStream(resource);
    }
}
