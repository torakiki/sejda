/*
 * Created on 12 gen 2016
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.tests.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.sejda.commons.util.IOUtils;
import org.sejda.core.context.SejdaConfiguration;
import org.sejda.core.service.DefaultTaskExecutionService;
import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.task.Task;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.graphics.image.PDImageXObject;
import org.sejda.tests.TestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Andrea Vacondio
 */
public abstract class BaseTaskTest<T extends TaskParameters> implements TestableTask<T> {

    public final TaskTestContext testContext = new TaskTestContext();
    private SejdaConfiguration configuration;
    private DefaultTaskExecutionService victim;

    @BeforeEach
    public void setUp() throws TaskException {
        configuration = mock(SejdaConfiguration.class);
        when(configuration.getTask(any())).thenReturn((Task) getTask());
        victim = new DefaultTaskExecutionService(configuration);
    }

    public void executeWithValidation(TaskParameters parameters) {
        when(configuration.isValidation()).thenReturn(Boolean.TRUE);
        testContext.listenForTaskFailure();
        testContext.listenForTaskWarnings();
        victim.execute(parameters);
    }

    public void execute(TaskParameters parameters) {
        when(configuration.isValidation()).thenReturn(Boolean.FALSE);
        testContext.listenForTaskFailure();
        testContext.listenForTaskWarnings();
        victim.execute(parameters);
    }

    @AfterEach
    public void closeContext() {
        IOUtils.closeQuietly(testContext);
    }

    public static void assertPageText(PDPage page, String text) {
        TestUtils.assertPageText(page, text);
    }

    public static void assertPageTextExactLines(PDPage page, String text) {
        TestUtils.assertPageTextExactLines(page, text);
    }

    public static void assertPageTextContains(PDPage page, String text) {
        TestUtils.assertPageTextContains(page, text);
    }

    public static void assertMediaBox(PDPage page, float width, float height) {
        Assertions.assertEquals(page.getMediaBox().getWidth(), width, 0.01);
        Assertions.assertEquals(page.getMediaBox().getHeight(), height, 0.01);
    }

    public static <T> List<T> getAnnotationsOf(PDPage page, Class<T> clazz) {
        return TestUtils.getAnnotationsOf(page, clazz);
    }

    // returns 1-based page numbers
    public static List<Integer> getPagesContainingImages(PDDocument doc) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < doc.getNumberOfPages(); i++) {
            PDPage page = doc.getPage(i);
            boolean hasImages = false;
            for (COSName name : page.getResources().getXObjectNames()) {
                try {
                    if (page.getResources().getXObject(name) instanceof PDImageXObject) {
                        hasImages = true;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (hasImages) {
                result.add(i + 1);
            }
        }
        return result;
    }
}
