/*
 * Created on 11/ago/2011
 * Copyright 2011 Sober Lemur S.r.l. and Sejda BV.
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.pdf.form.AcroFormPolicy;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.TestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static org.mockito.Mockito.mock;

/**
 * @author Andrea Vacondio
 */
public class MergeParametersTest {

    private SingleTaskOutput output;

    @BeforeEach
    public void setUp() throws IOException {
        output = new FileTaskOutput(Files.createTempFile(null, ".pdf").toFile());
    }

    @Test
    public void testEquals() {
        MergeParameters eq1 = new MergeParameters();
        MergeParameters eq2 = new MergeParameters();
        MergeParameters eq3 = new MergeParameters();
        MergeParameters diff = new MergeParameters();
        diff.setBlankPageIfOdd(true);
        diff.setAcroFormPolicy(AcroFormPolicy.MERGE);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testInvalidParametersNullSource() {
        MergeParameters victim = new MergeParameters();

        victim.setOutput(output);
        victim.addInput(new PdfMergeInput(null));
        TestUtils.assertInvalidParameters(victim);
    }

    @Test
    public void testInvalidParametersInvalidRange() {
        MergeParameters victim = new MergeParameters();
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        PdfMergeInput mergeInput = new PdfMergeInput(input);
        PageRange range = new PageRange(3, 2);
        mergeInput.addPageRange(range);
        victim.addInput(mergeInput);
        TestUtils.assertInvalidParameters(victim);
    }

    @Test
    public void testInvalidParametersExists() {
        MergeParameters victim = new MergeParameters();
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        PdfMergeInput mergeInput = new PdfMergeInput(input);
        PageRange range = new PageRange(2, 3);
        mergeInput.addPageRange(range);
        victim.addInput(mergeInput);
        victim.setExistingOutputPolicy(ExistingOutputPolicy.FAIL);
        TestUtils.assertInvalidParameters(victim);
    }

    @Test
    public void testInvalidParametersIntersectingRanges() {
        MergeParameters victim = new MergeParameters();
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        PdfMergeInput mergeInput = new PdfMergeInput(input);
        PageRange range1 = new PageRange(1, 20);
        PageRange range2 = new PageRange(10, 30);
        mergeInput.addPageRange(range1);
        mergeInput.addPageRange(range2);
        victim.addInput(mergeInput);
        TestUtils.assertInvalidParameters(victim);
    }
}
