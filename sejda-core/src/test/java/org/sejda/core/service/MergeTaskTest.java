/*
 * Created on 16/ago/2011
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
import org.sejda.model.pdf.form.AcroFormPolicy;
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
        parameters.setCompress(false);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        for (PdfMergeInput current : input) {
            parameters.addInput(current);
        }
        parameters.setOutlinePolicy(OutlinePolicy.RETAIN);
    }

    private List<PdfMergeInput> getInputWithOutline() {
        List<PdfMergeInput> input = new ArrayList<PdfMergeInput>();
        input.add(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/large_outline.pdf"), "first_test_file.pdf")));
        input.add(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/large_test.pdf"), "large_test.pdf")));
        return input;
    }

    private List<PdfMergeInput> getInputWithEncrypted() {
        List<PdfMergeInput> input = new ArrayList<PdfMergeInput>();
        input.add(new PdfMergeInput(PdfStreamSource.newInstanceWithPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/enc_with_modify_perm.pdf"),
                "enc_with_modify_perm.pdf", "test")));
        input.add(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/large_test.pdf"), "large_test.pdf")));
        return input;
    }

    private List<PdfMergeInput> getInput() {
        List<PdfMergeInput> input = new ArrayList<PdfMergeInput>();
        input.add(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/test_no_outline.pdf"), "first_test_file.pdf")));
        input.add(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/attachments.pdf"), "second_test.pdf")));
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

    @Test
    public void executeMergeAllStreamOutput() throws TaskException, IOException {
        setUpParameters(getInput());
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewStreamSingleOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = null;
        try {
            reader = getReaderFromResultStream();
            assertCreator(reader);
            assertVersion(reader, PdfVersion.VERSION_1_6);
            assertEquals(4, reader.getNumberOfPages());
        } finally {
            nullSafeCloseReader(reader);
        }
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
    public void testExecuteMergeAllFields() throws TaskException, IOException {
        setUpParameters(getInputWithOutline());
        parameters.addInput(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/simple_form.pdf"), "second_test_file.pdf")));
        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        parameters.setAcroFormPolicy(AcroFormPolicy.MERGE);
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewFileOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = null;
        try {
            reader = getReaderFromResultFile();
            assertCreator(reader);
            assertVersion(reader, PdfVersion.VERSION_1_6);
            assertEquals(312, reader.getNumberOfPages());
            assertNull(SimpleBookmark.getBookmark(reader));
            assertFalse(reader.getAcroFields().getFields().isEmpty());
        } finally {
            nullSafeCloseReader(reader);
        }
    }

    @Test
    public void testExecuteMergeDiscardForms() throws TaskException, IOException {
        setUpParameters(getInputWithOutline());
        parameters.addInput(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/simple_form.pdf"), "second_test_file.pdf")));

        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        parameters.setAcroFormPolicy(AcroFormPolicy.DISCARD);
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewFileOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = null;
        try {
            reader = getReaderFromResultFile();
            assertCreator(reader);
            assertVersion(reader, PdfVersion.VERSION_1_6);
            assertEquals(312, reader.getNumberOfPages());
            assertNull(SimpleBookmark.getBookmark(reader));
            assertTrue(reader.getAcroFields().getFields().isEmpty());
        } finally {
            nullSafeCloseReader(reader);
        }
    }

    @Test
    public void executeMergeRangesMergeForms() throws TaskException, IOException {
        setUpParameters(getInputWithOutline());
        for (PdfMergeInput input : parameters.getInputList()) {
            input.addPageRange(new PageRange(3, 10));
            input.addPageRange(new PageRange(20, 23));
            input.addPageRange(new PageRange(80, 90));
        }
        parameters.setAcroFormPolicy(AcroFormPolicy.MERGE);
        parameters.addInput(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/simple_form.pdf"), "forms.pdf")));
        doExecuteMergeRanges();
    }

    @Test
    public void executeMergeRanges() throws TaskException, IOException {
        setUpParameters(getInputWithOutline());
        for (PdfMergeInput input : parameters.getInputList()) {
            input.addPageRange(new PageRange(3, 10));
            input.addPageRange(new PageRange(20, 23));
            input.addPageRange(new PageRange(80, 90));
        }
        parameters.setAcroFormPolicy(AcroFormPolicy.DISCARD);
        parameters.addInput(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/simple_form.pdf"), "forms.pdf")));
        doExecuteMergeRanges();
    }

    public void doExecuteMergeRanges() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) getTask());
        initializeNewFileOutput(parameters);
        victim.execute(parameters);
        PdfReader reader = null;
        try {
            reader = getReaderFromResultFile();
            assertCreator(reader);
            assertVersion(reader, PdfVersion.VERSION_1_6);
            assertEquals(27, reader.getNumberOfPages());
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
