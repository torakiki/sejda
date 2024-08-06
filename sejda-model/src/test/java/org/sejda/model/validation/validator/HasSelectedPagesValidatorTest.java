/*
 * Created on 02/ott/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.validation.validator;

import org.junit.jupiter.api.Test;
import org.sejda.model.parameter.ExtractPagesParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PagesSelection;
import org.sejda.model.pdf.page.PredefinedSetOfPages;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrea Vacondio
 */
public class HasSelectedPagesValidatorTest {
    private HasSelectedPagesValidator victim = new HasSelectedPagesValidator();

    @Test
    public void testNull() {
        assertTrue(victim.isValid(null, null));
    }

    @Test
    public void testHasPredefined() {
        var params = new ExtractPagesParameters(PredefinedSetOfPages.EVEN_PAGES);
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testHasPageSelection() {
        var params = new ExtractPagesParameters();
        params.addPageRange(PagesSelection.LAST_PAGE);
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testHasBoth() {
        var params = new ExtractPagesParameters(PredefinedSetOfPages.EVEN_PAGES);
        params.addPageRange(new PageRange(20));
        assertTrue(victim.isValid(params, null));
    }

    @Test
    public void testHasNone() {
        var params = new ExtractPagesParameters();
        assertFalse(victim.isValid(params, null));
    }
}
