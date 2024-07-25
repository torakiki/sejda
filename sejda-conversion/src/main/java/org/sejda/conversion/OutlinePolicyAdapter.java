/*
 * Created on 10/giu/2013
 * Copyright 2011 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.conversion;

import org.sejda.model.outline.OutlinePolicy;

/**
 * Adapter class for enum {@link OutlinePolicy}. Provides initialization from string
 * 
 * @author Andrea Vacondio
 * 
 */
public class OutlinePolicyAdapter extends EnumAdapter<OutlinePolicy> {

    public OutlinePolicyAdapter(String userFriendlyName) {
        super(userFriendlyName, OutlinePolicy.class, "bookmarks policy");
    }
}
