/*
 * Created on 21 ott 2016
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
package org.sejda.impl.sambox;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.awt.Rectangle;
import java.io.IOException;

import org.sejda.core.service.WatermarkTaskTest;
import org.sejda.impl.sambox.component.ImageLocationsExtractor;
import org.sejda.model.parameter.WatermarkParameters;
import org.sejda.model.task.Task;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;

/**
 * @author Andrea Vacondio
 *
 */
public class WatermarkSamboxTaskTest extends WatermarkTaskTest {

    @Override
    public Task<WatermarkParameters> getTask() {
        return new WatermarkTask();
    }

    @Override
    protected void assertImageAtLocation(PDDocument document, PDPage page, Rectangle rectangle) {
        try {
            ImageLocationsExtractor imageLocationsExtractor = new ImageLocationsExtractor();
            imageLocationsExtractor.process(document);

            java.util.List<Rectangle> imageLocations = imageLocationsExtractor.getImageLocations().get(page);

            assertThat(imageLocations, is(notNullValue()));
            assertThat(imageLocations, hasItem(rectangle));

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Override
    protected void assertNoImageAtLocation(PDDocument document, PDPage page, Rectangle rectangle) {
        try {
            ImageLocationsExtractor imageLocationsExtractor = new ImageLocationsExtractor();
            imageLocationsExtractor.process(document);

            java.util.List<Rectangle> imageLocations = imageLocationsExtractor.getImageLocations().get(page);

            assertThat(imageLocations, not(hasItem(rectangle)));

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
