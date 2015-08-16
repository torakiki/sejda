/*
 * Created on 18/apr/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.support.util;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Utility class used to infer the parameter type of an input method of an input class
 * 
 * @author Andrea Vacondio
 * 
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
        // hide
    }

    /**
     * Given a concrete class and a method name, it tries to infer the Class of the first parameter of the method
     * 
     * @param clazz
     * @param methodName
     * @return the class or null if nothing found
     */
    @SuppressWarnings("rawtypes")
    public static Class inferParameterClass(Class clazz, String methodName) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && !method.isBridge()) {
                Type[] types = method.getGenericParameterTypes();
                for (Type type : types) {
                    if (type instanceof Class && !((Class) type).isInterface()) {
                        return (Class) type;
                    }
                }
            }
        }
        return null;
    }

}
