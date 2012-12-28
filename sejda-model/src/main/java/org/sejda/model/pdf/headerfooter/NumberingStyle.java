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
package org.sejda.model.pdf.headerfooter;

import org.apache.commons.lang3.StringUtils;
import org.sejda.common.FriendlyNamed;
import org.sejda.common.RomanNumbersUtils;

/**
 * Numbering styles for footer labels.
 * 
 * @author Eduard Weissmann
 * 
 */
public enum NumberingStyle implements FriendlyNamed {
    // TODO: maybe the alphabetic stuff too someday
    ARABIC("arabic") {
        @Override
        public String toStyledString(int number) {
            return Integer.toString(number);
        }
    },
    EMPTY("empty") {
        @Override
        public String toStyledString(int number) {
            return StringUtils.EMPTY;
        }
    },
    ROMAN("roman") {
        @Override
        public String toStyledString(int number) {
            return RomanNumbersUtils.toRoman(number);
        }
    };

    private String displayName;

    private NumberingStyle(String displayName) {
        this.displayName = displayName;
    }

    public String getFriendlyName() {
        return displayName;
    }

    /**
     * 
     * @param number
     * @return a string representing the given number.
     */
    public abstract String toStyledString(int number);
}
