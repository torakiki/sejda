/*
 * Copyright 2016 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.image.JpegToPdfParameters;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;

@Ignore
public abstract class JpegToPdfTaskTest extends BaseTaskTest<JpegToPdfParameters> {

    private JpegToPdfParameters basicParameters() throws IOException {
        return basicParameters("draft.png", "large.jpg");
    }

    private JpegToPdfParameters basicParameters(String... images) throws IOException {
        JpegToPdfParameters parameters = new JpegToPdfParameters();
        for(String image: images) {
            parameters.addSource(customNonPdfInput("image/" + image));
        }

        testContext.directoryOutputTo(parameters);
        parameters.setOutputPrefix("test_file");
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        return parameters;
    }

    @Test
    public void testAddingPngImage() throws Exception {
        execute(basicParameters());
        testContext.forPdfOutput("test_file.pdf", d -> {
            assertThat(d.getNumberOfPages(), is(2));

            // unscaled, center aligned
            int expectedWidth = 248;
            int expectedHeight = 103;
            assertImageAtLocation(d, d.getPage(0),
                    new Point(
                            (int) (PDRectangle.A4.getWidth() - expectedWidth) / 2,
                            (int) (PDRectangle.A4.getHeight() - expectedHeight) / 2
                    ),
                    expectedWidth, expectedHeight
            );

            // scaled down, full page, landscape
            assertImageAtLocation(d, d.getPage(1),
                    new Point(0, 17), (int) PDRectangle.A4.getHeight(), 561);
        });
        testContext.assertTaskCompleted();
    }

    @Test
    public void testUnsupportedTiffWithAlpha() throws Exception {
        execute(basicParameters("draft.tiff"));
        testContext.assertTaskCompleted();
    }

    @Test
    public void testUnsupportedTiff() throws Exception {
        execute(basicParameters("draft_no_alpha.tif"));
        testContext.assertTaskCompleted();
    }

    protected abstract void assertImageAtLocation(PDDocument Doc, PDPage page, Point2D position, int width, int height);
}
