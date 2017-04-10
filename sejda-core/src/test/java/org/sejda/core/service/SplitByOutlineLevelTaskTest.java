/*
 * Created on 09/ago/2011
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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.sejda.core.TestListenerFactory;
import org.sejda.core.TestListenerFactory.TestListenerFailed;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.SplitByOutlineLevelParameters;
import org.sejda.model.pdf.PdfVersion;

/**
 * @author Andrea Vacondio
 * 
 */
public abstract class SplitByOutlineLevelTaskTest extends BaseTaskTest<SplitByOutlineLevelParameters> {

    private SplitByOutlineLevelParameters setUpParameters(int level, String regEx) throws IOException {
        SplitByOutlineLevelParameters parameters = new SplitByOutlineLevelParameters(level);
        parameters.setMatchingTitleRegEx(regEx);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(customInput("pdf/bigger_outline_test.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        return parameters;
    }

    @Test
    public void testExecuteLevel3() throws IOException {
        SplitByOutlineLevelParameters parameters = setUpParameters(3, null);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2);
    }

    @Test
    public void testExecuteLevel2() throws IOException {
        SplitByOutlineLevelParameters parameters = setUpParameters(2, null);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(3).forEachPdfOutput(d -> {
            assertTrue(nonNull(d.getDocumentCatalog().getDocumentOutline()));
        });
    }

    @Test
    public void testExecuteLevel2DiscardOutline() throws IOException {
        SplitByOutlineLevelParameters parameters = setUpParameters(2, null);
        parameters.discardOutline(true);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(3).forEachPdfOutput(d -> {
            assertTrue(isNull(d.getDocumentCatalog().getDocumentOutline()));
        });
    }

    @Test
    public void testExecuteLevel1() throws IOException {
        SplitByOutlineLevelParameters parameters = setUpParameters(1, null);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4);
    }

    @Test
    public void testExecuteLevel1MatchingregEx() throws IOException {
        SplitByOutlineLevelParameters parameters = setUpParameters(1, "(Second)+.+");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2);
    }

    @Test
    public void testExecuteLevel1NotMatchingregEx() throws IOException {
        SplitByOutlineLevelParameters parameters = setUpParameters(1, ".+(Chuck)+.+");
        TestListenerFailed failListener = TestListenerFactory.newFailedListener();
        ThreadLocalNotificationContext.getContext().addListener(failListener);
        execute(parameters);
        assertTrue(failListener.isFailed());
    }

    @Test
    public void testExecuteLevel4() throws IOException {
        SplitByOutlineLevelParameters parameters = setUpParameters(4, null);
        TestListenerFailed failListener = TestListenerFactory.newFailedListener();
        ThreadLocalNotificationContext.getContext().addListener(failListener);
        execute(parameters);
        assertTrue(failListener.isFailed());
    }

}
