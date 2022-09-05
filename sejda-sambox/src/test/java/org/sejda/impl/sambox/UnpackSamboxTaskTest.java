/*
 * Created on 17 dic 2015
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
package org.sejda.impl.sambox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.UnpackParameters;
import org.sejda.model.task.Task;
import org.sejda.tests.tasks.BaseTaskTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sejda.tests.TestUtils.customInput;

/**
 * @author Andrea Vacondio
 */
public class UnpackSamboxTaskTest extends BaseTaskTest<UnpackParameters> {

    private UnpackParameters parameters;
    @TempDir
    public Path folder;

    @Test
    public void unpackAnnotations() throws IOException {
        executeTest("pdf/attachments_as_annots.pdf");
    }

    @Test
    public void unpackNamedTree() throws IOException {
        executeTest("pdf/attachments_as_named_tree.pdf");
    }

    public void executeTest(String filename) throws IOException {
        File out = Files.createTempDirectory(folder, "sejda").toFile();
        parameters = new UnpackParameters(new DirectoryTaskOutput(out));
        parameters.addSource(customInput(filename));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        execute(parameters);
        assertEquals(1, out.list().length);
    }

    @Override
    public Task<UnpackParameters> getTask() {
        return new UnpackTask();
    }

}
