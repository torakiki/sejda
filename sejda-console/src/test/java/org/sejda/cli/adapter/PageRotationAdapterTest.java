/*
 * Created on Jul 10, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import org.junit.Test;
import org.sejda.cli.adapters.PageRotationAdapter;
import org.sejda.core.manipulation.model.rotation.PageRotation;
import org.sejda.core.manipulation.model.rotation.Rotation;
import org.sejda.core.manipulation.model.rotation.RotationType;

/**
 * @author Eduard Weissmann
 * 
 */
public class PageRotationAdapterTest {

    @Test
    public void singlePage() {
        PageRotation expected = PageRotation.createSinglePageRotation(77, Rotation.DEGREES_270);
        assertEquals(expected, new PageRotationAdapter("77:DEGREES_270").getPageRotation());
    }

    @Test
    public void multiplePages() {
        assertEquals(PageRotation.createMultiplePagesRotation(Rotation.DEGREES_0, RotationType.ALL_PAGES),
                new PageRotationAdapter("ALL_PAGES:DEGREES_0").getPageRotation());

        assertEquals(PageRotation.createMultiplePagesRotation(Rotation.DEGREES_180, RotationType.EVEN_PAGES),
                new PageRotationAdapter("EVEN_PAGES:DEGREES_180").getPageRotation());

        assertEquals(PageRotation.createMultiplePagesRotation(Rotation.DEGREES_90, RotationType.ODD_PAGES),
                new PageRotationAdapter("ODD_PAGES:DEGREES_90").getPageRotation());
    }

    @Test
    public void negative_noSeparator() {
        try {
            new PageRotationAdapter("ALL_PAGESDEGREES_0").getPageRotation();
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("Separator ':' missing"));
        }
    }

    @Test
    public void negative_unrecognizedPages() {
        try {
            new PageRotationAdapter("SOME_PAGES:DEGREES_0").getPageRotation();
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("Unknown pages definition: 'SOME_PAGES'"));
        }
    }

    @Test
    public void negative_unrecognizedRotation() {
        try {
            new PageRotationAdapter("ODD_PAGES:DEGREES_99").getPageRotation();
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("Unknown rotation: 'DEGREES_99'"));
        }
    }
}
