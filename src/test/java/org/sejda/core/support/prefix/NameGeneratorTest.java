/*
 * Created on 03/lug/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.support.prefix;

import static org.junit.Assert.assertEquals;
import static org.sejda.core.support.perfix.NameGenerator.nameGenerator;
import static org.sejda.core.support.perfix.model.NameGenerationRequest.nameRequest;

import org.junit.Test;

/**
 * Test unit for the NameGenerator
 * 
 * @author Andrea Vacondio
 * 
 */
public class NameGeneratorTest {

    @Test
    public void testFullComplexPrefix() {
        String prefix = "BLA_[CURRENTPAGE###]_[BASENAME]";
        String originalName = "Original";
        String expected = "BLA_002_Original.pdf";
        assertEquals(expected, nameGenerator(prefix, originalName).generate(nameRequest().page(Integer.valueOf("2"))));
    }

    @Test
    public void testSimplePrefix() {
        String prefix = "BLA_";
        String originalName = "Original";
        String expected = "BLA_Original.pdf";
        assertEquals(expected, nameGenerator(prefix, originalName).generate(nameRequest()));
    }

    @Test
    public void testSimplePrefixWithPage() {
        String prefix = "BLA_";
        String originalName = "Original";
        String expected = "1_BLA_Original.pdf";
        assertEquals(expected, nameGenerator(prefix, originalName).generate(nameRequest().page(Integer.valueOf(1))));
    }

    @Test
    public void testComplexPrefixNoSubstitution() {
        String prefix = "BLA_[CURRENTPAGE###]_[BASENAME]";
        String originalName = "Original";
        String expected = "BLA_[CURRENTPAGE###]_[BASENAME]Original.pdf";
        assertEquals(expected, nameGenerator(prefix, originalName).generate(nameRequest()));
    }

    @Test
    public void testNullRequest() {
        String prefix = "BLA_";
        String originalName = "Original";
        String expected = "BLA_Original.pdf";
        assertEquals(expected, nameGenerator(prefix, originalName).generate(null));
    }
}
