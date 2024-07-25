/*
 * Copyright 2010 Sober Lemur S.r.l. and Sejda BV
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
package org.sejda.core.support.util;

import java.util.Locale;

public class HumanReadableSize {

    public static final long KB = 1000;
    public static final long MB = 1000 * KB;

    private HumanReadableSize() {}

    public static String toString(long size) {
        return toString(size, false);
    }

    public static String toString(long size, boolean roundUp) {
        String format = roundUp ? "%.0f" : "%.2f";
        String unit = "bytes";
        String unitSize = String.format("%.0f", (float)size);

        if(size > MB) {
            unit = "MB";
            unitSize = String.format(format, (float) size / MB);
        } else if(size > KB) {
            unit = "KB";
            unitSize = String.format(format, (float) size / KB);
        }

        return String.format(Locale.US, "%s %s", unitSize, unit);
    }
}
