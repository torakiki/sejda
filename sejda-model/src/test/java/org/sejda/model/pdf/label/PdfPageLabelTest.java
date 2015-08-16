/*
 * Created on 22/gen/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.pdf.label;

import org.junit.Test;
import org.sejda.TestUtils;

/**
 * @author Andrea Vacondio
 * 
 */
public class PdfPageLabelTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullLabel() {
        PdfPageLabel.newInstanceWithLabel(null, PdfLabelNumberingStyle.ARABIC, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullStyle() {
        PdfPageLabel.newInstanceWithLabel("dsdsadsa", null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativePhysicalNumb() {
        PdfPageLabel.newInstanceWithLabel("dsdsadsa", PdfLabelNumberingStyle.ARABIC, -1);
    }

    @Test
    public void testEqualsAndHasCode() {
        PdfPageLabel victim1 = PdfPageLabel.newInstanceWithLabel("dsdsadsa", PdfLabelNumberingStyle.ARABIC, 1);
        PdfPageLabel victim2 = PdfPageLabel.newInstanceWithLabel("dsdsadsa", PdfLabelNumberingStyle.ARABIC, 1);
        PdfPageLabel victim3 = PdfPageLabel.newInstanceWithLabel("dsdsadsa", PdfLabelNumberingStyle.ARABIC, 1);
        PdfPageLabel victim4 = PdfPageLabel.newInstanceWithoutLabel(PdfLabelNumberingStyle.LOWERCASE_LETTERS, 1);
        TestUtils.testEqualsAndHashCodes(victim1, victim2, victim3, victim4);
    }
}
