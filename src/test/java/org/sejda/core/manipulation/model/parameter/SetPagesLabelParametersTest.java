/*
 * Created on 23/gen/2011
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.core.manipulation.model.parameter;

import org.junit.Test;
import org.sejda.core.manipulation.model.pdf.label.PdfLabelNumberingStyle;
import org.sejda.core.manipulation.model.pdf.label.PdfPageLabel;

import static org.junit.Assert.*;

/**
 * @author Andrea Vacondio
 *
 */
public class SetPagesLabelParametersTest {

    @Test
    public void testEquals() {
        SetPagesLabelParameters first = new SetPagesLabelParameters();
        SetPagesLabelParameters second = new SetPagesLabelParameters();

        PdfPageLabel label = PdfPageLabel.newInstanceWithLabelAndLogicalNumber("label", PdfLabelNumberingStyle.ARABIC,
                2, 1);

        first.add(label);
        second.add(label);

        assertTrue(first.equals(second));
    }

    @Test
    public void testAddSameLabel() {
        SetPagesLabelParameters params = new SetPagesLabelParameters();

        PdfPageLabel label1 = PdfPageLabel.newInstanceWithLabelAndLogicalNumber("label1",
                PdfLabelNumberingStyle.ARABIC, 2, 1);
        PdfPageLabel label2 = PdfPageLabel.newInstanceWithLabelAndLogicalNumber("label2",
                PdfLabelNumberingStyle.LOWERCASE_ROMANS, 2, 1);
        assertTrue(params.add(label1));
        assertFalse(params.add(label2));
    }

    @Test
    public void testLabelsAreSorted() {
        SetPagesLabelParameters params = new SetPagesLabelParameters();
        PdfPageLabel label1 = PdfPageLabel.newInstance(PdfLabelNumberingStyle.ARABIC, 10);
        PdfPageLabel label2 = PdfPageLabel.newInstance(PdfLabelNumberingStyle.LOWERCASE_ROMANS, 1);

        params.add(label1);
        params.add(label2);

        // check the first element is label2
        for (PdfPageLabel label : params.getLabels()) {
            assertEquals(label2, label);
            break;
        }
    }
}
