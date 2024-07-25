/*
 * Created on 26 ago 2016
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.TestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.mock;

/**
 * @author Andrea Vacondio
 */
public class AlternateMixMultipleInputParametersTest {

    @TempDir
    public Path folder;

    @Test
    public void testEquals() {
        PdfMixInput firstInput = mock(PdfMixInput.class);
        PdfMixInput secondInput = mock(PdfMixInput.class);
        AlternateMixMultipleInputParameters eq1 = new AlternateMixMultipleInputParameters();
        eq1.addInput(firstInput);
        eq1.addInput(secondInput);
        AlternateMixMultipleInputParameters eq2 = new AlternateMixMultipleInputParameters();
        eq2.addInput(firstInput);
        eq2.addInput(secondInput);
        AlternateMixMultipleInputParameters eq3 = new AlternateMixMultipleInputParameters();
        eq3.addInput(firstInput);
        eq3.addInput(secondInput);
        AlternateMixMultipleInputParameters diff = new AlternateMixMultipleInputParameters();
        diff.addInput(firstInput);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void invalidMinInputSize() throws IOException {
        AlternateMixMultipleInputParameters victim = new AlternateMixMultipleInputParameters();
        victim.addInput(new PdfMixInput(
                PdfFileSource.newInstanceNoPassword(Files.createTempFile(folder, "test", ".pdf").toFile()), false, 1));
        victim.setOutput(new FileTaskOutput(Files.createTempFile(folder, "out", ".pdf").toFile()));
        TestUtils.assertInvalidParameters(victim);
    }

    @Test
    public void testInvalidParametersNullSource(@TempDir Path folder) throws IOException {
        AlternateMixMultipleInputParameters victim = new AlternateMixMultipleInputParameters();
        victim.addInput(new PdfMixInput(
                PdfFileSource.newInstanceNoPassword(Files.createTempFile(folder, "test", ".pdf").toFile()), false, 1));
        victim.addInput(new PdfMixInput(null, false, 1));
        victim.setOutput(new FileTaskOutput(Files.createTempFile(folder, "out", ".pdf").toFile()));
        TestUtils.assertInvalidParameters(victim);
    }
}
