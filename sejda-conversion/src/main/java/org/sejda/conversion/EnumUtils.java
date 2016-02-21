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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.sejda.common.FriendlyNamed;
import org.sejda.model.exception.SejdaRuntimeException;

/**
 * Utilities related to {@link Enum}s
 * 
 * @author Eduard Weissmann
 * 
 */
public final class EnumUtils {

    private EnumUtils() {
        // no instances
    }

    /**
     * @param enumClass
     * @param displayName
     * @return Returns the enum value matching the input displayName, belonging to the specified enum class
     *         Does not throw an exception if enum value is not found, returns null
     */
    public static <T extends Enum<?> & FriendlyNamed> T valueOfSilently(Class<T> enumClass, String displayName) {
        for (T each : enumClass.getEnumConstants()) {
            if (StringUtils.equalsIgnoreCase(each.getFriendlyName(), displayName)) {
                return each;
            }
        }

        return null;
    }

    /**
     * @param enumClass
     * @param displayName
     * @param describedEnumClass
     * @return Returns the enum value matching the input displayName, belonging to the specified enum class
     * @throws SejdaRuntimeException
     *             if enum value is not found, mentioning the valid values in the message
     */
    public static <T extends Enum<?> & FriendlyNamed> T valueOf(Class<T> enumClass, String displayName,
            String describedEnumClass) {
        T result = valueOfSilently(enumClass, displayName);

        if (result == null) {
            throw new SejdaRuntimeException("Invalid value '" + displayName + "' for " + describedEnumClass
                    + ". Valid values are '" + StringUtils.join(findValidValues(enumClass), "', '") + "'");
        }

        return result;
    }

    private static <T extends Enum<?> & FriendlyNamed> Collection<String> findValidValues(Class<T> enumClass) {
        List<String> result = new ArrayList<>();

        for (FriendlyNamed each : enumClass.getEnumConstants()) {
            result.add(each.getFriendlyName());
        }

        Collections.sort(result);
        return result;
    }

}
