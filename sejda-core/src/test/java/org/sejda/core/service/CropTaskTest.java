/*
 * Created on 10/set/2011
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

import java.awt.Point;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.RectangularBox;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.CropParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class CropTaskTest extends BaseTaskTest<CropParameters> {

    private static final RectangularBox EVEN_PAGES_RECTANGLE = RectangularBox.newInstanceFromPoints(new Point(0, 0),
            new Point(595, 421));
    private static final RectangularBox ODD_PAGES_RECTANGLE = RectangularBox.newInstanceFromPoints(new Point(0, 421),
            new Point(595, 842));

    private CropParameters parameters;


    @Test
    public void testExecuteRotated90() throws IOException {
        parameters = new CropParameters();
        parameters.setCompress(false);
        parameters.addCropArea(RectangularBox.newInstanceFromPoints(new Point(10, 20), new Point(60, 40)));
        parameters.addSource(customInput("pdf/rotation_90_test_file.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        PDDocument result = testContext.assertTaskCompleted();
        RectangularBox expected = RectangularBox.newInstanceFromPoints(new Point(555, 10), new Point(575, 60));
        result.getPages().forEach(p -> {
            assertEqualsRectangles(expected, p.getCropBox());
        });
    }

    @Test
    public void testExecuteRotated180() throws IOException {
        parameters = new CropParameters();
        parameters.setCompress(false);
        parameters.addCropArea(RectangularBox.newInstanceFromPoints(new Point(10, 20), new Point(60, 40)));
        parameters.addSource(customInput("pdf/rotation_180_test_file.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        PDDocument result = testContext.assertTaskCompleted();
        RectangularBox expected = RectangularBox.newInstanceFromPoints(new Point(535, 802), new Point(585, 822));
        result.getPages().forEach(p -> {
            assertEqualsRectangles(expected, p.getCropBox());
        });
    }
    @Test
    public void testExecuteRotated270() throws IOException {
        parameters = new CropParameters();
        parameters.setCompress(false);
        parameters.addCropArea(RectangularBox.newInstanceFromPoints(new Point(10, 20), new Point(60, 40)));
        parameters.addSource(customInput("pdf/rotation_270_test_file.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        PDDocument result = testContext.assertTaskCompleted();
        RectangularBox expected = RectangularBox.newInstanceFromPoints(new Point(20, 782), new Point(40, 832));
        result.getPages().forEach(p -> {
            assertEqualsRectangles(expected, p.getCropBox());
        });
    }

    @Test
    public void testExecuteOddEven() throws IOException {
        parameters = new CropParameters();
        parameters.setCompress(false);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addCropArea(ODD_PAGES_RECTANGLE);
        parameters.addCropArea(EVEN_PAGES_RECTANGLE);
        parameters.addSource(regularInput());
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        PDDocument outDocument = testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(22);
        for (int i = 0; i < outDocument.getNumberOfPages(); i++) {
            PDPage page = outDocument.getPage(i);
            if ((i % 2) == 0) {
                assertEqualsRectangles(ODD_PAGES_RECTANGLE, page.getCropBox());
            } else {
                assertEqualsRectangles(EVEN_PAGES_RECTANGLE, page.getCropBox());
            }
        }
    }

    private void assertEqualsRectangles(RectangularBox expected, PDRectangle found) {
        assertEquals(expected.getLeft(), (int) found.getLowerLeftX());
        assertEquals(expected.getBottom(), (int) found.getLowerLeftY());
        assertEquals(expected.getRight(), (int) found.getUpperRightX());
        assertEquals(expected.getTop(), (int) found.getUpperRightY());
    }

    protected CropParameters getParameters() {
        return parameters;
    }
}
