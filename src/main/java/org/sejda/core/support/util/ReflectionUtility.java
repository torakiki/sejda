/*
 * Created on 18/apr/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
public final class ReflectionUtility {

    private ReflectionUtility() {
        // hide
    }

    /**
     * Given a concrete class and a method name, it tries to infer the Class of the first parameter of the method
     * 
     * @param clazz
     * @param methodName
     * @return the class or null if nothing found
     */
    @SuppressWarnings("unchecked")
    public static Class inferParameterClass(Class clazz, String methodName) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Type[] types = method.getGenericParameterTypes();
                for (Type type : types) {
                    if (!((Class) type).isInterface()) {
                        return ((Class) type);
                    }
                }
            }
        }
        return null;
    }
}
