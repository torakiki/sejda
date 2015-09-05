/*
 * Created on 15/ott/2013
 * Copyright 2013 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.prefix;

import org.sejda.common.FriendlyNamed;

/**
 * Enumerates the available complex prefixes that Sejda can handle. A prefix has a friendly name that can be used to build help message or UI menus.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum Prefix implements FriendlyNamed {

    BASENAME("[BASENAME]"),
    TIMESTAMP("[TIMESTAMP]"),
    CURRENTPAGE("[CURRENTPAGE]"),
    FILENUMBER("[FILENUMBER]"),
    BOOKMARK("[BOOKMARK_NAME]"),
    BOOKMARK_STRICT("[BOOKMARK_NAME_STRICT]");

    private String name;

    private Prefix(String name) {
        this.name = name;
    }

    @Override
    public String getFriendlyName() {
        return name;
    }
}
