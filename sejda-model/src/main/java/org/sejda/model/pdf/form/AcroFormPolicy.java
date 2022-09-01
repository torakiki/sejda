/*
 * Created on 06/set/2015
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
package org.sejda.model.pdf.form;

import org.sejda.model.FriendlyNamed;

/**
 * Enumerates policies that a task can use to handle the interactive forms(AcroForms). This is mostly relevant when merging documents but it can be used for other tasks as well.
 * 
 * @author Andrea Vacondio
 *
 */
public enum AcroFormPolicy implements FriendlyNamed {
    DISCARD("discard"),
    MERGE("merge"),
    MERGE_RENAMING_EXISTING_FIELDS("merge_renaming"),
    FLATTEN("flatten");

    private final String displayName;

    AcroFormPolicy(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }
}
