/*
 * Created on 08/giu/2013
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.outline;

import org.sejda.common.FriendlyNamed;

/**
 * Enumerates policies that a task can use to handle the outline tree. This is mostly relevant when merging documents but it can be used for other tasks as well.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum OutlinePolicy implements FriendlyNamed {
    DISCARD("discard"),
    RETAIN("retain"),
    ONE_ENTRY_EACH_DOC("one_entry_each_doc");

    private String displayName;

    private OutlinePolicy(String displayName) {
        this.displayName = displayName;
    }

    public String getFriendlyName() {
        return displayName;
    }
}
