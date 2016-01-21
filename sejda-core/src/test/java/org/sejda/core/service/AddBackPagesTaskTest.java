/*
 * Created on 20 gen 2016
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
package org.sejda.core.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.AddBackPagesParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.page.PageRange;

/**
 * @author Andrea Vacondio
 *
 */
@Ignore
public abstract class AddBackPagesTaskTest extends BaseTaskTest<AddBackPagesParameters> {

    private AddBackPagesParameters parameters;

    private void setUpParametersWithOutline() {
        parameters = new AddBackPagesParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(largeOutlineInput());
        parameters.setBackPagesSource(encryptedInput());
        parameters.addPageRange(new PageRange(1, 1));
    }

    private void setUpParametersWithForms() {
        parameters = new AddBackPagesParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(customInput("pdf/forms/two_pages_form.pdf"));
        parameters.setBackPagesSource(encryptedInput());
        parameters.addPageRange(new PageRange(1, 1));
    }

    private void setUpParametersBackPageToEveryPage() {
        parameters = new AddBackPagesParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(shortInput());
        parameters.setBackPagesSource(encryptedInput());
        parameters.addPageRange(new PageRange(1, 1));
    }

    private void setUpParametersMultipleEncryptedInput() {
        parameters = new AddBackPagesParameters();
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(encryptedInput());
        parameters.addSource(stronglyEncryptedInput());
        parameters.setBackPagesSource(mediumInput());
        parameters.addPageRange(new PageRange(2, 2));
        parameters.addPageRange(new PageRange(5, 6));
        parameters.setStep(2);
    }

    @Test
    public void outlineIsStripped() throws IOException {
        setUpParametersWithOutline();
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertOutputSize(1).assertHasOutline(false);
    }

    @Test
    public void formIsStripped() throws IOException {
        setUpParametersWithForms();
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertOutputSize(1).assertHasAcroforms(false);
    }

    @Test
    public void backPages() throws IOException {
        setUpParametersBackPageToEveryPage();
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertOutputSize(1)
                .forEachPdfOutput(d -> assertEquals(8, d.getNumberOfPages()));
    }

    @Test
    public void backPagesMultipleEncryptedInput() throws IOException {
        setUpParametersMultipleEncryptedInput();
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2).forEachPdfOutput(d -> assertEquals(10, d.getNumberOfPages()));
    }
}
