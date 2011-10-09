/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.sejda.core.manipulation.model.input.PdfFileSource;
import org.sejda.core.manipulation.model.input.PdfMergeInput;
import org.sejda.core.manipulation.model.parameter.MergeParameters;
import org.sejda.core.manipulation.model.pdf.page.PageRange;

/**
 * Tests for the MergeTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class MergeTaskTest extends AbstractTaskTest {

    public MergeTaskTest() {
        super(TestableTask.MERGE);
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();

        createTestPdfFile("/tmp/merge/file1.pdf");
        createTestPdfFile("/tmp/merge/file2.pdf");
        createTestPdfFile("/tmp/merge/file3.pdf");
        createTestPdfFile("/tmp/merge/file4.pdf");

        createTestTextFile("./location/filenames.csv",
                "/tmp/merge/file3.pdf, /tmp/merge/file1.pdf, /tmp/merge/file2.pdf");
        createTestTextFile("./location/empty_filenames.csv", "");
        createTestTextFile("./location/filenames_invalidPaths.csv",
                "/tmp/merge/fileDoesntExist.pdf,/tmp/merge/file1.pdf");
        createTestTextFile(
                "./location/filenames.xml",
                "<filelist><file value=\"/tmp/merge/file1.pdf \"/><file value=\"/tmp/merge/file4.pdf\"/><file value=\"/tmp/merge/file3.pdf\"/></filelist>");
        createTestTextFile("./location/filenamesPasswordProtected.xml",
                "<filelist><file value=\"/tmp/merge/file1.pdf:secret1 \"/><file value=\"/tmp/merge/file4.pdf:secret4\"/></filelist>");
        createTestTextFile("./location/filenames_invalidXml.xml", "<filelist><file value=\"/tmp/merge/file1.pdf \">");
        createTestFolder("/tmp/emptyFolder");
        createTestPdfFile("./location/filenames.xls");
    }

    @Test
    public void onCopyFields() {
        MergeParameters parameters = defaultCommandLine().with("--copyFields").invokeSejdaConsole();
        assertTrue(parameters.isCopyFormFields());
    }

    @Test
    public void offCopyFields() {
        MergeParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertFalse(parameters.isCopyFormFields());
    }

    @Test
    public void folderInput() {
        MergeParameters parameters = defaultCommandLine().without("-f").with("-d", "/tmp/merge").invokeSejdaConsole();

        assertPdfMergeInputsFilesList(
                parameters,
                filesList("/tmp/merge/file1.pdf", "/tmp/merge/file2.pdf", "/tmp/merge/file3.pdf",
                        "/tmp/merge/file4.pdf"));
    }

    @Test
    public void folderInput_emptyFolder() {
        defaultCommandLine().with("-d", "/tmp/emptyFolder").assertConsoleOutputContains("No input files specified in");
    }

    private static List<File> filesList(String... filenames) {
        List<File> result = new ArrayList<File>();
        CollectionUtils.collect(Arrays.asList(filenames), new Transformer() {

            public Object transform(Object input) {
                return new File(input.toString());
            }
        }, result);

        return result;
    }

    @Test
    public void fileListConfigInput_csv() {
        MergeParameters parameters = defaultCommandLine().without("-f").with("-l", "./location/filenames.csv")
                .invokeSejdaConsole();

        assertPdfMergeInputsFilesList(parameters,
                filesList("/tmp/merge/file3.pdf", "/tmp/merge/file1.pdf", "/tmp/merge/file2.pdf"));
    }

    @Test
    public void fileListConfigInput_csv_invalidPaths() {
        defaultCommandLine().without("-f").with("-l", "./location/filenames_invalidPaths.csv")
                .assertConsoleOutputContains("Invalid filename found");
    }

    @Test
    public void fileListConfigInput_csv_doesntExist() {
        defaultCommandLine().without("-f").with("-l", "./location/doesntExist.csv")
                .assertConsoleOutputContains("File './location/doesntExist.csv' does not exist");
    }

    @Test
    public void fileListConfigInput_xml() {
        MergeParameters parameters = defaultCommandLine().without("-f").with("-l", "./location/filenames.xml")
                .invokeSejdaConsole();

        assertPdfMergeInputsFilesList(parameters,
                filesList("/tmp/merge/file1.pdf", "/tmp/merge/file4.pdf", "/tmp/merge/file3.pdf"));
    }

    @Test
    public void fileListConfigInput_xml_invalidXml() {
        defaultCommandLine().without("-f").with("-l", "./location/filenames_invalidXml.xml")
                .assertConsoleOutputContains("Can't extract filenames from");
    }

    @Test
    public void fileListConfigInput_xml_invalidContents() {
        defaultCommandLine().without("-f").with("-l", "./location/filenames_invalidXml.xml")
                .assertConsoleOutputContains("Can't extract filenames from");
    }

    @Test
    public void inputFiles_csv_empty() {
        defaultCommandLine().without("-f").with("-l", "./location/empty_filenames.csv")
                .assertConsoleOutputContains("No input files specified in './location/empty_filenames.csv'");
    }

    @Test
    public void inputFiles_unsupportedFormatConfigList() {
        defaultCommandLine().without("-f").with("-l", "./location/filenames.xls")
                .assertConsoleOutputContains("Unsupported file format: xls");
    }

    @Test
    public void filesInput() {
        MergeParameters parameters = defaultCommandLine().with("-f", "/tmp/merge/file4.pdf /tmp/merge/file2.pdf")
                .invokeSejdaConsole();

        assertPdfMergeInputsFilesList(parameters, filesList("/tmp/merge/file4.pdf", "/tmp/merge/file2.pdf"));
    }

    @Test
    public void input_tooManyOptionsGiven() {
        defaultCommandLine().without("-f").with("-d", "/tmp/merge").with("-l", "./location/filenames.xls")
                .assertConsoleOutputContains("Unsupported file format: xls");
    }

    private void assertPdfMergeInputsFilesList(MergeParameters parameters, List<File> expectedFilesList) {
        assertPdfMergeInputsFilesList(parameters, expectedFilesList, nullsFilledList(expectedFilesList.size()));
    }

    private List<String> nullsFilledList(int size) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            result.add(null);
        }

        return result;
    }

    private void assertPdfMergeInputsFilesList(MergeParameters parameters, List<File> expectedFilesList,
            List<String> expectedFilesPasswords) {
        List<File> actualFileList = new ArrayList<File>();
        List<String> actualPasswords = new ArrayList<String>();

        for (int i = 0; i < parameters.getInputList().size(); i++) {
            PdfMergeInput each = parameters.getInputList().get(i);
            PdfFileSource pdfFileSource = (PdfFileSource) each.getSource();
            actualFileList.add(pdfFileSource.getFile());
            actualPasswords.add(pdfFileSource.getPassword());
        }

        assertEquals(expectedFilesList, actualFileList);
        assertEquals(expectedFilesPasswords, actualPasswords);
    }

    @Test
    public void fileListConfigInput_xmlWithPasswordProtectedFilesInside() {
        MergeParameters parameters = defaultCommandLine().without("-f")
                .with("-l", "./location/filenamesPasswordProtected.xml").invokeSejdaConsole();

        assertPdfMergeInputsFilesList(parameters, filesList("/tmp/merge/file1.pdf", "/tmp/merge/file4.pdf"),
                Arrays.asList("secret1", "secret4"));
    }

    private static final String NO_PASSWORD = null;
    private static final Set<PageRange> NO_PAGE_RANGE_SPECIFIED = Collections.emptySet();
    private static final Set<PageRange> ALL_PAGES = Collections.emptySet();

    @Test
    public void pageRange_empty() {
        MergeParameters parameters = defaultCommandLine().invokeSejdaConsole();

        assertHasPdfMergeInput(parameters, "inputs/input.pdf", NO_PAGE_RANGE_SPECIFIED);
    }

    @Test
    public void pageRange_simpleInterval() {
        MergeParameters parameters = defaultCommandLine().with("-s", "3003-3010").invokeSejdaConsole();

        assertHasPdfMergeInput(parameters, "inputs/input.pdf", Arrays.asList(new PageRange(3003, 3010)));
        assertHasPdfMergeInput(parameters, "inputs/second_input.pdf", NO_PAGE_RANGE_SPECIFIED);
    }

    @Test
    public void pageRange_openedInterval() {
        MergeParameters parameters = defaultCommandLine().with("-s", "2-").invokeSejdaConsole();

        assertHasPdfMergeInput(parameters, "inputs/input.pdf", Arrays.asList(new PageRange(2)));
        assertHasPdfMergeInput(parameters, "inputs/second_input.pdf", NO_PAGE_RANGE_SPECIFIED);
    }

    @Test
    public void pageRange_singlePage() {
        MergeParameters parameters = defaultCommandLine().with("-s", "3003").invokeSejdaConsole();

        assertHasPdfMergeInput(parameters, "inputs/input.pdf", Arrays.asList(new PageRange(3003, 3003)));
        assertHasPdfMergeInput(parameters, "inputs/second_input.pdf", NO_PAGE_RANGE_SPECIFIED);
    }

    @Test
    public void pageRange_allPages() {
        MergeParameters parameters = defaultCommandLine().with("-s", "all").invokeSejdaConsole();

        assertHasPdfMergeInput(parameters, "inputs/input.pdf", ALL_PAGES);
        assertHasPdfMergeInput(parameters, "inputs/second_input.pdf", NO_PAGE_RANGE_SPECIFIED);
    }

    @Test
    public void pageRange_combined() {
        MergeParameters parameters = defaultCommandLine().with("-s", "all:3,5,8-10,2,2,9-9,30-").invokeSejdaConsole();

        assertHasPdfMergeInput(parameters, "inputs/input.pdf", ALL_PAGES);
        assertHasPdfMergeInput(parameters, "inputs/second_input.pdf", Arrays.asList(new PageRange(3, 3), new PageRange(
                5, 5), new PageRange(8, 10), new PageRange(2, 2), new PageRange(9, 9), new PageRange(30)));
    }

    private void assertHasPdfMergeInput(MergeParameters parameters, String filename,
            Collection<PageRange> expectedPageRanges) {
        assertHasPdfMergeInput(parameters, filename, NO_PASSWORD, expectedPageRanges);
    }

    private void assertHasPdfMergeInput(MergeParameters parameters, String filename, String password,
            Collection<PageRange> expectedPageRanges) {
        boolean found = false;
        File file = new File(filename);
        for (PdfMergeInput each : parameters.getInputList()) {
            PdfFileSource pdfFileSource = (PdfFileSource) each.getSource();
            if (matchesPdfFileSource(file, password, pdfFileSource)) {
                assertContainsAll("For file " + pdfFileSource.getName(), expectedPageRanges, each.getPageSelection());
                found = true;
            }
        }

        assertTrue("File '" + file + "'"
                + (StringUtils.isEmpty(password) ? " and no password" : " and password '" + password + "'"), found);

    }
}
