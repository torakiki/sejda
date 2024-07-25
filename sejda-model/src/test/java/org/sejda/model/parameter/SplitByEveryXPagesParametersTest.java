/*
 * Created on 11/giu/2014
 * Copyright 2014 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.model.parameter;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.sejda.model.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.SingleOrMultipleTaskOutput;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author Andrea Vacondio
 * 
 */
public class SplitByEveryXPagesParametersTest {

    @Test
    public void testEquals() {
        SplitByEveryXPagesParameters eq1 = new SplitByEveryXPagesParameters(1);
        SplitByEveryXPagesParameters eq2 = new SplitByEveryXPagesParameters(1);
        SplitByEveryXPagesParameters eq3 = new SplitByEveryXPagesParameters(1);
        SplitByEveryXPagesParameters diff = new SplitByEveryXPagesParameters(2);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void getPages() {
        SplitByEveryXPagesParameters victim = new SplitByEveryXPagesParameters(5);
        assertEquals(1, victim.getPages(5).size());
        assertEquals(2, victim.getPages(10).size());
        assertThat(victim.getPages(15), CoreMatchers.hasItems(5, 10, 15));
    }

    @Test
    public void testInvalidParameters() {
        SplitByEveryXPagesParameters victim = new SplitByEveryXPagesParameters(-5);
        SingleOrMultipleTaskOutput output = mock(SingleOrMultipleTaskOutput.class);
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.addSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
