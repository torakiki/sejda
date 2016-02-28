/*
 * Created on 22 gen 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.parameter;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sejda.TestUtils;
import org.sejda.model.input.StreamSource;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.pdf.collection.InitialView;

/**
 * @author Andrea Vacondio
 *
 */
public class AttachmentsCollectionParametersTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testEquals() {
        AttachmentsCollectionParameters eq1 = new AttachmentsCollectionParameters();
        AttachmentsCollectionParameters eq2 = new AttachmentsCollectionParameters();
        AttachmentsCollectionParameters eq3 = new AttachmentsCollectionParameters();
        AttachmentsCollectionParameters diff = new AttachmentsCollectionParameters();
        diff.setInitialView(InitialView.HIDDEN);
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }

    @Test
    public void testInvalidParametersEmptySourceList() throws IOException {
        AttachmentsCollectionParameters victim = new AttachmentsCollectionParameters();
        victim.setOutput(new FileTaskOutput(folder.newFile("out.pdf")));
        TestUtils.assertInvalidParameters(victim);
    }

    @Test
    public void testInvalidParametersOutput() throws IOException {
        AttachmentsCollectionParameters victim = new AttachmentsCollectionParameters();
        victim.addSource(StreamSource.newInstance(new ByteArrayInputStream(new byte[] { -1 }), "source"));
        victim.setOutput(new FileTaskOutput(folder.newFile("out.chuck")));
        TestUtils.assertInvalidParameters(victim);
    }
}
