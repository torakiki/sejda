/*
 * Created on Oct 4, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.conversion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.support.DisplayNamedEnum;

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
     * @return Returns the enum value matching the input displayName, belonging to the specified enum class<br/>
     *         Does not throw an exception if enum value is not found, returns null
     */
    public static <T extends DisplayNamedEnum> T valueOfSilently(Class<T> enumClass, String displayName) {
        for (T each : enumClass.getEnumConstants()) {
            if (StringUtils.equalsIgnoreCase(each.getDisplayName(), displayName)) {
                return each;
            }
        }

        return null;
    }

    /**
     * @param enumClass
     * @param displayName
     * @param describedEnumClass
     * @return Returns the enum value matching the input displayName, belonging to the specified enum class<br/>
     * @throws SejdaRuntimeException
     *             if enum value is not found, mentioning the valid values in the message
     */
    public static <T extends DisplayNamedEnum> T valueOf(Class<T> enumClass, String displayName,
            String describedEnumClass) {
        T result = valueOfSilently(enumClass, displayName);

        if (result == null) {
            throw new SejdaRuntimeException("Invalid value '" + displayName + "' for " + describedEnumClass
                    + ". Valid values are '" + StringUtils.join(findValidValues(enumClass), "', '") + "'");
        }

        return result;
    }

    private static Collection<String> findValidValues(Class<? extends DisplayNamedEnum> enumClass) {
        List<String> result = new ArrayList<String>();

        for (DisplayNamedEnum each : enumClass.getEnumConstants()) {
            result.add(each.getDisplayName());
        }

        Collections.sort(result);
        return result;
    }

}
