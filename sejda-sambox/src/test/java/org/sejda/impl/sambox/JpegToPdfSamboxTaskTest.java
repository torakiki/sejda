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

import org.sejda.core.service.JpegToPdfTaskTest;
import org.sejda.impl.sambox.component.ImageLocationsExtractor;
import org.sejda.model.parameter.image.JpegToPdfParameters;
import org.sejda.model.task.Task;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class JpegToPdfSamboxTaskTest extends JpegToPdfTaskTest {

    @Override
    public Task<JpegToPdfParameters> getTask() {
        return new JpegToPdfTask();
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
