/*
 * Created on Oct 4, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import org.sejda.common.FriendlyNamed;

/**
 * Base class for adapters for sejda {@link Enum}s. Provides common boiler plate code to initialize the actual enum value from it's user friendly display name
 * 
 * @author Eduard Weissmann
 * @param <T>
 *            type of the enum we are converting
 */
// TODO: add support for ParameterizedTypeImpl in jewel-cli and reduce all classes extending this to one single class
public class EnumAdapter<T extends Enum<?> & FriendlyNamed> {

    private final T enumValue;

    public EnumAdapter(String userFriendlyName, Class<T> enumClass, String enumClassDescription) {
        this.enumValue = EnumUtils.valueOf(enumClass, userFriendlyName, enumClassDescription);
    }

    public T getEnumValue() {
        return enumValue;
    }
}
