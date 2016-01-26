/*
 * Created on 22 gen 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.pdf.collection;

import org.sejda.common.FriendlyNamed;

/**
 * Possible initial view values of a collection dictionary
 * 
 * @author Andrea Vacondio
 *
 */
public enum InitialView implements FriendlyNamed {
    DETAILS("details", "D"),
    TILES("tiles", "T"),
    HIDDEN("hidden", "H");

    private String displayName;
    public final String value;

    private InitialView(String displayName, String value) {
        this.displayName = displayName;
        this.value = value;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }
}
