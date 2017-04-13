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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;

public class StringUtilsTest {

    @Test
    public void testNbsp() {
        assertEquals("result", StringUtils.normalizeWhitespace((char) 160 + "result").trim());
        assertEquals("result", StringUtils.normalizeWhitespace("result" + (char) 160).trim());
        assertEquals("", StringUtils.normalizeWhitespace("" + (char) 160).trim());
        assertEquals("", StringUtils.normalizeWhitespace((char) 160 + "" + (char) 160).trim());
        assertEquals("Foo bar", StringUtils.normalizeWhitespace("Foo" + (char) 160 + "bar"));
    }

    @Test
    public void asUnicodes() {
        assertEquals("\\U+20\\U+E6\\U+65\\U+5EA", StringUtils.asUnicodes(" æeת"));
    }

    @Test
    public void difference() {
        Set<Character> expected = new LinkedHashSet<>();
        expected.add('\uFE0F');
        expected.add('\uEF0F');

        assertEquals(StringUtils.difference("a\uFE0Fb\uEF0Fc", "abc"), expected);
    }
}