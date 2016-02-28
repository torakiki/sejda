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
package org.sejda.core.service;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.input.StreamSource;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.AttachmentsCollectionParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSName;

/**
 * @author Andrea Vacondio
 *
 */
@Ignore
public abstract class AttachmentsCollectionTaskTest extends BaseTaskTest<AttachmentsCollectionParameters> {
    private AttachmentsCollectionParameters parameters;

    private void setUpParameters() {
        parameters = new AttachmentsCollectionParameters();
        parameters.setCompress(false);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(StreamSource.newInstance(
                getClass().getClassLoader().getResourceAsStream("pdf/short-test-file.pdf"), "short-test-file.pdf"));
        parameters.addSource(StreamSource.newInstance(getClass().getClassLoader().getResourceAsStream("text_file.txt"),
                "text_file.txt"));
        parameters.addSource(StreamSource.newInstance(
                getClass().getClassLoader().getResourceAsStream("pdf/medium_test.pdf"), "medium-test-file.pdf"));
    }

    @Test
    public void testExecutePages() throws IOException {
        setUpParameters();
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertPages(1).forPdfOutput(d -> {
            COSBase names = d.getDocumentCatalog().getNames().getEmbeddedFiles().getCOSObject().getItem(COSName.NAMES);
            assertThat(names, instanceOf(COSArray.class));
            assertEquals(6, ((COSArray) names).size());
            assertNotNull(d.getDocumentCatalog().getCOSObject().getItem(COSName.getPDFName("Collection")));
        });
    }

}
