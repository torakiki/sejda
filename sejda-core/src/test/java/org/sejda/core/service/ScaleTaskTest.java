/*
 * Created on 01 dic 2016
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
import org.sejda.model.parameter.ScaleParameters;
import org.sejda.model.scale.ScaleType;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;

/**
 * @author Andrea Vacondio
 *
 */
@Ignore
public abstract class ScaleTaskTest extends BaseTaskTest<ScaleParameters> {

    private ScaleParameters parameters;

    private void setUpParameters() throws IOException {
        parameters.addSource(mediumInput());
        parameters.addSource(encryptedInput());
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setOutputPrefix("[FILENUMBER]_test_file");
        testContext.directoryOutputTo(parameters);
    }

    @Test
    public void testPageScale() throws IOException {
        parameters = new ScaleParameters(0.6);
        setUpParameters();
        parameters.setScaleType(ScaleType.PAGE);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2).assertOutputContainsFilenames("1_test_file.pdf", "2_test_file.pdf")
                .forEachPdfOutput(d -> {
                    PDPage page = d.getPage(0);
                    PDRectangle expected = new PDRectangle(0f, 0f, 357f, 505.2f);
                    assertEquals(expected, page.getMediaBox());
                    assertEquals(expected, page.getCropBox());
                });
    }
}
