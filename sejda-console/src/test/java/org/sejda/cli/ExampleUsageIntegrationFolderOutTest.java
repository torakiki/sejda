/*
 * Created on 07 dic 2015
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.Parameterized.Parameters;
import org.sejda.StandardConsoleOnly;
import org.sejda.cli.command.TestableTask;
import org.sejda.cli.command.TestableTasks;

/**
 * @author Andrea Vacondio
 *
 */
@Category(StandardConsoleOnly.class)
public class ExampleUsageIntegrationFolderOutTest extends AbstractTaskTraitTest {

    @Parameters
    public static Collection<Object[]> data() {
        return asParameterizedTestData(TestableTasks.getTasksWithFolderOutputAndPdfInput());
    }

    public ExampleUsageIntegrationFolderOutTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void executeExampleUsage() {
        String exampleUsage = testableTask.getCommand().getExampleUsage();
        exampleUsage = StringUtils.replace(exampleUsage, "-e \".+(Chapter)+.+\"", "-e \".+(page)+.+\""); // quick hack for split by bookmarks (Chapter is better for help, page
                                                                                                         // actually exists in the sample pdf)
        assertThat("Task " + getTaskName() + " doesnt provide example usage", exampleUsage, is(notNullValue()));

        assertTaskCompletes(exampleUsage + " -j overwrite");
    }
}
