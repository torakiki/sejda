/*
 * Created on 10/25/13
 * Copyright 2013 by Edi Weissmann (edi.weissmann@gmail.com).
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.core.TestListenerFactory;
import org.sejda.core.TestListenerFactory.TestListenerFailed;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.ExtractTextByPagesParameters;
import org.sejda.model.pdf.page.PageRange;

/**
 * Base tests for testing the ExtractTextByPages task.
 * 
 * @author Edi Weissmann
 */
@Ignore
public abstract class ExtractTextByPagesTaskTest extends BaseTaskTest<ExtractTextByPagesParameters> {

    private ExtractTextByPagesParameters parameters;

    private void setUpParameters() throws IOException {
        parameters = new ExtractTextByPagesParameters();
        parameters.setSource(customInput("pdf/test_file.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setTextEncoding("UTF-8");
        parameters.setOutputPrefix("[FILENUMBER]_test_file");
        testContext.directoryOutputTo(parameters);
    }

    @Test
    public void testExecuteWrongRange() throws IOException {
        setUpParameters();
        parameters.addPageRange(new PageRange(10));
        TestListenerFailed failListener = TestListenerFactory.newFailedListener();
        ThreadLocalNotificationContext.getContext().addListener(failListener);
        execute(parameters);
        assertTrue(failListener.isFailed());
    }

    @Test
    public void textCroppedOut() throws IOException {
        setUpParameters();
        parameters.setSource(customInput("pdf/text_cropped_out.pdf"));
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(1).assertOutputContainsFilenames("1_test_file.txt").forEachRawOutput(p -> {
            try {
                assertEquals("First page", Files.lines(p).findFirst().get());
                assertFalse(Files.lines(p).anyMatch(s -> "Content".equals(s)));
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void testExecuteRange() throws IOException {
        setUpParameters();
        parameters.addPageRange(new PageRange(1, 3));
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(3)
                .assertOutputContainsFilenames("1_test_file.txt", "2_test_file.txt", "3_test_file.txt")
                .forEachRawOutput(p -> {
                    try {
                        if (p.getFileName().equals("1_test_file.txt")) {
                            assertEquals("GNU LIBRARY GENERAL PUBLIC LICENSE", Files.lines(p).findFirst().get());
                        }
                        if (p.getFileName().equals("3_test_file.txt")) {
                            assertEquals("and installation of the library.", Files.lines(p).findFirst().get());
                        }
                    } catch (IOException e) {
                        fail(e.getMessage());
                    }
                });
    }

    @Test
    public void testExecute() throws IOException {
        setUpParameters();
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4).assertOutputContainsFilenames("1_test_file.txt", "2_test_file.txt",
                "3_test_file.txt", "4_test_file.txt").forEachRawOutput(p -> {
                    try {
                        if (p.getFileName().equals("1_test_file.txt")) {
                            assertEquals("GNU LIBRARY GENERAL PUBLIC LICENSE", Files.lines(p).findFirst().get());
                        }
                        if (p.getFileName().equals("3_test_file.txt")) {
                            assertEquals("and installation of the library.", Files.lines(p).findFirst().get());
                        }
                    } catch (IOException e) {
                        fail(e.getMessage());
                    }
                });
    }
}
