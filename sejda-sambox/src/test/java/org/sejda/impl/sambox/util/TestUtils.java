/*
 * Copyright 2022 Sober Lemur S.r.l. and Sejda BV
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
package org.sejda.impl.sambox.util;

import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.sambox.pdmodel.PDDocument;

import java.io.File;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class TestUtils {

    public static PDDocument getTestDoc(String name) throws TaskIOException {
        File file = new File(name);
        if (file.exists()) {
            PdfFileSource source = PdfFileSource.newInstanceNoPassword(file);
            return new DefaultPdfSourceOpener().open(source).getUnderlyingPDDocument();
        }

        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(
                TestUtils.class.getClassLoader().getResourceAsStream(name), randomAlphanumeric(16) + ".pdf");

        return new DefaultPdfSourceOpener().open(source).getUnderlyingPDDocument();
    }
}
