/*
 * Created on Oct 10, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.cli.command.TestableTask;
import org.sejda.cli.command.TestableTasks;

/**
 * Test encrypted files across all pdf implementations (sambox and icepdf) - due to collisions between the libraries that each pdf implementation uses for encryption, there might
 * be different behaviour at runtime
 * 
 * @author Eduard Weissmann
 * 
 */
public class EncryptionIntegrationTest extends AbstractTaskTraitTest {

    @Parameters
    public static Collection<Object[]> data() {
        return asParameterizedTestData(TestableTasks
                .getTasksWith(t -> !TestableTasks.hasMultipleSource(t) && !TestableTasks.hasFolderOutput(t)));
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();
        createTestEncryptedPdfFile("/tmp/file1encrypted.pdf");
    }

    public EncryptionIntegrationTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void executeExampleUsageWithEncryptedFileAsInput() {
        String exampleUsage = testableTask.getCommand().getExampleUsage();
        // use an encrypted file as input instead of the regular input file
        exampleUsage = StringUtils.replace(exampleUsage, "/tmp/file1.pdf:secret123", "/tmp/file1encrypted.pdf:test"); // replace file1.pdf with encrypted one
        // change pattern to match outline test file we have
        exampleUsage = StringUtils.replace(exampleUsage, "-l 2 -e \".+(Chapter)+.+\"", "-l 1 -e \".+(level)+.+\""); // to match outline test file we have

        assertThat("Task " + getTaskName() + " doesnt provide example usage", exampleUsage, is(notNullValue()));

        assertTaskCompletes(exampleUsage + " --overwrite");
    }
}
