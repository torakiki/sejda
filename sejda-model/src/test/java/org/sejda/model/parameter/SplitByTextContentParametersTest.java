/*
 * Created on 24/ago/2015
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

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.TopLeftRectangularBox;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.MultipleTaskOutput;

/**
 * @author Andrea Vacondio
 *
 */
public class SplitByTextContentParametersTest {

    @Test
    public void testEquals() {
        TopLeftRectangularBox rec = new TopLeftRectangularBox(114, 70, 41, 15);
        SplitByTextContentParameters eq1 = new SplitByTextContentParameters(rec);
        SplitByTextContentParameters eq2 = new SplitByTextContentParameters(rec);
        SplitByTextContentParameters eq3 = new SplitByTextContentParameters(rec);
        SplitByTextContentParameters diff = new SplitByTextContentParameters(new TopLeftRectangularBox(49, 170, 41, 4));
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testInvalidParameters() {
        SplitByTextContentParameters victim = new SplitByTextContentParameters(null);
        MultipleTaskOutput<?> output = mock(MultipleTaskOutput.class);
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.setSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
