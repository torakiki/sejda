/*
 * Created on 27 set 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter;

import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.MultipleTaskOutput;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.repaginate.Repagination;

/**
 * @author Andrea Vacondio
 *
 */
public class SplitDownTheMiddleParametersTest {
    private SplitDownTheMiddleParameters victim;

    @Before
    public void setUp() {
        victim = new SplitDownTheMiddleParameters();
        MultipleTaskOutput<?> output = mock(MultipleTaskOutput.class);
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.addSource(input);
    }

    @Test
    public void testEquals() {
        SplitDownTheMiddleParameters eq1 = new SplitDownTheMiddleParameters();
        eq1.setRepagination(Repagination.LAST_FIRST);
        SplitDownTheMiddleParameters eq2 = new SplitDownTheMiddleParameters();
        eq2.setRepagination(Repagination.LAST_FIRST);
        SplitDownTheMiddleParameters eq3 = new SplitDownTheMiddleParameters();
        eq3.setRepagination(Repagination.LAST_FIRST);
        SplitDownTheMiddleParameters diff = new SplitDownTheMiddleParameters();
        diff.setRepagination(Repagination.NONE);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void invalidRepagination() {
        victim.setRepagination(null);
        TestUtils.assertInvalidParameters(victim);
    }

    @Test
    public void invalidMode() {
        victim.setMode(null);
        TestUtils.assertInvalidParameters(victim);
    }

    @Test
    public void invalidExcludedPageRange() {
        victim.addExcludedPageRange(new PageRange(-5));
        TestUtils.assertInvalidParameters(victim);
    }
}
