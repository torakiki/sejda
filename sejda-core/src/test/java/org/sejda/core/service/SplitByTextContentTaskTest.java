/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.core.TestListenerFactory;
import org.sejda.core.TestListenerFactory.TestListenerFailed;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.model.TopLeftRectangularBox;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.SplitByTextContentParameters;
import org.sejda.model.pdf.PdfVersion;

@Ignore
public abstract class SplitByTextContentTaskTest extends BaseTaskTest<SplitByTextContentParameters> {

    private SplitByTextContentParameters parameters;

    private void setUpParameters(TopLeftRectangularBox area) throws IOException {
        parameters = new SplitByTextContentParameters(area);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setSource(customInput("pdf/split_by_text_contents_sample.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
    }

    @Test
    public void testExecute() throws IOException {
        setUpParameters(new TopLeftRectangularBox(114, 70, 41, 15));
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(3);
    }

    @Test
    public void testExecuteDoesNotFailOnDocWithoutMultipleResults() throws IOException {
        setUpParameters(new TopLeftRectangularBox(114, 70, 41, 15));
        parameters.setSource(customInput("pdf/split_by_text_contents_sample_one.pdf"));
        parameters.setOutputPrefix("[TEXT]");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(1);
        testContext.assertOutputContainsFilenames("00001.pdf");
    }

    @Test
    public void testNoTextFoundInAreas() throws IOException {
        setUpParameters(new TopLeftRectangularBox(1, 1, 1, 1));
        TestListenerFailed failListener = TestListenerFactory.newFailedListener();
        ThreadLocalNotificationContext.getContext().addListener(failListener);
        execute(parameters);
        assertTrue(failListener.isFailed());
    }

    @Test
    public void testFileOutputNaming() throws IOException {
        setUpParameters(new TopLeftRectangularBox(70, 70, 81, 15));
        parameters.setOutputPrefix("[CURRENTPAGE]-[TEXT]");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(3).assertOutputContainsFilenames("1-Invoice 00001.pdf", "4-Invoice 00002.pdf",
                "5-Invoice 00003.pdf");
    }

    @Test
    public void testDoesNotSplitWhenThePageOnlyHasNonBreakingSpace() throws IOException {
        setUpParameters(new TopLeftRectangularBox(68, 70, 73, 18));
        parameters.setSource(customInput("pdf/split_by_text_newlines_sample.pdf"));
        parameters.setOutputPrefix("[CURRENTPAGE]-[TEXT]");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2).assertOutputContainsFilenames("1-1234561234.pdf", "3-1234561235.pdf");
    }

    @Test
    public void testUsingPrefix() throws IOException {
        setUpParameters(new TopLeftRectangularBox(70, 110, 92, 15));
        parameters.setStartsWith("Fax:");
        parameters.setSource(customInput("pdf/split_by_text_with_prefix.pdf"));
        parameters.setOutputPrefix("[CURRENTPAGE]-[TEXT]");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2).assertOutputContainsFilenames("1-1234561234.pdf", "3-4321231234.pdf");
    }

    @Test
    public void testUsingSuffix() throws IOException {
        setUpParameters(new TopLeftRectangularBox(69, 95, 154, 16));
        parameters.setEndsWith("Amsterdam, Netherlands");
        parameters.setSource(customInput("pdf/split_by_text_with_prefix.pdf"));
        parameters.setOutputPrefix("[CURRENTPAGE]-[TEXT]");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2).assertOutputContainsFilenames("1-1023AB.pdf", "3-6543AB.pdf");
    }
}
