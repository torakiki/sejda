/*
 * Created on 13/nov/2012
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
package org.sejda.model.pdf;

import org.sejda.common.DisplayNamedEnum;

/**
 * Standard font type 1 fonts.<br>
 * Pdf reference 1.7, section 5.5.1
 * 
 * @author Andrea Vacondio
 * 
 */
public enum StandardType1Font implements DisplayNamedEnum {

    TIMES_ROMAN("Times−Roman"),
    TIMES_BOLD("Times−Bold"),
    TIMES_ITALIC("Times−Italic"),
    TIMES_BOLD_ITALIC("Times−BoldItalic"),
    HELVETICA("Helvetica"),
    HELVETICA_BOLD("Helvetica−Bold"),
    HELVETICA_OBLIQUE("Helvetica−Oblique"),
    HELVETICA_BOLD_OBLIQUE("Helvetica−BoldOblique"),
    CURIER("Courier"),
    CURIER_BOLD("Courier−Bold"),
    CURIER_OBLIQUE("Courier−Oblique"),
    CURIER_BOLD_OBLIQUE("Courier−BoldOblique"),
    SYMBOL("Symbol"),
    ZAPFDINGBATS("ZapfDingbats");

    private String displayName;

    private StandardType1Font(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
