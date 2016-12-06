/*
 * Created on 06 dic 2016
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
package org.sejda.model.parameter;

import java.util.Locale;

import org.junit.Test;
import org.sejda.TestUtils;

/**
 * @author Andrea Vacondio
 *
 */
public class OcrTextByPagesParametersTest {
    @Test
    public void testEquals() {
        OcrTextByPagesParameters eq1 = new OcrTextByPagesParameters();
        OcrTextByPagesParameters eq2 = new OcrTextByPagesParameters();
        OcrTextByPagesParameters eq3 = new OcrTextByPagesParameters();
        OcrTextByPagesParameters diff = new OcrTextByPagesParameters();
        diff.addLanguage(Locale.CHINESE);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

}
