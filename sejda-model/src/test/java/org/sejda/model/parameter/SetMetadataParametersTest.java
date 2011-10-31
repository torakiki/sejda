/*
 * Created on 03/ago/2011
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.util.Set;

import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.TaskOutput;
import org.sejda.model.pdf.PdfMetadataKey;

/**
 * @author Andrea Vacondio
 * 
 */
public class SetMetadataParametersTest {

    @Test
    public void testEquals() {
        SetMetadataParameters eq1 = new SetMetadataParameters();
        eq1.put(PdfMetadataKey.AUTHOR, "author");
        SetMetadataParameters eq2 = new SetMetadataParameters();
        eq2.put(PdfMetadataKey.AUTHOR, "author");
        SetMetadataParameters eq3 = new SetMetadataParameters();
        eq3.put(PdfMetadataKey.AUTHOR, "author");
        SetMetadataParameters diff = new SetMetadataParameters();
        diff.put(PdfMetadataKey.AUTHOR, "author");
        diff.put(PdfMetadataKey.CREATOR, "creator");
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testPut() {
        SetMetadataParameters victim = new SetMetadataParameters();
        victim.put(PdfMetadataKey.AUTHOR, "author");
        victim.put(PdfMetadataKey.CREATOR, "creator");
        Set<PdfMetadataKey> keys = victim.keySet();
        assertEquals(2, keys.size());
        assertTrue(keys.contains(PdfMetadataKey.AUTHOR));
        assertTrue(keys.contains(PdfMetadataKey.CREATOR));
        assertFalse(keys.contains(PdfMetadataKey.KEYWORDS));
    }

    @Test
    public void testInvalidParameters() {
        SetMetadataParameters victim = new SetMetadataParameters();
        TaskOutput output = mock(TaskOutput.class);
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.setSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
