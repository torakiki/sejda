/*
 * Created on 04/lug/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.sejda.io.SeekableSources;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.text.PDFTextStripperByArea;
import org.junit.Ignore;
import org.sejda.core.Sejda;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.output.StreamTaskOutput;
import org.sejda.model.parameter.base.MultipleOutputTaskParameters;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;
import org.sejda.model.pdf.PdfMetadataKey;
import org.sejda.model.pdf.PdfVersion;

import com.lowagie.text.pdf.PdfReader;

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

    /**
     * @param expectedFileName
     *            the expected name of the first file in the ZipInputStream
     * @param ownerPwd
     *            owner password
     * @return a {@link PdfReader} opened on the first resulting file found in the ZipInputStream coming form the manipulation.
     * @throws IOException
     */
    protected PdfReader getReaderFromResultStream(String expectedFileName, byte[] ownerPwd) throws IOException {
        PdfReader reader = new PdfReader(getResultInputStream(expectedFileName), ownerPwd);
        reader.removeUnusedObjects();
        reader.consolidateNamedDestinations();
        return reader;
    }

    protected PdfReader getReaderFromResultStream() throws IOException {
        return getReaderFromResultStream(null);
    }

    protected InputStream getResultInputStream(String expectedFileName) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
        ZipInputStream zip = new ZipInputStream(input);
        ZipEntry entry = zip.getNextEntry();
        while(entry != null) {
            if(expectedFileName == null || entry.getName().equals(expectedFileName)){
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
        assertArrayEquals(expectedFilenames, actualFilenames.toArray(new String[actualFilenames.size()]));
    }

    /**
     * @param expectedFileName
     *            the expected name of the first file in the ZipInputStream
     * @return a {@link PdfReader} opened on the first resulting file found in the ZipInputStream coming form the manipulation.
     * @throws IOException
     */
    protected PdfReader getReaderFromResultStream(String expectedFileName) throws IOException {
        return getReaderFromResultStream(expectedFileName, null);
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
        assertEquals("Number of pages don't match", expected, getReaderFromResultStream().getNumberOfPages());
    }

    public void assertPageText(String... expectedText) throws IOException {
        for(int pageNumber = 0; pageNumber < expectedText.length; pageNumber++) {
            PDFTextStripperByArea textStripper = new PDFTextStripperByArea();

            PDDocument doc = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(getResultInputStream(null)));
            PDPage page = doc.getDocumentCatalog().getPages().get(pageNumber);

            PDRectangle rect = page.getCropBox();
            // reposition rectangle to match text space
            float x = rect.getLowerLeftX();
            float y = rect.getUpperRightY();
            float width = rect.getWidth();
            float height = rect.getHeight();
            int rotation = page.getRotation();
            if (rotation == 0) {
                PDRectangle pageSize = page.getMediaBox();
                y = pageSize.getHeight() - y;
            }
            Rectangle2D.Float awtRect = new Rectangle2D.Float(x, y, width, height);

            textStripper.setSortByPosition(true);
            textStripper.addRegion("area1", awtRect);
            textStripper.extractRegions(page);

            String actualText = textStripper.getTextForRegion("area1").replaceAll("[^A-Za-z0-9]", "");
            assertEquals("Page " + (pageNumber + 1) + " text", expectedText[pageNumber], actualText);
        }
    }
}
