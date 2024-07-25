/*
 * Created on 23/gen/2011
 * Copyright 2010 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.model.parameter;

import org.junit.jupiter.api.Test;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.pdf.label.PdfLabelNumberingStyle;
import org.sejda.model.pdf.label.PdfPageLabel;
import org.sejda.model.TestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author Andrea Vacondio
 * 
 */
public class SetPagesLabelParametersTest {


    @Test
    public void testEquals() {
        SetPagesLabelParameters victim1 = new SetPagesLabelParameters();
        SetPagesLabelParameters victim2 = new SetPagesLabelParameters();
        SetPagesLabelParameters victim3 = new SetPagesLabelParameters();
        SetPagesLabelParameters victim4 = new SetPagesLabelParameters();

        PdfPageLabel label = PdfPageLabel.newInstanceWithLabel("label", PdfLabelNumberingStyle.ARABIC, 2);
        PdfPageLabel diffLabel = PdfPageLabel.newInstanceWithoutLabel(PdfLabelNumberingStyle.ARABIC, 2);

        victim1.putLabel(1, label);
        victim2.putLabel(1, label);
        victim3.putLabel(1, label);
        victim4.putLabel(1, diffLabel);
        TestUtils.testEqualsAndHashCodes(victim1, victim2, victim3, victim4);
    }

    @Test
    public void testPutLabel() {
        SetPagesLabelParameters victim = new SetPagesLabelParameters();
        PdfPageLabel firstLabel = PdfPageLabel.newInstanceWithLabel("label1", PdfLabelNumberingStyle.ARABIC, 2);
        victim.putLabel(3, firstLabel);
        assertEquals(1, victim.getLabels().size());
        PdfPageLabel secondLabel = PdfPageLabel.newInstanceWithoutLabel(PdfLabelNumberingStyle.LOWERCASE_ROMANS, 2);
        PdfPageLabel result = victim.putLabel(3, secondLabel);
        assertEquals(firstLabel, result);
        assertEquals(1, victim.getLabels().size());
    }

    @Test
    public void testInvalidParameters() throws IOException {
        SetPagesLabelParameters victim = new SetPagesLabelParameters();
        SingleTaskOutput output = new FileTaskOutput(Files.createTempFile(null, ".pdf").toFile());
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.setSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
