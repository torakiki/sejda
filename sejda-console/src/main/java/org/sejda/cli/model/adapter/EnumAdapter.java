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
package org.sejda.cli.model.adapter;

import org.sejda.core.DisplayNamedEnum;

/**
 * Base class for adapters for sejda {@link Enum}s. Provides common boiler plate code to initialize the actual enum value from it's user friendly display name
 * 
 * @author Eduard Weissmann
 * 
 */
// TODO: add support for ParameterizedTypeImpl in jewel-cli and reduce all classes extending this to one single class
class EnumAdapter<T extends DisplayNamedEnum> {

    private final T enumValue;

    public EnumAdapter(String userFriendlyName, Class<T> enumClass, String enumClassDescription) {
        this.enumValue = EnumUtils.valueOf(enumClass, userFriendlyName, enumClassDescription);
    }

    public T getEnumValue() {
        return enumValue;
    }
}
