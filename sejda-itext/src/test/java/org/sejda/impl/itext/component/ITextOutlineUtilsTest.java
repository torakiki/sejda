/*
 * Created on 09/giu/2013
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.itext.component;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrea Vacondio
 * 
 */
public class ITextOutlineUtilsTest {
    private Map<String, Object> outline;

    @Before
    public void setUp() {
        outline = new HashMap<String, Object>();
    }

    @Test
    public void testGetPageNumberNotExisting() {
        assertEquals(-1, ITextOutlineUtils.getPageNumber(outline));
    }

    @Test
    public void testGetPageNumber() {
        outline.put(ITextOutlineUtils.PAGE_KEY, "10 0 R");
        assertEquals(10, ITextOutlineUtils.getPageNumber(outline));
    }

    @Test
    public void testGetPageNumberWrongString() {
        outline.put(ITextOutlineUtils.PAGE_KEY, "Chuck Norris");
        assertEquals(-1, ITextOutlineUtils.getPageNumber(outline));
    }

    @Test
    public void testGetTitle() {
        outline.put(ITextOutlineUtils.TITLE_KEY, "Chuck");
        assertEquals("Chuck", ITextOutlineUtils.nullSafeGetTitle(outline));
    }

    @Test
    public void testNullOutline() {
        assertEquals("", ITextOutlineUtils.nullSafeGetTitle(null));
    }

    @Test
    public void testNullTitle() {
        outline.put(ITextOutlineUtils.TITLE_KEY, null);
        assertEquals("", ITextOutlineUtils.nullSafeGetTitle(null));
    }
}
