/*
 * Created on 15/set/2011
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
package org.sejda.impl.sambox;

import org.junit.jupiter.api.Test;
import org.sejda.impl.sambox.component.PdfTextExtractorByArea;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.AlternateMixMultipleInputParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.task.Task;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.tests.TestUtils;
import org.sejda.tests.tasks.BaseTaskTest;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.sejda.tests.TestUtils.customInput;
import static org.sejda.tests.TestUtils.encryptedInput;
import static org.sejda.tests.TestUtils.largeInput;
import static org.sejda.tests.TestUtils.largeOutlineInput;
import static org.sejda.tests.TestUtils.shortInput;
import static org.sejda.tests.TestUtils.stronglyEncryptedInput;

/**
 * @author Andrea Vacondio
 */
public class AlternateMixSamboxTaskTest extends BaseTaskTest<AlternateMixMultipleInputParameters> {

    private void setUpParameters(AlternateMixMultipleInputParameters parameters) {
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_5);
    }

    @Test
    public void threeDocsMerge() throws IOException {
        AlternateMixMultipleInputParameters params = new AlternateMixMultipleInputParameters();
        params.addInput(new PdfMixInput(shortInput(), true, 1));
        params.addInput(new PdfMixInput(stronglyEncryptedInput(), true, 3));
        params.addInput(new PdfMixInput(largeOutlineInput()));
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
    public void threeDocsMergeWithPageSelection() throws IOException {
        AlternateMixMultipleInputParameters params = new AlternateMixMultipleInputParameters();
        PdfMixInput first = new PdfMixInput(shortInput(), true, 1);
        first.addPageRange(new PageRange(1, 2));
        params.addInput(first);
        PdfMixInput second = new PdfMixInput(stronglyEncryptedInput(), false, 2);
        params.addInput(second);
        PdfMixInput third = new PdfMixInput(largeInput(), false, 6);
        third.addPageRange(new PageRange(5, 10));
        third.addPageRange(new PageRange(22, 23));
        params.addInput(third);
        setUpParameters(params);
        testContext.pdfOutputTo(params);
        execute(params);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(14).assertVersion(PdfVersion.VERSION_1_5).forPdfOutput(d -> {
            assertHeaderContains(d.getPage(0), "Pagina 2 di 4");
            assertHeaderContains(d.getPage(9), "Pagina 1 di 4");
        });
    }

    @Test
    public void withStandardInput() throws IOException {
        AlternateMixMultipleInputParameters parameters = new AlternateMixMultipleInputParameters();
        parameters.addInput(new PdfMixInput(shortInput()));
        parameters.addInput(new PdfMixInput(shortInput(), true, 3));
        setUpParameters(parameters);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(8).assertVersion(PdfVersion.VERSION_1_5);
    }

    @Test
    public void withEncryptedInput() throws IOException {
        AlternateMixMultipleInputParameters parameters = new AlternateMixMultipleInputParameters();
        parameters.addInput(new PdfMixInput(encryptedInput()));
        parameters.addInput(new PdfMixInput(stronglyEncryptedInput(), true, 3));
        setUpParameters(parameters);
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(8).assertVersion(PdfVersion.VERSION_1_5);
    }

    @Test
    public void atRestEncryptionTest() throws IOException {
        AlternateMixMultipleInputParameters params = new AlternateMixMultipleInputParameters();
        params.addInput(new PdfMixInput(TestUtils.encryptedAtRest(shortInput())));
        params.addInput(new PdfMixInput(TestUtils.encryptedAtRest(stronglyEncryptedInput())));
        setUpParameters(params);
        testContext.pdfOutputTo(params);
        execute(params);

        testContext.assertTaskCompleted();
        testContext.assertPages(8);
    }

    @Test
    public void repeatForeverExplicitlyMarkedInputs() throws IOException {
        AlternateMixMultipleInputParameters params = new AlternateMixMultipleInputParameters();
        params.addInput(new PdfMixInput(shortInput()));
        params.addInput(new PdfMixInput(customInput("pdf/one_page.pdf")));

        for (PdfMixInput input : params.getInputList()) {
            input.setRepeatForever(true);
        }

        setUpParameters(params);
        testContext.pdfOutputTo(params);
        execute(params);
        testContext.assertTaskCompleted();
        testContext.assertPages(8).forPdfOutput(d -> {
            assertHeaderContains(d.getPage(0), "Pagina 1 di 4");
            assertPageTextContains(d.getPage(1), "First page");
            assertHeaderContains(d.getPage(2), "Pagina 2 di 4");
            assertPageTextContains(d.getPage(3), "First page");
            assertHeaderContains(d.getPage(4), "Pagina 3 di 4");
            assertPageTextContains(d.getPage(5), "First page");
            assertHeaderContains(d.getPage(6), "Pagina 4 di 4");
            assertPageTextContains(d.getPage(7), "First page");
        });
    }

    @Override
    public Task<AlternateMixMultipleInputParameters> getTask() {
        return new AlternateMixTask();
    }

    private void assertHeaderContains(PDPage page, String expectedText) {
        try {
            assertThat(new PdfTextExtractorByArea().extractHeaderText(page).trim(), containsString(expectedText));
        } catch (TaskIOException e) {
            fail(e.getMessage());
        }
    }

}
