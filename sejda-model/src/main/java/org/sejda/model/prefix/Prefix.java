/*
 * Created on 15/ott/2013
 * Copyright 2013 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.prefix;

import org.sejda.common.FriendlyNamed;

/**
 * Enumerates the available complex prefixes that Sejda can handle. A prefix has a friendly name that can be used to build help message or UI menus.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum Prefix implements FriendlyNamed {

    BASENAME("[BASENAME]"),
    TIMESTAMP("[TIMESTAMP]"),
    CURRENTPAGE("[CURRENTPAGE]"),
    FILENUMBER("[FILENUMBER]"),
    BOOKMARK("[BOOKMARK_NAME]"),
    BOOKMARK_STRICT("[BOOKMARK_NAME_STRICT]");

    private String name;

    private Prefix(String name) {
        this.name = name;
    }

    public String getFriendlyName() {
        return name;
    }
}
