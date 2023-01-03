package org.sejda.model.scale;
/*
 * Created on 03/01/23
 * Copyright 2023 Sober Lemur S.r.l. and Sejda BV
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

import org.sejda.model.FriendlyNamed;

/**
 * @author Andrea Vacondio
 */
public enum PageNormalizationPolicy implements FriendlyNamed {
    NONE("none"),
    // Normalize all pages to have the same width as the first page. Pages are scaled, so the aspect ratio is preserved.
    // Eg: a doc with first 2 pages A4 and next ones A5 will be changed to all pages are A4
    SAME_WIDTH("same_width"),
    // Normalize all pages to have the same width as the first page.
    // Any page with a mismatched orientation is scaled based on the ratio between the first page height and the page width
    SAME_WIDTH_ORIENTATION_BASED("same_width_orientation_based");

    private final String displayName;

    PageNormalizationPolicy(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }

}
