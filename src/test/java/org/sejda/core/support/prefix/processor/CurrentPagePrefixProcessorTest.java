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
package org.sejda.core.support.prefix.processor;

import static junit.framework.Assert.assertEquals;
import static org.sejda.core.support.perfix.NameGenerationRequest.nameRequest;

import org.junit.Test;
import org.sejda.core.support.perfix.processor.CurrentPagePrefixProcessor;
import org.sejda.core.support.perfix.processor.PrefixProcessor;

/**
 * @author Andrea Vacondio
 * 
 */
public class CurrentPagePrefixProcessorTest extends BasePrefixProcessorTest {

    private CurrentPagePrefixProcessor victim = new CurrentPagePrefixProcessor();
    private Integer page = new Integer("5");

    @Override
    public PrefixProcessor getProcessor() {
        return victim;
    }

    @Test
    public void testComplexProcess() {
        String prefix = "prefix_[CURRENTPAGE]_[BASENAME]";
        String expected = "prefix_5_[BASENAME]";
        assertEquals(expected, victim.process(prefix, nameRequest().page(page)));
    }

    @Test
    public void testComplexProcessWithPatter() {
        String prefix = "prefix_[CURRENTPAGE###]_[BASENAME]";
        String expected = "prefix_005_[BASENAME]";
        assertEquals(expected, victim.process(prefix, nameRequest().page(page)));
    }

    @Test
    public void testComplexProcessDouble() {
        String prefix = "prefix_[CURRENTPAGE]_[CURRENTPAGE]";
        String expected = "prefix_5_5";
        assertEquals(expected, victim.process(prefix, nameRequest().page(page)));
    }

    @Test
    public void testComplexProcessDoubleSinglePattern() {
        String prefix = "prefix_[CURRENTPAGE###]_[CURRENTPAGE]";
        String expected = "prefix_005_5";
        assertEquals(expected, victim.process(prefix, nameRequest().page(page)));
    }

    @Test
    public void testComplexProcessDoubleDoublePattern() {
        String prefix = "prefix_[CURRENTPAGE###]_[CURRENTPAGE##]";
        String expected = "prefix_005_05";
        assertEquals(expected, victim.process(prefix, nameRequest().page(page)));
    }
}
