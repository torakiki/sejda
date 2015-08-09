/*
 * Copyright 2012 by Eduard Weissmann (edi.weissmann@gmail.com).
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter;

import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.model.pdf.numbering.BatesSequence;

public class SetHeaderFooterParametersTest {

    @Test
    public void testEquals() {
        BatesSequence bates = new BatesSequence();

        SetHeaderFooterParameters eq1 = new SetHeaderFooterParameters();
        SetHeaderFooterParameters eq2 = new SetHeaderFooterParameters();
        SetHeaderFooterParameters eq3 = new SetHeaderFooterParameters();
        SetHeaderFooterParameters diff = new SetHeaderFooterParameters();
        eq1.setBatesSequence(bates);
        eq2.setBatesSequence(bates);
        eq3.setBatesSequence(bates);
        diff.setBatesSequence(bates);
        diff.setFont(StandardType1Font.CURIER_BOLD);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testValidation() {
        SetHeaderFooterParameters victim = new SetHeaderFooterParameters();
        victim.setPageCountStartFrom(-1);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.addSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
