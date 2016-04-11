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
package org.sejda.model.outline;

import java.util.ArrayList;
import java.util.List;

/**
 * Used in extracting docs by outline. Keeps track of the extraction points and the associated page destination
 */
public class OutlineExtractPageDestinations {

    public final List<OutlineItemBoundaries> sections = new ArrayList<>();

    public void add(int startPage, String title, int endPage) {
        sections.add(new OutlineItemBoundaries(startPage, title, endPage));
    }

    public static class OutlineItemBoundaries {
        public int startPage;
        public final String title;
        public final int endPage;

        public OutlineItemBoundaries(int startPage, String title, int endPage) {
            this.startPage = startPage;
            this.title = title;
            this.endPage = endPage;
        }
    }
}
