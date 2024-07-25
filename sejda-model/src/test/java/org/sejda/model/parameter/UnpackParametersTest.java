/*
 * Created on 18/ago/2011
 * Copyright 2011 Sober Lemur S.r.l. and Sejda BV.
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sejda.model.output.MultipleTaskOutput;
import org.sejda.model.TestUtils;

import static org.mockito.Mockito.mock;

/**
 * @author Andrea Vacondio
 * 
 */
public class UnpackParametersTest {

    private MultipleTaskOutput output;

    @BeforeEach
    public void setUp() {
        output = mock(MultipleTaskOutput.class);
    }

    @Test
    public void testEquals() {
        UnpackParameters eq1 = new UnpackParameters(output);
        UnpackParameters eq2 = new UnpackParameters(output);
        UnpackParameters eq3 = new UnpackParameters(output);
        UnpackParameters diff = new UnpackParameters(output);
        diff.setLenient(true);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testInvalidParametersEmptySourceList() {
        UnpackParameters victim = new UnpackParameters(output);
        TestUtils.assertInvalidParameters(victim);
    }
}
