/*
 * Created on 18/apr/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
                        return ((Class) type);
                    }
                }
            }
        }
        return null;
    }

}
