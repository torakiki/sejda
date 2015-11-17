/*
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

import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilsTest {

    @Test
    public void trimIncludingNbsp() throws Exception {
        assertEquals("result", StringUtils.trimIncludingNbsp((char) 160 + "result"));
        assertEquals("result", StringUtils.trimIncludingNbsp("result" + (char) 160));
        assertEquals("", StringUtils.trimIncludingNbsp("" + (char) 160));
        assertEquals("", StringUtils.trimIncludingNbsp((char) 160 + "" + (char) 160));
    }
}