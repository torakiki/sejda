/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.model.repaginate;

import org.sejda.model.FriendlyNamed;

public enum Repagination implements FriendlyNamed {
    /**
     * This repagination is useful in scenarios such as two page layout scans from unstapled booklets
     * (staples removed from the booklet and then the double pages scanned front/back)
     *
     * First scanned page contains the last and first covers (hence the name), second scanned page contains second and before-last page, and so on.
     * Example for a 10 pages booklet scanned in this manner: (10,1) (2,9) (8,3) (4,7) (6,5)
     *
     * Splitting the two-page layout document down the middle would result in: 10,1,2,9,8,3,4,7,6,5
     * Applying the last-first repagination in this case would order the pages as a reader would expect them: 1,2,3,4,5,6,7,8,9,10
     */
    LAST_FIRST("last-first"),
    NONE("none");

    private final String displayName;

    Repagination(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }
}

