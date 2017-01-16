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
package org.sejda.impl.sambox.component;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.StreamSource;
import org.sejda.sambox.pdmodel.graphics.image.PDImageXObject;

public class PageImageWriterTest {

    @Test
    public void testTiffWithAlphaToPDXImageObject() throws TaskIOException {
        PDImageXObject result = PageImageWriter.toPDXImageObject(customNonPdfInput("image/draft.tiff"));
        assertThat(result.getHeight(), is(103));
    }

    public StreamSource customNonPdfInput(String path) {
        String extension = FilenameUtils.getExtension(path);
        return StreamSource.newInstance(getClass().getClassLoader().getResourceAsStream(path),
                randomAlphanumeric(16) + "." + extension);
    }
}