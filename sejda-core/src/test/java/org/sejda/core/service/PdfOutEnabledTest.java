/*
 * Created on 04/lug/2010
 *
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Ignore;
import org.sejda.core.Sejda;
import org.sejda.io.SeekableSources;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.output.StreamTaskOutput;
import org.sejda.model.parameter.base.MultipleOutputTaskParameters;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;
import org.sejda.model.pdf.PdfMetadataKey;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.text.PDFTextStripperByArea;

/**
 * Parent test class with common methods to read results or common asserts.
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public class PdfOutEnabledTest {

    private ByteArrayOutputStream out;
    private File outFile;

    /**
     * Initialize the input parameters with a new {@link StreamTaskOutput}
     * 
     * @param parameters
     */
    void initializeNewStreamOutput(MultipleOutputTaskParameters parameters) {
        out = new ByteArrayOutputStream();
        StreamTaskOutput pdfOut = new StreamTaskOutput(out);
        parameters.setOutput(pdfOut);
    }

    void initializeNewStreamSingleOutput(SingleOutputTaskParameters parameters) {
        out = new ByteArrayOutputStream();
        StreamTaskOutput pdfOut = new StreamTaskOutput(out);
        parameters.setOutput(pdfOut);
    }

    void initializeNewFileOutput(SingleOutputTaskParameters parameters) throws IOException {
        outFile = File.createTempFile("SejdaTest", ".pdf");
        outFile.deleteOnExit();
        FileTaskOutput pdfOut = new FileTaskOutput(outFile);
        parameters.setOutput(pdfOut);
    }

    /**
     * 
     * @return a {@link PdfReader} opened on temporary output file containing the result of the manipulation.
     * @throws IOException
     */
    protected PdfReader getReaderFromResultFile() throws IOException {
        return new PdfReader(new FileInputStream(outFile));
    }

    protected PdfReader getReaderFromResultStream() throws IOException {
        return new PdfReader(new ByteArrayInputStream(out.toByteArray()));
    }

    /**
     * @param expectedFileName
     *            the expected name of the first file in the ZipInputStream
     * @param ownerPwd
     *            owner password
     * @return a {@link PdfReader} opened on the first resulting file found in the ZipInputStream coming form the manipulation.
     * @throws IOException
     */
    protected PdfReader getReaderFromResultZipStream(String expectedFileName, byte[] ownerPwd) throws IOException {
        PdfReader reader = new PdfReader(getResultInputStream(expectedFileName), ownerPwd);
        reader.removeUnusedObjects();
        reader.consolidateNamedDestinations();
        return reader;
    }

    protected PdfReader getReaderFromResultZipStream() throws IOException {
        return getReaderFromResultZipStream(null);
    }

    protected InputStream getResultInputStream(String expectedFileName) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
        ZipInputStream zip = new ZipInputStream(input);
        ZipEntry entry = zip.getNextEntry();
        while (entry != null) {
            if (expectedFileName == null || entry.getName().equals(expectedFileName)) {
                return zip;
            }
            entry = zip.getNextEntry();
        }

        throw new RuntimeException("Didn't find any output file that matched " + expectedFileName);
    }

    /**
     * Assert the the generated output zip stream contains the given number of entries.
     * 
     * @param expectedNumberOfDocuments
     * @throws IOException
     */
    protected void assertOutputContainsDocuments(int expectedNumberOfDocuments) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
        ZipInputStream zip = new ZipInputStream(input);
        int counter = 0;
        while (zip.getNextEntry() != null) {
            counter++;
        }
        assertEquals(expectedNumberOfDocuments, counter);
    }

    /**
     * Assert the the generated output zip stream contains the given file names.
     *
     * @param expectedFilenames
     * @throws IOException
     */
    protected void assertOutputContainsFilenames(String... expectedFilenames) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
        ZipInputStream zip = new ZipInputStream(input);
        List<String> actualFilenames = new LinkedList<String>();
        for (ZipEntry e; (e = zip.getNextEntry()) != null;) {
            actualFilenames.add(e.getName());
        }
        Collections.sort(actualFilenames);
        for (String expectedFilename : expectedFilenames) {
            assertThat(actualFilenames, hasItem(expectedFilename));
        }
    }

    /**
     * @param expectedFileName
     *            the expected name of the first file in the ZipInputStream
     * @return a {@link PdfReader} opened on the first resulting file found in the ZipInputStream coming form the manipulation.
     * @throws IOException
     */
    protected PdfReader getReaderFromResultZipStream(String expectedFileName) throws IOException {
        return getReaderFromResultZipStream(expectedFileName, null);
    }

    /**
     * Assert the correct creator
     * 
     * @param reader
     */
    protected void assertCreator(PdfReader reader) {
        HashMap<String, String> meta = reader.getInfo();
        assertEquals(Sejda.CREATOR, meta.get(PdfMetadataKey.CREATOR.getKey()));
    }

    /**
     * Assert that the version on the read document is the same as the input one
     * 
     * @param reader
     * @param version
     */
    protected void assertVersion(PdfReader reader, PdfVersion version) {
        assertEquals(version.getVersionAsCharacter(), reader.getPdfVersion());
    }

    protected void nullSafeCloseReader(PdfReader reader) {
        if (reader != null) {
            reader.close();
        }
    }

    public File getResultFile() {
        return outFile;
    }

    void assertNumberOfPages(int expected) throws IOException {
        assertEquals("Number of pages don't match", expected, getReaderFromResultZipStream().getNumberOfPages());
    }

    void assertNumberOfPages(String filename, int expectedNumberOfPages) throws IOException {
        int actualNumerOfPages = getReaderFromResultZipStream(filename).getNumberOfPages();
        assertEquals("Number of pages don't match for " + filename, expectedNumberOfPages, actualNumerOfPages);
    }

    public void assertPageText(String... expectedText) throws IOException {
        for (int pageNumber = 0; pageNumber < expectedText.length; pageNumber++) {
            PDFTextStripperByArea textStripper = new PDFTextStripperByArea();

            PDDocument doc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(getResultInputStream(null)));
            PDPage page = doc.getDocumentCatalog().getPages().get(pageNumber);

            PDRectangle pageSize = page.getCropBox();
            Rectangle cropBoxRectangle = new Rectangle(0, 0, (int)pageSize.getWidth(), (int)pageSize.getHeight());
            textStripper.setSortByPosition(true);
            textStripper.addRegion("area1", cropBoxRectangle);
            textStripper.extractRegions(page);

            String actualText = textStripper.getTextForRegion("area1").replaceAll("[^A-Za-z0-9]", "");
            assertEquals("Page " + (pageNumber + 1) + " text", expectedText[pageNumber], actualText);
        }
    }
}
