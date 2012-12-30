/*
 * Created on 29/dic/2012
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
package org.sejda.model.pdf.headerfooter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * @author Andrea Vacondio
 * 
 */
public class NumberingTest {
    @Test
    public void testFormatForLabelArabic() {
        Numbering victim = new Numbering(NumberingStyle.ARABIC, 100);
        assertThat(victim.styledLabelFor(110), is("110"));
    }

    @Test
    public void testFormatForLabelRoman() {
        Numbering victim = new Numbering(NumberingStyle.ROMAN, 100);
        assertThat(victim.styledLabelFor(110), is("CX"));
    }

}
