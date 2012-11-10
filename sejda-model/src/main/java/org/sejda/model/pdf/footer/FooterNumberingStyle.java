/*
 * Created on 02/gen/2011
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
package org.sejda.model.pdf.footer;

import org.sejda.common.DisplayNamedEnum;

/**
 * Numbering styles for footer labels.
 *
 * @author Eduard Weissmann
 * 
 */
public enum FooterNumberingStyle implements DisplayNamedEnum {
    // TODO: maybe the roman and alphabetic stuff too someday
    ARABIC("arabic"),
    EMPTY("empty"); // no page number is added

    private String displayName;

    private FooterNumberingStyle(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
