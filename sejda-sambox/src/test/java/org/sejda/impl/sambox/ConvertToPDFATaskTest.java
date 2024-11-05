package org.sejda.impl.sambox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.ConvertToPDFAParameters;
import org.sejda.model.pdfa.ConformanceLevel;
import org.sejda.model.pdfa.InvalidElementPolicy;
import org.sejda.model.task.Task;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.tests.tasks.BaseTaskTest;

import java.io.IOException;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.sejda.tests.TestUtils.customInput;

/*
 * Created on 28/10/24
 * Copyright 2024 Sober Lemur S.r.l. and Sejda BV
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
class ConvertToPDFATaskTest extends BaseTaskTest<ConvertToPDFAParameters> {

    private void setUpParameters(ConvertToPDFAParameters parameters) {
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
    }

    @Test
    @DisplayName("FirstChar and LastChar not there")
    public void noFirstChar() throws IOException {
        var parameters = new ConvertToPDFAParameters(InvalidElementPolicy.FIX, ConformanceLevel.PDFA_1B);
        setUpParameters(parameters);
        parameters.addSource(customInput("pdf/pdfa/ttf-no-first-char.pdf"));
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(1)
                .forEachPdfOutput(d -> d.getPages().forEach(ConvertToPDFATaskTest::validateWidthsGenerated));
    }

    @Test
    @DisplayName("Missing Widths array")
    public void noWidthsArray() throws IOException {
        var parameters = new ConvertToPDFAParameters(InvalidElementPolicy.FIX, ConformanceLevel.PDFA_1B);
        setUpParameters(parameters);
        parameters.addSource(customInput("pdf/pdfa/ttf-no-widths.pdf"));
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(1)
                .forEachPdfOutput(d -> d.getPages().forEach(ConvertToPDFATaskTest::validateWidthsGenerated));
    }

    @Test
    @DisplayName("Wrong value in Widths array")
    public void wrongWidthsArray() throws IOException {
        var parameters = new ConvertToPDFAParameters(InvalidElementPolicy.FIX, ConformanceLevel.PDFA_1B);
        setUpParameters(parameters);
        parameters.addSource(customInput("pdf/pdfa/ttf-wrong-width.pdf"));
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(1)
                .forEachPdfOutput(d -> d.getPages().forEach(ConvertToPDFATaskTest::validateWidthsGenerated));
    }

    private static void validateWidthsGenerated(PDPage p) {
        var font = ofNullable(p.getResources().getCOSObject()).map(
                        r -> r.getDictionaryObject(COSName.FONT, COSDictionary.class))
                .map(f -> f.getDictionaryObject(COSName.getPDFName("F1"), COSDictionary.class)).orElse(null);
        assertNotNull(font);
        assertEquals(COSName.TRUE_TYPE, font.getCOSName(COSName.SUBTYPE));
        assertEquals(67, font.getInt(COSName.FIRST_CHAR));
        assertEquals(68, font.getInt(COSName.LAST_CHAR));
        var widths = font.getDictionaryObject(COSName.WIDTHS, COSArray.class);
        assertNotNull(widths);
        assertEquals(2, widths.size());
        assertEquals(676, widths.getInt(0));
        assertEquals(732, widths.getInt(1));
    }

    @Override
    public Task<ConvertToPDFAParameters> getTask() {
        return new ConvertToPDFATask();
    }
}