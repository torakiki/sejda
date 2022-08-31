/*
 * Created on 16 feb 2016
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
package org.sejda.model.toc;

import org.sejda.model.FriendlyNamed;

/**
 * Possible ToC creation policies to use in tasks that may require a ToC
 * 
 * @author Andrea Vacondio
 *
 */
public enum ToCPolicy implements FriendlyNamed {
    NONE("none"),
    FILE_NAMES("file_names"),
    DOC_TITLES("doc_titles");

    private String displayName;

    private ToCPolicy(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }
}
