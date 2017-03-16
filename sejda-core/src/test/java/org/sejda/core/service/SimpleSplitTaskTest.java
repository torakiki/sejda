/*
 * Created on 29/lug/2011
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

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.SimpleSplitParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.page.PredefinedSetOfPages;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class SimpleSplitTaskTest extends BaseTaskTest<SimpleSplitParameters> {

    private SimpleSplitParameters parameters;

    private void setUpParameters(PredefinedSetOfPages type) {
        parameters = new SimpleSplitParameters(type);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(shortInput());
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
    }

    @Test
    public void testExecuteBurst() throws IOException {
        setUpParameters(PredefinedSetOfPages.ALL_PAGES);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4);
    }

    @Test
    public void testExecuteBurstEncrypted() throws IOException {
        setUpParameters(PredefinedSetOfPages.ALL_PAGES);
        parameters.removeAllSources();
        parameters.addSource(stronglyEncryptedInput());
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4);
    }

    @Test
    public void testExecuteEven() throws IOException {
        setUpParameters(PredefinedSetOfPages.EVEN_PAGES);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2);
    }

    @Test
    public void testExecuteOdd() throws IOException {
        setUpParameters(PredefinedSetOfPages.ODD_PAGES);
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(3);
    }

    @Test
    public void batchMode() throws IOException {
        setUpParameters(PredefinedSetOfPages.EVEN_PAGES);
        parameters.addSource(shortInput());
        parameters.addSource(mediumInput());

        testContext.directoryOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.assertOutputSize(19);
        testContext.assertOutputContainsFilenames("1_short-test-file.pdf", "3_short-test-file.pdf",
                "1_medium-test-file.pdf", "3_medium-test-file.pdf", "5_medium-test-file.pdf", "7_medium-test-file.pdf", "9_medium-test-file.pdf",
                "11_medium-test-file.pdf", "13_medium-test-file.pdf", "15_medium-test-file.pdf", "17_medium-test-file.pdf", "19_medium-test-file.pdf",
                "21_medium-test-file.pdf", "23_medium-test-file.pdf", "25_medium-test-file.pdf", "27_medium-test-file.pdf", "29_medium-test-file.pdf",
                "31_medium-test-file.pdf", "33_medium-test-file.pdf"
        );
    }

}
