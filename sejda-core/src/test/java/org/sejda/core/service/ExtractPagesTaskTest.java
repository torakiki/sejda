/*
 * Created on 26/ago/2011
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
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.core.TestListenerFactory;
import org.sejda.core.TestListenerFactory.TestListenerFailed;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.model.exception.TaskException;
import org.sejda.model.optimization.OptimizationPolicy;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.ExtractPagesParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PredefinedSetOfPages;

/**
 * Test for an extract pages task.
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class ExtractPagesTaskTest extends BaseTaskTest<ExtractPagesParameters> {

    private ExtractPagesParameters parameters;

    private void setUpParametersOddPages() {
        parameters = new ExtractPagesParameters(PredefinedSetOfPages.ODD_PAGES);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setSource(shortInput());
    }

    private void setUpParametersEvenPagesEncrypted() {
        parameters = new ExtractPagesParameters(PredefinedSetOfPages.ODD_PAGES);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setSource(encryptedInput());
    }

    private void setUpParametersToOptimize() {
        parameters = new ExtractPagesParameters(PredefinedSetOfPages.ODD_PAGES);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setOptimizationPolicy(OptimizationPolicy.AUTO);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setSource(customInput("pdf/shared_resource_dic.pdf"));
    }

    private void setUpParametersPageRangesPages() {
        PageRange firstRange = new PageRange(1, 1);
        PageRange secondRange = new PageRange(3);
        parameters = new ExtractPagesParameters();
        parameters.addPageRange(firstRange);
        parameters.addPageRange(secondRange);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setSource(shortInput());
    }

    private void setUpParametersPageRangesMediumFile() {
        PageRange firstRange = new PageRange(2, 3);
        PageRange secondRange = new PageRange(5, 7);
        PageRange thirdRange = new PageRange(12, 18);
        PageRange fourthRange = new PageRange(20, 26);
        Set<PageRange> ranges = new HashSet<PageRange>();
        ranges.add(firstRange);
        ranges.add(secondRange);
        ranges.add(thirdRange);
        ranges.add(fourthRange);
        parameters = new ExtractPagesParameters();
        parameters.addAllPageRanges(ranges);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_7);
        parameters.setSource(mediumInput());
    }

    private void setUpParametersWrongPageRanges() {
        PageRange range = new PageRange(10);
        parameters = new ExtractPagesParameters();
        parameters.addPageRange(range);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setSource(shortInput());
    }

    @Test
    public void extractWrongPageRages() throws IOException {
        setUpParametersWrongPageRanges();
        testContext.pdfOutputTo(parameters);
        TestListenerFailed failListener = TestListenerFactory.newFailedListener();
        ThreadLocalNotificationContext.getContext().addListener(failListener);
        execute(parameters);
        assertTrue(failListener.isFailed());
    }

    @Test
    public void extractOddPages() throws IOException {
        setUpParametersOddPages();
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(2);
    }

    @Test
    public void extractEvenPagesFromEncrypted() throws IOException {
        setUpParametersEvenPagesEncrypted();
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(2);
    }

    @Test
    public void extractRanges() throws IOException {
        setUpParametersPageRangesPages();
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(3);
    }

    @Test
    public void extractRangesMedium() throws TaskException, IOException {
        setUpParametersPageRangesMediumFile();
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_7).assertPages(19);
    }

    @Test
    public void extractOptimized() throws IOException {
        setUpParametersToOptimize();
        testContext.pdfOutputTo(parameters);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_6).assertPages(1);
    }
}
