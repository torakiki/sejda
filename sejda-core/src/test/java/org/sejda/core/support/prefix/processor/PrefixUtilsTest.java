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
    public void testSafeFilename() {
        assertEquals("1_Invoice#0001.pdf", PrefixUtils.toSafeFilename("1_Invoice#0001:*<>/\\.pdf"));
        assertEquals("..test.pdf", PrefixUtils.toSafeFilename("../test.pdf"));
        assertEquals("..test.pdf", PrefixUtils.toSafeFilename("..\\test.pdf"));
        assertEquals(".test.pdf", PrefixUtils.toSafeFilename("./test.pdf"));
        assertEquals("rest.pdf", PrefixUtils.toSafeFilename("\r\n\t\f`rest.pdf"));
    }

    @Test
    public void testStrictFilename() {
        assertEquals("1_Invoice0001.pdf", PrefixUtils.toStrictFilename("1_Invoice#0001:*<>/\\.pdf"));
        assertEquals(StringUtils.repeat('a', 255), PrefixUtils.toStrictFilename(StringUtils.repeat('a', 256)));
    }

    @Test
    public void testNulls(){
        assertEquals("", PrefixUtils.toSafeFilename(null));
        assertEquals("", PrefixUtils.toStrictFilename(null));
    }
}
