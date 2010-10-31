/*
 * Created on 03/lug/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.core.support.prefix.processor;

import static org.junit.Assert.assertEquals;
import static org.sejda.core.support.perfix.model.NameGenerationRequest.nameRequest;

import org.junit.Test;
import org.sejda.core.support.perfix.processor.CurrentPagePrefixProcessor;
import org.sejda.core.support.perfix.processor.PrefixProcessor;

/**
 * @author Andrea Vacondio
 * 
 */
public class CurrentPagePrefixProcessorTest extends BasePrefixProcessorTest {

    private CurrentPagePrefixProcessor victim = new CurrentPagePrefixProcessor();
    private Integer page = Integer.valueOf("5");

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
