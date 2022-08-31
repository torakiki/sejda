/*
 * Created on 08/giu/2013
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
package org.sejda.model.outline;

import org.sejda.model.FriendlyNamed;

/**
 * Enumerates policies that a task can use to handle the outline tree. This is mostly relevant when merging documents but it can be used for other tasks as well.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum OutlinePolicy implements FriendlyNamed {
    DISCARD("discard"),
    RETAIN("retain"),
    ONE_ENTRY_EACH_DOC("one_entry_each_doc"),
    RETAIN_AS_ONE_ENTRY("retain_as_one_entry");

    private String displayName;

    private OutlinePolicy(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }
}
