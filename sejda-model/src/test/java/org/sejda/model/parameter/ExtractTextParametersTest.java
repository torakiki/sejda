/*
 * Created on 24/ago/2011
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
package org.sejda.model.parameter;

import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.MultipleTaskOutput;

/**
 * @author Andrea Vacondio
 * 
 */
public class ExtractTextParametersTest {

    private MultipleTaskOutput<?> output;
    private PdfSource<InputStream> input;

    @Before
    public void setUp() {
        output = mock(MultipleTaskOutput.class);
        InputStream stream = mock(InputStream.class);
        input = PdfStreamSource.newInstanceNoPassword(stream, "name");
    }

    @Test
    public void testEquals() {
        ExtractTextParameters eq1 = new ExtractTextParameters();
        ExtractTextParameters eq2 = new ExtractTextParameters();
        ExtractTextParameters eq3 = new ExtractTextParameters();
        ExtractTextParameters diff = new ExtractTextParameters();
        diff.setOverwrite(true);
        diff.setTextEncoding("UTF-8");
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testInvalidParametersEmptySourceList() {
        ExtractTextParameters victim = new ExtractTextParameters();
        TestUtils.assertInvalidParameters(victim);
    }

    @Test
    public void testInvalidParametersInvalidEncoding() {
        ExtractTextParameters victim = new ExtractTextParameters();
        victim.setOutput(output);
        victim.addSource(input);
        victim.setTextEncoding("");
        TestUtils.assertInvalidParameters(victim);
    }
}
