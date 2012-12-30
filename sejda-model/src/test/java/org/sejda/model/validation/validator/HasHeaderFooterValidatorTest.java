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
package org.sejda.model.validation.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sejda.model.parameter.SetHeaderFooterParameters;
import org.sejda.model.pdf.headerfooter.Numbering;
import org.sejda.model.pdf.headerfooter.NumberingStyle;

/**
 * @author Andrea Vacondio
 * 
 */
public class HasHeaderFooterValidatorTest {
    private HasHeaderFooterValidator victim = new HasHeaderFooterValidator();

    @Test
    public void testNull() {
        assertTrue(victim.isValid(null, null));
    }

    @Test
    public void testHasLabel() {
        SetHeaderFooterParameters params = new SetHeaderFooterParameters();
        params.setLabelPrefix("Prefix");
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testHasNumbering() {
        SetHeaderFooterParameters params = new SetHeaderFooterParameters();
        params.setNumbering(new Numbering(NumberingStyle.ARABIC, 2));
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testHasBoth() {
        SetHeaderFooterParameters params = new SetHeaderFooterParameters();
        params.setNumbering(new Numbering(NumberingStyle.ARABIC, 2));
        params.setLabelPrefix("Prefix");
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testHasNone() {
        SetHeaderFooterParameters params = new SetHeaderFooterParameters();
        assertFalse(victim.isValid(params, null));
    }
}
