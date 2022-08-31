/*
 * Created on 03/08/2015
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com).
 *
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
package org.sejda.impl.sambox;

import org.junit.jupiter.api.Test;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.SplitByOutlineLevelParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;
import org.sejda.tests.tasks.BaseTaskTest;

import java.io.IOException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SplitByOutlineLevelSamboxTaskTest extends BaseTaskTest<SplitByOutlineLevelParameters> {

    private SplitByOutlineLevelParameters setUpParameters(int level, String regEx) throws IOException {
        SplitByOutlineLevelParameters parameters = new SplitByOutlineLevelParameters(level);
        parameters.setMatchingTitleRegEx(regEx);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.addSource(customInput("/pdf/bigger_outline_test.pdf"));
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
    public void testSameBookmarkName() throws IOException {
        SplitByOutlineLevelParameters parameters = setUpParameters(1, null);
        parameters.removeAllSources();
        parameters.addSource(customInput("/pdf/same_bookmarks_name.pdf"));
        parameters.setOutputPrefix("[BOOKMARK_NAME]");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(2).forEachRawOutput((p) -> p.toString().endsWith(".pdf"))
                .assertOutputContainsFilenames("Summary.pdf", "Summary(1).pdf");
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
        testContext.listenForTaskFailure();
        execute(parameters);
        testContext.assertTaskFailed();
    }

    @Test
    public void testExecuteLevel4() throws IOException {
        SplitByOutlineLevelParameters parameters = setUpParameters(4, null);
        testContext.listenForTaskFailure();
        execute(parameters);
        testContext.assertTaskFailed();
    }

    @Test
    public void specificResultFilenames() throws IOException {
        SplitByOutlineLevelParameters parameters = setUpParameters(1, null);
        parameters.addSpecificResultFilename("one");
        parameters.addSpecificResultFilename("two");
        parameters.addSpecificResultFilename("some/*?Invalid<chars");
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.assertOutputSize(4).assertOutputContainsFilenames("one.pdf", "two.pdf", "someInvalidchars.pdf");
    }

    @Override
    public Task<SplitByOutlineLevelParameters> getTask() {
        return new SplitByOutlineLevelTask();
    }
}
