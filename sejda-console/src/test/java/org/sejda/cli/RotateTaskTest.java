/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.model.parameter.RotateParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.rotation.Rotation;

/**
 * Tests for the RotateTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class RotateTaskTest extends AbstractTaskTest {

    public RotateTaskTest() {
        super(StandardTestableTask.ROTATE);
    }

    @Test
    public void testOutputPrefix_Specified() {
        RotateParameters parameters = defaultCommandLine().with("-p", "fooPrefix").invokeSejdaConsole();
        assertEquals("fooPrefix", parameters.getOutputPrefix());
    }

    @Test
    public void testOutputPrefix_Default() {
        RotateParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals("", parameters.getOutputPrefix());
    }

    @Test
    public void rotation_90() {
        RotateParameters parameters = defaultCommandLine().with("-r", "90").invokeSejdaConsole();
        assertEquals(Rotation.DEGREES_90, parameters.getRotation());
    }

    @Test
    public void pageRotation_invalidRotationType() {
        defaultCommandLine().with("-r", "99990").assertConsoleOutputContains("Invalid value '99990' for rotation");
    }

    @Test
    public void predefinedPages_ALL_PAGES() {
        RotateParameters parameters = defaultCommandLine().with("-m", "all").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(1, 2, 3, 4, 5), parameters.getPages(5));
    }

    @Test
    public void predefinedPages_ODD_PAGES() {
        RotateParameters parameters = defaultCommandLine().with("-m", "odd").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(1, 3, 5), parameters.getPages(5));
    }

    @Test
    public void predefinedPages_EVEN_PAGES() {
        RotateParameters parameters = defaultCommandLine().with("-m", "even").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(2, 4), parameters.getPages(5));
    }

    @Test
    public void pageRange_combined() {
        RotateParameters parameters = defaultCommandLine().with("-s", "3,5,8-10,2,2,9-9,30-")
                .invokeSejdaConsole();

        assertContainsAll(parameters.getPageSelection(), Arrays.asList(new PageRange(3, 3), new PageRange(5, 5),
                new PageRange(8, 10), new PageRange(2, 2), new PageRange(9, 9), new PageRange(30)));
    }

    @Test
    public void specificPagesAndRotations() {
        RotateParameters parameters = defaultCommandLine().without("-m").with("-s", "3,5,8-10").with("-k", "90 180 270")
                .invokeSejdaConsole();

        assertEquals(parameters.getRotation(3), Rotation.DEGREES_90);
        assertEquals(parameters.getRotation(5), Rotation.DEGREES_180);
        assertEquals(parameters.getRotation(8), Rotation.DEGREES_270);
        assertEquals(parameters.getRotation(9), Rotation.DEGREES_270);
    }

    @Test
    public void mandatoryPageDefinitionParams() {
        defaultCommandLine().without("-m").without("-s")
                .assertConsoleOutputContains("Please specify at least one option that defines pages to be rotated");
    }

    @Test
    public void mandatoryRotationParams() {
        defaultCommandLine().without("-r").without("-k").assertConsoleOutputContains("Please specify at least one option that defines rotation");
    }
}
