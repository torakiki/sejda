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
package org.sejda.core.support.prefix.processor;

import static org.junit.Assert.assertEquals;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import org.junit.Test;

/**
 * Test unit for the {@link NumberPrefixProcessor}
 * 
 * @author Andrea Vacondio
 * 
 */
public class FileNumberPrefixProcessorTest extends BasePrefixProcessorTest {

    private NumberPrefixProcessor victim = new FileNumberPrefixProcessor();
    private Integer fileNumber = Integer.valueOf("5");

    @Override
    public PrefixProcessor getProcessor() {
        return victim;
    }

    @Test
    public void nullFileNumber() {
        String prefix = "prefix_[FILENUMBER]_[BASENAME]";
        assertEquals(prefix, victim.process(prefix, nameRequest()));
    }

    @Test
    public void testComplexProcess() {
        String prefix = "prefix_[FILENUMBER]_[BASENAME]";
        String expected = "prefix_5_[BASENAME]";
        assertEquals(expected, victim.process(prefix, nameRequest().fileNumber(fileNumber)));
    }

    @Test
    public void testComplexProcessStartingPage() {
        String prefix = "prefix_[FILENUMBER12]_[BASENAME]";
        String expected = "prefix_17_[BASENAME]";
        assertEquals(expected, victim.process(prefix, nameRequest().fileNumber(fileNumber)));
    }

    @Test
    public void testComplexProcessWithPatter() {
        String prefix = "prefix_[FILENUMBER###]_[BASENAME]";
        String expected = "prefix_005_[BASENAME]";
        assertEquals(expected, victim.process(prefix, nameRequest().fileNumber(fileNumber)));
    }

    @Test
    public void testComplexProcessWithPatterStartingPage() {
        String prefix = "prefix_[FILENUMBER###10]_[BASENAME]";
        String expected = "prefix_015_[BASENAME]";
        assertEquals(expected, victim.process(prefix, nameRequest().fileNumber(fileNumber)));
    }

    @Test
    public void testComplexProcessDouble() {
        String prefix = "prefix_[FILENUMBER]_[FILENUMBER]";
        String expected = "prefix_5_5";
        assertEquals(expected, victim.process(prefix, nameRequest().fileNumber(fileNumber)));
    }

    @Test
    public void testComplexProcessDoubleSinglePattern() {
        String prefix = "prefix_[FILENUMBER###]_[FILENUMBER]";
        String expected = "prefix_005_5";
        assertEquals(expected, victim.process(prefix, nameRequest().fileNumber(fileNumber)));
    }

    @Test
    public void testComplexProcessDoubleSinglePatternStartingPage() {
        String prefix = "prefix_[FILENUMBER###23]_[FILENUMBER32]";
        String expected = "prefix_028_37";
        assertEquals(expected, victim.process(prefix, nameRequest().fileNumber(fileNumber)));
    }

    @Test
    public void testComplexProcessDoubleSinglePatternNegativeStartingPage() {
        String prefix = "prefix_[FILENUMBER###-23]_[FILENUMBER-2]";
        String expected = "prefix_-018_3";
        assertEquals(expected, victim.process(prefix, nameRequest().fileNumber(fileNumber)));
    }

    @Test
    public void testComplexProcessDoubleDoublePattern() {
        String prefix = "prefix_[FILENUMBER###]_[FILENUMBER##]";
        String expected = "prefix_005_05";
        assertEquals(expected, victim.process(prefix, nameRequest().fileNumber(fileNumber)));
    }
}
