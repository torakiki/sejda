/*
 * Created on 22/ago/2011
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
package org.sejda.core.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.UnpackParameters;

/**
 * Parent test for an Unpack test.
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class UnpackTaskTest extends BaseTaskTest<UnpackParameters> {

    private UnpackParameters parameters;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void unpackAnnotations() throws IOException {
        executeTest("pdf/attachments_as_annots.pdf");
    }

    @Test
    public void unpackNamedTree() throws IOException {
        executeTest("pdf/attachments_as_named_tree.pdf");
    }

    public void executeTest(String filename) throws IOException {
        File out = folder.newFolder();
        parameters = new UnpackParameters(new DirectoryTaskOutput(out));
        parameters.addSource(customInput(filename));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        execute(parameters);
        assertEquals(1, out.list().length);
    }
}
