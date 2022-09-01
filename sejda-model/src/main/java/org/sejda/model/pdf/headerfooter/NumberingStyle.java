/*
 * Created on 02/gen/2011
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
package org.sejda.model.pdf.headerfooter;

import org.apache.commons.lang3.StringUtils;
import org.sejda.model.FriendlyNamed;
import org.sejda.model.util.RomanNumbersUtils;

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

    private final String displayName;

    NumberingStyle(String displayName) {
        this.displayName = displayName;
    }

    @Override
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
