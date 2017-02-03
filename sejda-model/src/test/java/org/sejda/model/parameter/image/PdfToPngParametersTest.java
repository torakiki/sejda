/*
 * Created on 03 feb 2017
 * Copyright 2017 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.parameter.image;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.image.ImageColorType;

/**
 * @author Andrea Vacondio
 *
 */
public class PdfToPngParametersTest {
    @Test
    public void testEquals() {
        PdfToPngParameters eq1 = new PdfToPngParameters(ImageColorType.COLOR_RGB);
        PdfToPngParameters eq2 = new PdfToPngParameters(ImageColorType.COLOR_RGB);
        PdfToPngParameters eq3 = new PdfToPngParameters(ImageColorType.COLOR_RGB);
        PdfToPngParameters diff = new PdfToPngParameters(ImageColorType.COLOR_RGB);
        diff.setResolutionInDpi(120);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }
}
