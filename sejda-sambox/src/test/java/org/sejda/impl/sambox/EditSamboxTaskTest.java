/*
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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.IOException;

import org.sejda.core.service.EditTaskTest;
import org.sejda.impl.sambox.component.ImageLocationsExtractor;
import org.sejda.impl.sambox.component.PdfTextExtractorByArea;
import org.sejda.model.TopLeftRectangularBox;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.EditParameters;
import org.sejda.model.task.Task;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;

public class EditSamboxTaskTest extends EditTaskTest {

    @Override
    public Task<EditParameters> getTask() {
        return new EditTask();
    }

    @Override
    protected void assertPageTextDoesNotContain(PDPage page, String expectedNotFoundText) {
        PDRectangle cropBox = page.getCropBox();
        Rectangle fullPage = new Rectangle(0, 0, (int) cropBox.getWidth(), (int) cropBox.getHeight());

        try {
            assertThat(new PdfTextExtractorByArea().extractTextFromArea(page, fullPage).trim(),
                    not(containsString(expectedNotFoundText)));
        } catch (TaskIOException e) {
            fail(e.getMessage());
        }
    }

    @Override
    protected void assertTextAreaHasText(PDPage page, String expectedText, TopLeftRectangularBox area) {
        try {
            assertThat(new PdfTextExtractorByArea().extractTextFromArea(page, area.asRectangle()).trim(),
                    is(expectedText));
        } catch (TaskIOException e) {
            fail(e.getMessage());
        }
    }

    @Override
    protected void assertTextEditAreaHasText(PDPage page, String expectedText) {
        try {
            assertThat(new PdfTextExtractorByArea().extractAddedText(page, TEXT_EDIT_POSITION).trim(),
                    is(expectedText));
        } catch (TaskIOException e) {
            fail(e.getMessage());
        }
    }

    @Override
    protected void assertImageAtLocation(PDDocument document, PDPage page, Point2D position, int width, int height) {
        try {
            ImageLocationsExtractor imageLocationsExtractor = new ImageLocationsExtractor();
            imageLocationsExtractor.process(document);

            java.util.List<Rectangle> imageLocations = imageLocationsExtractor.getImageLocations().get(page);

            Rectangle rectangle = new Rectangle((int) position.getX(), (int) position.getY(), width, height);

            assertThat(imageLocations, is(notNullValue()));
            assertThat(imageLocations, hasItem(rectangle));

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
