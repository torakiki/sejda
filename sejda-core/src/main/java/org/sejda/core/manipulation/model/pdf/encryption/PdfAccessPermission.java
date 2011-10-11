/*
 * Created on 16/set/2010
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
package org.sejda.core.manipulation.model.pdf.encryption;

import org.sejda.core.DisplayNamedEnum;

/**
 * Access permissions correspond to various operations that can be allowed/disallowed when encrypting a pdf document.<br>
 * Pdf reference 1.7, section 3.5.2
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfAccessPermission implements DisplayNamedEnum {
    MODIFY("modify"),
    COPY_AND_EXTRACT("copy"),
    ANNOTATION("modifyannotations"),
    PRINT("print"),
    FILL_FORMS("fill"),
    ASSEMBLE("assembly"),
    DEGRADATED_PRINT("degradedprinting"),
    EXTRACTION_FOR_DISABLES("screenreaders");

    private String displayName;

    private PdfAccessPermission(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
