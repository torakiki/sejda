/*
 * Created on 23 ago 2019
 * Copyright 2019 Sober Lemur S.a.s. di Vacondio Andrea and Sejda BV
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.fonts;

import java.util.Arrays;
import java.util.List;

import org.sejda.model.pdf.font.FontResource;
import org.sejda.model.pdf.font.Type0FontsProvider;

/**
 * @author Andrea Vacondio
 *
 */
public class UnicodeType0FontsProvider implements Type0FontsProvider {

    @Override
    public List<FontResource> getFonts() {
        return Arrays.asList(UnicodeType0Font.NOTO_SANS_MERGED_REGULAR);
    }

}
