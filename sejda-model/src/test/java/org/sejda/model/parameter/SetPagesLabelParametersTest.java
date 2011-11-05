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
package org.sejda.model.parameter;

import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.TaskOutput;
import org.sejda.model.pdf.label.PdfLabelNumberingStyle;
import org.sejda.model.pdf.label.PdfPageLabel;

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
        Assert.assertEquals(1, victim.getLabels().size());
        PdfPageLabel secondLabel = PdfPageLabel.newInstanceWithoutLabel(PdfLabelNumberingStyle.LOWERCASE_ROMANS, 2);
        PdfPageLabel result = victim.putLabel(3, secondLabel);
        Assert.assertEquals(firstLabel, result);
        Assert.assertEquals(1, victim.getLabels().size());
    }

    @Test
    public void testInvalidParameters() {
        SetPagesLabelParameters victim = new SetPagesLabelParameters();
        TaskOutput output = mock(TaskOutput.class);
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.setSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
