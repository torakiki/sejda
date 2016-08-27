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
import org.sejda.model.parameter.AbstractAlternateMixParameters;
import org.sejda.model.parameter.AlternateMixMultipleInputParameters;
import org.sejda.model.parameter.AlternateMixParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.sambox.pdmodel.PDPage;

/**
 * Abstract test unit for the alternate mix task
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class AlternateMixTaskTest extends BaseTaskTest<AbstractAlternateMixParameters> {

    private void setUpParameters(AbstractAlternateMixParameters parameters) {
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_5);
    }

    @Test
    public void threeDocsMerge() throws TaskException, IOException {
        AlternateMixMultipleInputParameters params = new AlternateMixMultipleInputParameters();
        params.addInput(new PdfMixInput(shortInput(), true, 1));
        params.addInput(new PdfMixInput(stronglyEncryptedInput(), true, 3));
        params.addInput(new PdfMixInput(largeOutlineInput()));
        params.setOutputName("outName.pdf");
        setUpParameters(params);
        testContext.pdfOutputTo(params);
        execute(params);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(13).assertVersion(PdfVersion.VERSION_1_5).forPdfOutput(d -> {
            assertHeaderContains(d.getPage(0), "Pagina 4 di 4");
            assertHeaderContains(d.getPage(5), "Pagina 3 di 4");
            assertHeaderContains(d.getPage(8), "Pagina 2 di 4");
            assertHeaderContains(d.getPage(10), "Pagina 1 di 4");
        });
    }

    @Test
    @Deprecated
    public void withStandardInput() throws TaskException, IOException {
        AlternateMixParameters parameters = new AlternateMixParameters(new PdfMixInput(shortInput()),
                new PdfMixInput(shortInput(), true, 3));
        parameters.setOutputName("outName.pdf");
        setUpParameters(parameters);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(8).assertVersion(PdfVersion.VERSION_1_5);
    }

    @Test
    @Deprecated
    public void withEncryptedInput() throws TaskException, IOException {
        AlternateMixParameters parameters = new AlternateMixParameters(new PdfMixInput(encryptedInput()),
                new PdfMixInput(stronglyEncryptedInput(), true, 3));
        parameters.setOutputName("outName.pdf");
        setUpParameters(parameters);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(8).assertVersion(PdfVersion.VERSION_1_5);
    }

    protected abstract void assertHeaderContains(PDPage page, String expectedText);
}
