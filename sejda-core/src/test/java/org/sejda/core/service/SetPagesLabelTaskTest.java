/*
 * Created on 23/gen/2011
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
package org.sejda.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.SetPagesLabelParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.label.PdfLabelNumberingStyle;
import org.sejda.model.pdf.label.PdfPageLabel;
import org.sejda.sambox.pdmodel.common.PDPageLabelRange;
import org.sejda.sambox.pdmodel.common.PDPageLabels;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class SetPagesLabelTaskTest extends BaseTaskTest<SetPagesLabelParameters> {

    private SetPagesLabelParameters parameters;

    private void setUpParameters() throws IOException {
        parameters = new SetPagesLabelParameters();
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.putLabel(1, PdfPageLabel.newInstanceWithoutLabel(PdfLabelNumberingStyle.LOWERCASE_ROMANS, 1));
        parameters.putLabel(3, PdfPageLabel.newInstanceWithLabel("Test", PdfLabelNumberingStyle.ARABIC, 1));
        parameters.putLabel(20, PdfPageLabel.newInstanceWithLabel("OutOfRange", PdfLabelNumberingStyle.ARABIC, 1));
        parameters.setSource(shortInput());
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.pdfOutputTo(parameters);
    }

    @Test
    public void testExecute() throws IOException {
        setUpParameters();
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(4).forPdfOutput(d -> {
            try {
                PDPageLabels labels = d.getDocumentCatalog().getPageLabels();
                PDPageLabelRange range1 = labels.getPageLabelRange(0);
                assertNotNull(range1);
                assertEquals(PDPageLabelRange.STYLE_ROMAN_LOWER, range1.getStyle());
                assertEquals(1, range1.getStart());
                PDPageLabelRange range2 = labels.getPageLabelRange(2);
                assertNotNull(range2);
                assertEquals(PDPageLabelRange.STYLE_DECIMAL, range2.getStyle());
                assertEquals(1, range2.getStart());
                assertEquals("Test", range2.getPrefix());
                assertNull(labels.getPageLabelRange(19));
            } catch (Exception e) {
                fail(e.getMessage());
            }
        });

    }
}
