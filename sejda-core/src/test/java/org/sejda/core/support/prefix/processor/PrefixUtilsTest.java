/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.core.support.prefix.processor;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class PrefixUtilsTest {

    @Test
    public void nullSafe() {
        assertEquals("", PrefixUtils.toSafeFilename(null));
    }

    @Test
    public void testSafeFilename() {
        assertEquals("1_Invoice#0001", PrefixUtils.toSafeFilename("1_Invoice#0001:*<>/\\"));
        assertEquals("..test", PrefixUtils.toSafeFilename("../test"));
        assertEquals("..test", PrefixUtils.toSafeFilename("..\\test"));
        assertEquals(".test", PrefixUtils.toSafeFilename("./test"));
        assertEquals("rest", PrefixUtils.toSafeFilename("\r\n\t\f`rest"));
    }

    @Test
    public void safeFilenameWhitespaces() {
        assertEquals("Chuck Norris", PrefixUtils.toSafeFilename("Chuck\tNorris"));
        assertEquals("Chuck Norris", PrefixUtils.toSafeFilename("\u00A0Chuck\u00A0Norris\u00A0"));
        assertEquals("Chuck Norris", PrefixUtils.toSafeFilename("\u00A0\n\t\u000B\f\rChuck\nNorris\u202f"));
        assertEquals("This is a Chuck Norris roundkick, will Steven Segal survive Nope!", PrefixUtils.toSafeFilename(
                "This\u1680is\u180ea\u2000Chuck\u200aNorris\u202froundkick,\u205fwill\u3000Steven\fSegal\rsurvive?\u000BNope!"));
    }

    @Test
    public void testStrictFilename() {
        assertEquals("1_Invoice0001", PrefixUtils.toStrictFilename("1_Invoice#0001:*<>/\\"));
        assertEquals(StringUtils.repeat('a', 255), PrefixUtils.toStrictFilename(StringUtils.repeat('a', 256)));
    }

    @Test
    public void testNulls() {
        assertEquals("", PrefixUtils.toSafeFilename(null));
        assertEquals("", PrefixUtils.toStrictFilename(null));
    }
}
