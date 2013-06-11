/*
 * Created on 16/ago/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.outline.OutlinePolicy;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.task.Task;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

/**
 * Test for the merge task
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public abstract class MergeTaskTest extends PdfOutEnabledTest implements TestableTask<MergeParameters> {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private MergeParameters parameters;

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
    }

    private void setUpParameters(List<PdfMergeInput> input) {
        parameters = new MergeParameters();
        parameters.setOverwrite(true);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        for (PdfMergeInput current : input) {
            parameters.addInput(current);
        }
        parameters.setOutlinePolicy(OutlinePolicy.RETAIN);
    }

    private List<PdfMergeInput> getInputWithOutline() {
        List<PdfMergeInput> input = new ArrayList<PdfMergeInput>();
        input.add(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/large_outline.pdf"), "first_test_file.pdf")));
        input.add(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/large_test.pdf"), "large_test.pdf")));
        return input;
    }

    private List<PdfMergeInput> getInputWithEncrypted() {
        List<PdfMergeInput> input = new ArrayList<PdfMergeInput>();
        input.add(new PdfMergeInput(PdfStreamSource.newInstanceWithPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/enc_with_modify_perm.pdf"), "enc_with_modify_perm.pdf", "test")));
        input.add(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/large_test.pdf"), "large_test.pdf")));
        return input;
    }

    private List<PdfMergeInput> getInput() {
        List<PdfMergeInput> input = new ArrayList<PdfMergeInput>();
        input.add(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/test_no_outline.pdf"), "first_test_file.pdf")));
        input.add(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/attachments.pdf"), "second_test.pdf")));
        return input;
    }

    @Test
    public void executeMergeAllWithOutlineRetainingOutline() throws TaskException, IOException {
        setUpParameters(getInputWithOutline());
        doExecuteMergeAll(true, 311);
    }

    @Test
    public void executeMergeAllWithEncryptedRetainingOutline() throws TaskException, IOException {
        setUpParameters(getInputWithEncrypted());
        doExecuteMergeAll(true, 310);
    }

    @Test
    public void executeMergeAllRetainingOutline() throws TaskException, IOException {
        setUpParameters(getInput());
        doExecuteMergeAll(false, 4);
    }

    @Test
    public void executeMergeAllWithOutlineDiscardingOutline() throws TaskException, IOException {
        setUpParameters(getInputWithOutline());
        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        doExecuteMergeAll(false, 311);
    }

    @Test
    public void executeMergeAllDiscardingOutline() throws TaskException, IOException {
        setUpParameters(getInput());
        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        doExecuteMergeAll(false, 4);
    }

    @Test
    public void executeMergeAllWithEncryptedDiscardingOutline() throws TaskException, IOException {
        setUpParameters(getInputWithEncrypted());
        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        doExecuteMergeAll(false, 310);
    }

    @Test
    public void executeMergeAllWithOutlineOnePerDoc() throws TaskException, IOException {
        setUpParameters(getInputWithOutline());
        parameters.setOutlinePolicy(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        doExecuteMergeAll(true, 311);
    }

    @Test
    public void executeMergeAllOnePerDoc() throws TaskException, IOException {
        setUpParameters(getInput());
        parameters.setOutlinePolicy(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        doExecuteMergeAll(true, 4);
    }

    @Test
    public void executeMergeAllWithEncryptedOnePerDoc() throws TaskException, IOException {
        setUpParameters(getInputWithEncrypted());
        parameters.setOutlinePolicy(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        doExecuteMergeAll(true, 310);
    }

    void doExecuteMergeAll(boolean hasBookmarks, int pages) throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewFileOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = null;
        try {
            reader = getReaderFromResultFile();
            assertCreator(reader);
            assertVersion(reader, PdfVersion.VERSION_1_6);
            assertEquals(pages, reader.getNumberOfPages());
            if (hasBookmarks) {
                assertNotNull(SimpleBookmark.getBookmark(reader));
            } else {
                assertNull(SimpleBookmark.getBookmark(reader));
            }
        } finally {
            nullSafeCloseReader(reader);
        }
    }

    @Test
    public void testExecuteMergeAllCopyFields() throws TaskException, IOException {
        setUpParameters(getInputWithOutline());
        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        // TODO use input with forms
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewFileOutput(parameters);
        TestUtils.setProperty(parameters, "copyFormFields", Boolean.TRUE);
        victim.execute(parameters);
        PdfReader reader = null;
        try {
            reader = getReaderFromResultFile();
            assertCreator(reader);
            assertVersion(reader, PdfVersion.VERSION_1_6);
            assertEquals(311, reader.getNumberOfPages());
            assertNull(SimpleBookmark.getBookmark(reader));
        } finally {
            nullSafeCloseReader(reader);
        }
    }

    @Test
    public void executeMergeRangesCopyFields() throws TaskException, IOException {
        doExecuteMergeRanges(true);
    }

    @Test
    public void executeMergeRanges() throws TaskException, IOException {
        doExecuteMergeRanges(false);
    }

    public void doExecuteMergeRanges(boolean copyFields) throws TaskException, IOException {
        setUpParameters(getInputWithOutline());
        TestUtils.setProperty(parameters, "copyFormFields", copyFields);
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewFileOutput(parameters);
        for (PdfMergeInput input : parameters.getInputList()) {
            input.addPageRange(new PageRange(3, 10));
            input.addPageRange(new PageRange(20, 23));
            input.addPageRange(new PageRange(80, 90));
        }
        victim.execute(parameters);
        PdfReader reader = null;
        try {
            reader = getReaderFromResultFile();
            assertCreator(reader);
            assertVersion(reader, PdfVersion.VERSION_1_6);
            assertEquals(26, reader.getNumberOfPages());
            List<Map<String, Object>> bookmarks = SimpleBookmark.getBookmark(reader);
            assertNotNull(bookmarks);
            assertBookmarksMerged(bookmarks);
        } finally {
            nullSafeCloseReader(reader);
        }
    }

    private void assertBookmarksMerged(List<Map<String, Object>> bookmarks) {
        boolean found = false;
        if (bookmarks != null) {
            for (Map<String, Object> bookmark : bookmarks) {
                if ("GoTo".equals(bookmark.get("Action"))) {
                    String title = bookmark.get("Title").toString();
                    assertFalse(title.equals("Bookmark1"));
                    if ("Bookmark27".equals(title)) {
                        found = true;
                    }
                }
            }
        }
        assertTrue("Bookmark27 expected but not found", found);
    }

    @Test
    public void testExecuteMergeRangesWithBlankPage() throws TaskException, IOException {
        setUpParameters(getInputWithOutline());
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewFileOutput(parameters);
        for (PdfMergeInput input : parameters.getInputList()) {
            input.addPageRange(new PageRange(2, 4));
        }
        parameters.setBlankPageIfOdd(true);
        victim.execute(parameters);
        PdfReader reader = null;
        try {
            reader = getReaderFromResultFile();
            assertCreator(reader);
            assertVersion(reader, PdfVersion.VERSION_1_6);
            assertEquals(8, reader.getNumberOfPages());
        } finally {
            nullSafeCloseReader(reader);
        }
    }

    protected MergeParameters getParameters() {
        return parameters;
    }
}
