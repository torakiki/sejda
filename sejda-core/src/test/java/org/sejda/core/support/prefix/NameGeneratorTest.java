/*
 * Created on 03/lug/2010
 *
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
package org.sejda.core.support.prefix;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

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
        assertEquals(expected, nameGenerator(prefix).generate(nameRequest().page(2).originalName(originalName)));
    }

    @Test
    public void testSimplePrefix() {
        String prefix = "BLA_";
        String originalName = "Original";
        String expected = "BLA_Original.pdf";
        assertEquals(expected, nameGenerator(prefix).generate(nameRequest().originalName(originalName)));
    }

    @Test
    public void testSimplePrefixWithPage() {
        String prefix = "BLA_";
        String originalName = "Original";
        String expected = "1_BLA_Original.pdf";
        assertEquals(expected, nameGenerator(prefix).generate(nameRequest().page(1).originalName(originalName)));
    }

    @Test
    public void testComplexPrefixNoSubstitution() {
        String prefix = "BLA_[CURRENTPAGE###]_[BASENAME]";
        String originalName = "Original";
        String expected = "BLA_[CURRENTPAGE###]_Original.pdf";
        assertEquals(expected, nameGenerator(prefix).generate(nameRequest().originalName(originalName)));
    }

    @Test
    public void reuseSameNameGenerator() {
        NameGenerator generator = nameGenerator("BLA_[CURRENTPAGE###]_[BASENAME]");
        String originalName = "Original";
        assertEquals("BLA_001_Original.pdf", generator.generate(nameRequest().originalName(originalName).page(1)));
        assertEquals("BLA_005_Original.pdf", generator.generate(nameRequest().originalName(originalName).page(5)));
        assertEquals("BLA_010_Original.pdf", generator.generate(nameRequest().originalName(originalName).page(10)));
    }

    @Test
    public void testNullRequest() {
        assertThrows(IllegalArgumentException.class, () -> nameGenerator("BLA_").generate(null));
    }

    @Test
    public void testInvalidCharacters() {
        var generatedFilename = nameGenerator("Invalid_\\").generate(nameRequest("pdf"));
        assertEquals("Invalid_.pdf", generatedFilename);
    }

    @Test
    public void testDollarSignInFilename() {
        var generatedFilename = nameGenerator("[CURRENTPAGE]-[BASENAME]").generate(
                nameRequest("pdf").page(99).originalName("My file 6-04-2015 $1234-56"));
        assertEquals("99-My file 6-04-2015 $1234-56.pdf", generatedFilename);
    }

    @Test
    public void testSuffix() {
        var generatedFilename = nameGenerator("[BASENAME]_suffix").generate(nameRequest("pdf").originalName("My file"));
        assertEquals("My file_suffix.pdf", generatedFilename);
    }

    @Test
    public void testPrefixSuffix() {
        var generatedFilename = nameGenerator("prefix_[BASENAME]_suffix").generate(
                nameRequest("pdf").originalName("My file"));
        assertEquals("prefix_My file_suffix.pdf", generatedFilename);
    }
}
