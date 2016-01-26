/*
 * Created on 24/ago/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
import org.sejda.model.SejdaFileExtensions;
import org.sejda.model.exception.TaskException;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.ExtractTextParameters;

/**
 * Parent class for tests testing the ExtractText task.
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class ExtractTextTaskTest extends BaseTaskTest<ExtractTextParameters> {

    private ExtractTextParameters parameters;

    private void setUpParameters() throws IOException {
        parameters = new ExtractTextParameters();
        testContext.directoryOutputTo(parameters);
        parameters.addSource(customInput("pdf/enc_test_test_file.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setTextEncoding("UTF-8");
    }

    @Test
    public void executeUnethicalExtract() throws TaskException, IOException {
        setUpParameters();
        new WithUnethicalReadProperty(true) {
            @Override
            public void execute() throws IOException {
                ExtractTextTaskTest.this.execute(parameters);
                testContext.assertTaskCompleted();
                testContext.assertOutputSize(1).forEachRawOutput(p -> p.endsWith(SejdaFileExtensions.TXT_EXTENSION));
            }
        };
    }

    @Test
    public void failedExtractMissingPermission() throws TaskException, IOException {
        setUpParameters();
        new WithUnethicalReadProperty(false) {
            @Override
            public void execute() {
                TestListenerFailed failListener = TestListenerFactory.newFailedListener();
                ThreadLocalNotificationContext.getContext().addListener(failListener);
                ExtractTextTaskTest.this.execute(parameters);
                assertTrue(failListener.isFailed());
            }
        };
    }
}
