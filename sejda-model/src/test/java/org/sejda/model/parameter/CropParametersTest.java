/*
 * Created on 09/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.util.Set;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.RectangularBox;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.TaskOutput;

/**
 * @author Andrea Vacondio
 * 
 */
public class CropParametersTest {

    @Test
    public void testEquals() {
        CropParameters eq1 = new CropParameters();
        eq1.addCropArea(RectangularBox.newInstance(0, 1, 10, 9));
        CropParameters eq2 = new CropParameters();
        eq2.addCropArea(RectangularBox.newInstance(0, 1, 10, 9));
        CropParameters eq3 = new CropParameters();
        eq3.addCropArea(RectangularBox.newInstance(0, 1, 10, 9));
        CropParameters diff = new CropParameters();
        diff.addCropArea(RectangularBox.newInstance(1, 1, 10, 9));
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testAdd() {
        CropParameters victim = new CropParameters();
        RectangularBox area = RectangularBox.newInstance(0, 1, 10, 9);
        victim.addCropArea(area);
        Set<RectangularBox> areas = victim.getCropAreas();
        assertEquals(1, areas.size());
        assertTrue(areas.contains(area));
    }

    @Test
    public void testInvalidParameters() {
        CropParameters victim = new CropParameters();
        TaskOutput output = mock(TaskOutput.class);
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.setSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
