/*
 * Created on 25/dic/2010
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.AlternateMixParameters;
import org.sejda.model.pdf.PdfVersion;

/**
 * Abstract test unit for the alternate mix task
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class AlternateMixTaskTest extends BaseTaskTest<AlternateMixParameters> {

    private AlternateMixParameters parameters;

    private void setUpParameters(PdfMixInput firstInput, PdfMixInput secondInput) {
        parameters = new AlternateMixParameters(firstInput, secondInput);
        parameters.setOutputName("outName.pdf");
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_5);
    }

    @Test
    public void withStandardInput() throws TaskException, IOException {
        setUpParameters(new PdfMixInput(shortInput()), new PdfMixInput(shortInput(), true, 3));
        testContext.fileOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(8).assertVersion(PdfVersion.VERSION_1_5);
    }

    @Test
    public void withEncryptedInput() throws TaskException, IOException {
        setUpParameters(new PdfMixInput(encryptedInput()), new PdfMixInput(stronglyEncryptedInput(), true, 3));
        testContext.fileOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(8).assertVersion(PdfVersion.VERSION_1_5);
    }
}
