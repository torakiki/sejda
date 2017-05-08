/*
 * Created on 03/ago/2011
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
package org.sejda.model.parameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sejda.TestUtils;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.pdf.PdfMetadataKey;

/**
 * @author Andrea Vacondio
 * 
 */
public class SetMetadataParametersTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

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
    public void testInvalidParameters() throws IOException {
        SetMetadataParameters victim = new SetMetadataParameters();
        SingleTaskOutput output = new FileTaskOutput(folder.newFile());
        victim.setOutput(output);
        InputStream stream = mock(InputStream.class);
        PdfSource<InputStream> input = PdfStreamSource.newInstanceNoPassword(stream, "name");
        victim.setSource(input);
        TestUtils.assertInvalidParameters(victim);
    }
}
