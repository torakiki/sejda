/*
 * Created on 15 nov 2016
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
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.SingleOrMultipleTaskOutput;
import org.sejda.model.scale.ScaleType;

/**
 * @author Andrea Vacondio
 *
 */
public class ScaleParametersTest {
    @Test
    public void testEquals() {
        ScaleParameters eq1 = new ScaleParameters(10);
        eq1.setScaleType(ScaleType.CONTENT);
        ScaleParameters eq2 = new ScaleParameters(10);
        eq2.setScaleType(ScaleType.CONTENT);
        ScaleParameters eq3 = new ScaleParameters(10);
        eq3.setScaleType(ScaleType.CONTENT);
        ScaleParameters diff = new ScaleParameters(10);
        diff.setScaleType(ScaleType.PAGE);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void invalidParametersEmptyWatermark() {
        ScaleParameters victim = new ScaleParameters(10);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(mock(InputStream.class), "name");
        victim.addSource(input);
        SingleOrMultipleTaskOutput output = mock(SingleOrMultipleTaskOutput.class);
        victim.setOutput(output);
        victim.setScaleType(null);
        TestUtils.assertInvalidParameters(victim);
    }
}
