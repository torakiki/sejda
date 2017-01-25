/*
 * Created on 21 gen 2016
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
package org.sejda.cli;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.model.parameter.AddBackPagesParameters;
import org.sejda.model.pdf.page.PageRange;

/**
 * @author Andrea Vacondio
 *
 */
public class AddBackPagesTaskTest extends AbstractTaskTest {

    public AddBackPagesTaskTest() {
        super(StandardTestableTask.ADD_BACK_PAGES);
    }

    @Test
    public void testOutputPrefix_Specified() {
        AddBackPagesParameters parameters = defaultCommandLine().with("-p", "fooPrefix").invokeSejdaConsole();
        assertEquals("fooPrefix", parameters.getOutputPrefix());
    }

    @Test
    public void testOutputPrefix_Default() {
        AddBackPagesParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals("", parameters.getOutputPrefix());
    }

    @Test
    public void pageRange_combined() {
        AddBackPagesParameters parameters = defaultCommandLine().with("-s", "3,5,8-10,2,2,9-9,30-")
                .invokeSejdaConsole();

        assertContainsAll(parameters.getPageSelection(), Arrays.asList(new PageRange(3, 3), new PageRange(5, 5),
                new PageRange(8, 10), new PageRange(2, 2), new PageRange(9, 9), new PageRange(30)));
    }

    @Test
    public void step_3() {
        AddBackPagesParameters parameters = defaultCommandLine().with("-n", "3").invokeSejdaConsole();
        assertEquals(3, parameters.getStep());
    }
}
