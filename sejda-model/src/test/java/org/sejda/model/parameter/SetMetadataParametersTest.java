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

import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sejda.TestUtils;
import org.sejda.model.pdf.PdfMetadataFields;

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
        eq1.put(PdfMetadataFields.AUTHOR, "author");
        SetMetadataParameters eq2 = new SetMetadataParameters();
        eq2.put(PdfMetadataFields.AUTHOR, "author");
        SetMetadataParameters eq3 = new SetMetadataParameters();
        eq3.put(PdfMetadataFields.AUTHOR, "author");
        SetMetadataParameters diff = new SetMetadataParameters();
        diff.put(PdfMetadataFields.AUTHOR, "author");
        diff.put(PdfMetadataFields.CREATOR, "creator");
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testPut() {
        SetMetadataParameters victim = new SetMetadataParameters();
        victim.put(PdfMetadataFields.AUTHOR, "author");
        victim.put(PdfMetadataFields.CREATOR, "creator");
        Set<String> keys = victim.getMetadata().keySet();
        assertEquals(2, keys.size());
        assertTrue(keys.contains(PdfMetadataFields.AUTHOR));
        assertTrue(keys.contains(PdfMetadataFields.CREATOR));
        assertFalse(keys.contains(PdfMetadataFields.KEYWORDS));
    }
}
