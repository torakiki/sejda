/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import static org.apache.commons.io.FilenameUtils.separatorsToWindows;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.CombinableMatcher.either;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.sejda.cli.command.StandardTestableTask;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.outline.OutlinePolicy;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.pdf.form.AcroFormPolicy;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.toc.ToCPolicy;

/**
 * Tests for the MergeTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class MergeTaskTest extends AbstractTaskTest {

    public MergeTaskTest() {
        super(StandardTestableTask.MERGE);
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();

        createTestPdfFile("/tmp/merge/file1.pdf");
        createTestPdfFile("/tmp/merge/file2.pdf");
        createTestPdfFile("/tmp/merge/file3.pdf");
        createTestPdfFile("/tmp/merge/file4.pdf");
        createTestPdfFile("/tmp/merge/file5.txt");
        createTestPdfFile("/tmp/merge/subdir/file5.pdf");

        createTestTextFile("./location/filenames.csv",
                "/tmp/merge/file3.pdf, /tmp/merge/file1.pdf, /tmp/merge/file2.pdf");
        createTestTextFile("./location/empty_filenames.csv", "");
        createTestTextFile("./location/filenames_invalidPaths.csv",
                "/tmp/merge/fileDoesntExist.pdf,/tmp/merge/file1.pdf");

        createTestTextFile("/tmp/filenames.xml", getClass().getResourceAsStream("/merge-filelist-config.xml"));
        // files inside
        createTestPdfFile("/tmp/pdf/inputFile.pdf");
        createTestPdfFile("/tmp/pdf/inputFile2.pdf");
        createTestPdfFile("/tmp/inputFile1.pdf");
        createTestPdfFile("/tmp/inputFile2.pdf");
        createTestPdfFile("/tmp/subdir/inputFile1.pdf");
        createTestPdfFile("/tmp/subdir3/inputFile2.pdf");
        createTestPdfFile("/tmp/subdir2/inputFile1.pdf");
        createTestPdfFile("/tmp/subdir2/inputFile2.pdf");
        createTestPdfFile("/tmp/subdir2/inputFile3.pdf");

        createTestTextFile("./location/filenames_invalidXml.xml", "<filelist><file value=\"/tmp/merge/file1.pdf \">");
        createTestFolder("/tmp/emptyFolder");
        createTestPdfFile("./location/filenames.xls");
    }

    @Test
    public void onAddBlanks() {
        MergeParameters parameters = defaultCommandLine().withFlag("--addBlanks").invokeSejdaConsole();
        assertTrue(parameters.isBlankPageIfOdd());
    }

    @Test
    public void offAddBlankss() {
        MergeParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertFalse(parameters.isBlankPageIfOdd());
    }

    @Test
    public void onDiscardBookmarks() {
        MergeParameters parameters = defaultCommandLine().with("-b", "discard").invokeSejdaConsole();
        assertEquals(OutlinePolicy.DISCARD, parameters.getOutlinePolicy());
    }

    @Test
    public void onOneEachDoc() {
        MergeParameters parameters = defaultCommandLine().with("-b", "one_entry_each_doc").invokeSejdaConsole();
        assertEquals(OutlinePolicy.ONE_ENTRY_EACH_DOC, parameters.getOutlinePolicy());
    }

    @Test
    public void addFooter() {
        MergeParameters parameters = defaultCommandLine().withFlag("--footer").invokeSejdaConsole();
        assertTrue(parameters.isFilenameFooter());
    }

    @Test
    public void onDefault() {
        MergeParameters parameters = defaultCommandLine().invokeSejdaConsole();
        assertEquals(OutlinePolicy.RETAIN, parameters.getOutlinePolicy());
        assertEquals(AcroFormPolicy.DISCARD, parameters.getAcroFormPolicy());
        assertEquals(ToCPolicy.NONE, parameters.getTableOfContentsPolicy());
        assertFalse(parameters.isFilenameFooter());
    }

    @Test
    public void onRetainAsOneEntry() {
        MergeParameters parameters = defaultCommandLine().with("-b", "retain_as_one_entry").invokeSejdaConsole();
        assertEquals(OutlinePolicy.RETAIN_AS_ONE_ENTRY, parameters.getOutlinePolicy());
    }

    @Test
    public void onMergeAcroForms() {
        MergeParameters parameters = defaultCommandLine().with("-a", "merge").invokeSejdaConsole();
        assertEquals(AcroFormPolicy.MERGE, parameters.getAcroFormPolicy());
    }

    @Test
    public void onFlattenAcroForms() {
        MergeParameters parameters = defaultCommandLine().with("-a", "flatten").invokeSejdaConsole();
        assertEquals(AcroFormPolicy.FLATTEN, parameters.getAcroFormPolicy());
    }

    @Test
    public void onMergeRenamingAcroForms() {
        MergeParameters parameters = defaultCommandLine().with("-a", "merge_renaming").invokeSejdaConsole();
        assertEquals(AcroFormPolicy.MERGE_RENAMING_EXISTING_FIELDS, parameters.getAcroFormPolicy());
    }

    @Test
    public void onTextNamesToC() {
        MergeParameters parameters = defaultCommandLine().with("-t", "file_names").invokeSejdaConsole();
        assertEquals(ToCPolicy.FILE_NAMES, parameters.getTableOfContentsPolicy());
    }

    @Test
    public void onTextTitlesToC() {
        MergeParameters parameters = defaultCommandLine().with("-t", "doc_titles").invokeSejdaConsole();
        assertEquals(ToCPolicy.DOC_TITLES, parameters.getTableOfContentsPolicy());
    }

    @Test
    public void folderInput() {
        MergeParameters parameters = defaultCommandLine().without("-f").with("-d", "/tmp/merge").invokeSejdaConsole();

        assertPdfMergeInputsFilesList(parameters, filesList("/tmp/merge/file1.pdf", "/tmp/merge/file2.pdf",
                "/tmp/merge/file3.pdf", "/tmp/merge/file4.pdf"));
    }

    @Test
    public void folderInputWithRegexp() {
        MergeParameters parameters = defaultCommandLine().without("-f").with("-d", "/tmp/merge")
                .with("-e", "([^\\s]+(1|4)(?i)(\\.pdf)$)").invokeSejdaConsole();

        assertPdfMergeInputsFilesList(parameters, filesList("/tmp/merge/file1.pdf", "/tmp/merge/file4.pdf"));
    }

    @Test
    public void folderInputWithRegexpFilteringEverythig() {
        defaultCommandLine().without("-f").with("-d", "/tmp/merge").with("-e", "([^\\s]+(\\.(norris))$)")
                .assertConsoleOutputContains("No input files specified in");

    }

    @Test
    public void wildcardInput() {
        MergeParameters parameters = defaultCommandLine().without("-f")
                .with("-f", "/tmp/merge/*.pdf /tmp/inputFile1.pdf").invokeSejdaConsole();

        assertPdfMergeInputsFilesList(parameters, filesList("/tmp/merge/file1.pdf", "/tmp/merge/file2.pdf",
                "/tmp/merge/file3.pdf", "/tmp/merge/file4.pdf", "/tmp/inputFile1.pdf"));
    }

    @Test
    public void folderInput_emptyFolder() {
        defaultCommandLine().with("-d", "/tmp/emptyFolder").assertConsoleOutputContains("No input files specified in");
    }

    private static List<Matcher<Iterable<? super String>>> filesList(String... filenames) {
        List<Matcher<Iterable<? super String>>> result = new ArrayList<Matcher<Iterable<? super String>>>();
        for (String current : filenames) {
            String filename = current.toString();
            result.add(either(hasItem(filename)).or(hasItem(endsWith(separatorsToWindows(filename)))));
        }
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
                .assertConsoleOutputContains("does not exist");
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
                .assertConsoleOutputContains("No input files specified in ");
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
    public void input_noOptionGiven() {
        defaultCommandLine().without("-f").without("-d").without("-l")
                .assertConsoleOutputContains("No option given for input");
    }

    @Test
    public void input_tooManyOptionsGiven() {
        defaultCommandLine().without("-f").with("-d", "/tmp/merge").with("-l", "./location/filenames.xls")
                .assertConsoleOutputContains("Too many options given");
    }

    private void assertPdfMergeInputsFilesList(MergeParameters parameters,
            Collection<Matcher<Iterable<? super String>>> expectedFilesMatchers) {
        assertPdfMergeInputsFilesList(parameters, expectedFilesMatchers,
                nullsFilledList(parameters.getInputList().size()));
    }

    private List<String> nullsFilledList(int size) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            result.add(null);
        }

        return result;
    }

    private void assertPdfMergeInputsFilesList(MergeParameters parameters,
            Collection<Matcher<Iterable<? super String>>> expectedFilesMatchers, List<String> expectedFilesPasswords) {
        List<String> actualFileList = new ArrayList<String>();
        List<String> actualPasswords = new ArrayList<String>();

        for (int i = 0; i < parameters.getInputList().size(); i++) {
            PdfMergeInput each = parameters.getPdfInputList().get(i);
            PdfFileSource pdfFileSource = (PdfFileSource) each.getSource();
            actualFileList.add(pdfFileSource.getSource().getAbsolutePath());
            actualPasswords.add(pdfFileSource.getPassword());
        }

        for (Matcher<Iterable<? super String>> expectedFileMatcher : expectedFilesMatchers) {
            assertThat(actualFileList, expectedFileMatcher);
        }
        assertEquals(expectedFilesPasswords, actualPasswords);
    }

    @Test
    public void fileListConfigInput_xml() {
        MergeParameters parameters = defaultCommandLine().without("-f").with("-l", "/tmp/filenames.xml")
                .invokeSejdaConsole();

        assertPdfMergeInputsFilesList(parameters,
                filesList("/tmp/pdf/inputFile.pdf", "/tmp/pdf/inputFile2.pdf", "/tmp/inputFile1.pdf",
                        "/tmp/inputFile2.pdf", "/tmp/subdir/inputFile1.pdf", "/tmp/subdir3/inputFile2.pdf",
                        "/tmp/subdir2/inputFile1.pdf", "/tmp/subdir2/inputFile2.pdf", "/tmp/subdir2/inputFile3.pdf"),
                Arrays.asList(null, "test", null, null, null, null, null, "secret2", null));
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
        assertHasPdfMergeInput(parameters, "inputs/second_input.pdf",
                Arrays.asList(new PageRange(3, 3), new PageRange(5, 5), new PageRange(8, 10), new PageRange(2, 2),
                        new PageRange(9, 9), new PageRange(30)));
    }

    private void assertHasPdfMergeInput(MergeParameters parameters, String filename,
            Collection<PageRange> expectedPageRanges) {
        assertHasPdfMergeInput(parameters, filename, NO_PASSWORD, expectedPageRanges);
    }

    private void assertHasPdfMergeInput(MergeParameters parameters, String filename, String password,
            Collection<PageRange> expectedPageRanges) {
        boolean found = false;
        File file = new File(filename);
        for (PdfMergeInput each : parameters.getPdfInputList()) {
            PdfFileSource pdfFileSource = (PdfFileSource) each.getSource();
            if (matchesPdfFileSource(file, password, pdfFileSource)) {
                assertContainsAll("For file " + pdfFileSource.getName(), expectedPageRanges, each.getPageSelection());
                found = true;
            }
        }

        assertTrue(
                "File '" + file + "'"
                        + (StringUtils.isEmpty(password) ? " and no password" : " and password '" + password + "'"),
                found);

    }
}
