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
package org.sejda.impl.sambox.component;

public class OutlineItem {
    public final String title;
    public final int page;
    public final int level;
    // Sometimes when you click an outline item it goes to the beginning of the page,
    // some other times it goes to a specific page location (eg: 3rd paragraph title)
    // name comes from PDPageXYZDestination
    public final boolean xyzDestination;

    public OutlineItem(String title, int page, int level, boolean xyzDestination) {
        this.title = title;
        this.page = page;
        this.level = level;
        this.xyzDestination = xyzDestination;
    }
}
