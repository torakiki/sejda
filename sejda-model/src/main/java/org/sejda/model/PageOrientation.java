/*
 * Copyright 2017 by Edi Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.model;

public enum PageOrientation implements FriendlyNamed {
    PORTRAIT("portrait"),
    LANDSCAPE("landscape"),
    AUTO("auto");

    private final String name;

    PageOrientation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getFriendlyName() {
        return getName();
    }
}
