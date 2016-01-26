/*
 * Created on 13/giu/2010
 *
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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.RotateParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PredefinedSetOfPages;
import org.sejda.model.rotation.Rotation;

/**
 * Abstract test unit for the rotate task
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class RotateTaskTest extends BaseTaskTest<RotateParameters> {

    private RotateParameters parameters;

    private void setUpDefaultParameters() {
        parameters = new RotateParameters(Rotation.DEGREES_180, PredefinedSetOfPages.ALL_PAGES);
        parameters.addSource(shortInput());
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
    }

    private void setUpParametersWithVersionPrefixAndCompressionSpecified() {
        parameters = new RotateParameters(Rotation.DEGREES_180, PredefinedSetOfPages.ALL_PAGES);
        parameters.setCompress(true);
        parameters.setOutputPrefix("test_prefix_");
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(shortInput());
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.SKIP);
    }

    private void setUpRotateSpecificPages() {
        parameters = new RotateParameters(Rotation.DEGREES_90);
        parameters.addPageRange(new PageRange(2, 4));
        parameters.addSource(shortInput());
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
    }

    private void setUpRotateMultipleInputNotRangesContained() {
        parameters = new RotateParameters(Rotation.DEGREES_90);
        parameters.addPageRange(new PageRange(2, 4));
        parameters.addPageRange(new PageRange(15, 15));
        parameters.addSource(shortInput());
        parameters.addSource(mediumInput());
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
    }

    private void setUpParametersEncrypted() {
        parameters = new RotateParameters(Rotation.DEGREES_180, PredefinedSetOfPages.ALL_PAGES);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.addSource(stronglyEncryptedInput());
    }

    @Test
    public void testExecute() throws IOException {
        setUpDefaultParameters();
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(4)
                .forEachPdfOutput(d -> d.getPages().forEach(p -> assertEquals(180, p.getRotation())));
    }

    @Test
    public void testRotateSpecificPages() throws IOException {
        setUpRotateSpecificPages();
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(4).forEachPdfOutput(d -> assertEquals(90, d.getPage(2).getRotation()));
    }

    @Test
    public void testExecuteEncrypted() throws IOException {
        setUpParametersEncrypted();
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(4)
                .forEachPdfOutput(d -> d.getPages().forEach(p -> assertEquals(180, p.getRotation())));
    }

    @Test
    public void testVersionPrefixAndCreatorAreApplied() throws IOException {
        setUpParametersWithVersionPrefixAndCompressionSpecified();
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(4).assertVersion(PdfVersion.VERSION_1_6);
    }

    @Test
    public void testMultipleInputOneDoesntContainRange() throws IOException {
        setUpRotateMultipleInputNotRangesContained();
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2);
    }

    @Test
    public void testDifferentRotations() throws IOException {
        parameters = new RotateParameters();
        parameters.addPageRange(new PageRange(1, 2), Rotation.DEGREES_90);
        parameters.addPageRange(new PageRange(3, 4), Rotation.DEGREES_180);
        parameters.addSource(shortInput());
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(4).forEachPdfOutput(d -> {
            assertEquals(90, d.getPage(0).getRotation());
            assertEquals(90, d.getPage(1).getRotation());
            assertEquals(180, d.getPage(2).getRotation());
            assertEquals(180, d.getPage(3).getRotation());
        });

    }
}
