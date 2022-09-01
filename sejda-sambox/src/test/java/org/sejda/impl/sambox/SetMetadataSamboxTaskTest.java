/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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
package org.sejda.impl.sambox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sejda.model.input.PdfSource;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.SetMetadataParameters;
import org.sejda.model.pdf.PdfMetadataFields;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDDocumentInformation;
import org.sejda.sambox.util.DateConverter;
import org.sejda.tests.tasks.BaseTaskTest;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * set metadata task test for the pdfbox implementation
 *
 * @author Nero Couvalli
 */
public class SetMetadataSamboxTaskTest extends BaseTaskTest<SetMetadataParameters> {
    private SetMetadataParameters parameters = new SetMetadataParameters();

    private void setUpParams(PdfSource<?> source) {
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_7);
        parameters.put(PdfMetadataFields.AUTHOR, "test_author");
        parameters.put(PdfMetadataFields.KEYWORDS, "test_keywords");
        parameters.put(PdfMetadataFields.SUBJECT, "test_subject");
        parameters.put(PdfMetadataFields.TITLE, "test_title");
        parameters.put("CreationDate", "D:20150814090348+02'00'");
        parameters.put("ModDate", "D:20170814090348+02'00'");
        parameters.put("Producer", "test_producer");
        parameters.put("Custom field", "custom_field_value");

        parameters.removeAllSources();
        parameters.addSource(source);
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
    }

    @Test
    public void testExecute() throws IOException {
        setUpParams(shortInput());
        doExecute();
    }

    @Test
    public void testExecuteEncrypted() throws IOException {
        setUpParams(stronglyEncryptedInput());
        doExecute();
    }

    @Test
    public void removingField() throws IOException {
        SetMetadataParameters parameters = new SetMetadataParameters();
        parameters.addFieldsToRemove(Arrays.asList("Creator", "Author", "RandomStringThatDoesNotExist"));
        parameters.addSource(shortInput());

        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);

        PDDocument document = testContext.assertTaskCompleted();
        PDDocumentInformation info = document.getDocumentInformation();
        assertNull(info.getAuthor());
        assertNull(info.getCreator());
    }

    @Test
    public void multipleFiles() throws IOException {
        SetMetadataParameters parameters = new SetMetadataParameters();
        String author = "test_author_" + new Date().getTime();
        parameters.put(PdfMetadataFields.AUTHOR, author);
        parameters.addSource(shortInput());
        parameters.addSource(mediumInput());

        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.forEachPdfOutput(document -> {
            PDDocumentInformation info = document.getDocumentInformation();
            Assertions.assertEquals(author, info.getAuthor());
        });
    }

    @Test
    public void doesNotChangeProducerAndModificationDate() throws IOException {
        SetMetadataParameters parameters = new SetMetadataParameters();
        parameters.put(PdfMetadataFields.AUTHOR, "test_author");
        parameters.addSource(mediumInput());

        parameters.setUpdateCreatorProducerModifiedDate(false);

        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.forEachPdfOutput(document -> {
            PDDocumentInformation info = document.getDocumentInformation();
            Assertions.assertEquals("iText 2.1.7 by 1T3XT", info.getProducer());
            assertNull(info.getCreator());
            Assertions.assertEquals(DateConverter.toCalendar("D:20111010235709+02'00'"), info.getModificationDate());
        });
    }

    private String getNodeValue(Document xmlDoc, String path) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        Node node = (Node) xPath.compile(path).evaluate(xmlDoc, XPathConstants.NODE);
        if (node != null) {
            return node.getTextContent();
        } else {
            return null;
        }
    }

    @Test
    public void updatedXmpMetadata() throws IOException {
        setUpParams(stronglyEncryptedInput());
        parameters.put(PdfMetadataFields.CREATOR, "test_creator");
        parameters.put("Producer", "test_producer");

        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.forEachPdfOutput(document -> {
            try {
                DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
                DocumentBuilder b = f.newDocumentBuilder();
                Document xmlDoc = b.parse(document.getDocumentCatalog().getMetadata().createInputStream());

                assertEquals("2015-08-14T07:03:48+0000", getNodeValue(xmlDoc, "//*[name()='xmp:CreateDate']"));
                assertEquals("2017-08-14T07:03:48+0000", getNodeValue(xmlDoc, "//*[name()='xmp:ModifyDate']"));
                assertEquals("test_keywords", getNodeValue(xmlDoc, "//*[name()='pdf:Keywords']"));
                assertEquals("test_producer", getNodeValue(xmlDoc, "//*[name()='pdf:Producer']"));
                assertEquals("test_creator", getNodeValue(xmlDoc, "//*[name()='xmp:CreatorTool']"));

                // exact second might be different
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                String nowUptoMinute = dateFormat.format(new Date());

                assertThat(getNodeValue(xmlDoc, "//*[name()='xmp:MetadataDate']"), startsWith(nowUptoMinute));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void updatedXmpMetadataRemoveFields() throws IOException {
        setUpParams(stronglyEncryptedInput());
        parameters.addFieldsToRemove(Arrays.asList("Producer", "ModDate"));

        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.forEachPdfOutput(document -> {
            try {
                DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
                DocumentBuilder b = f.newDocumentBuilder();
                Document xmlDoc = b.parse(document.getDocumentCatalog().getMetadata().createInputStream());

                assertEquals("", getNodeValue(xmlDoc, "//*[name()='xmp:ModifyDate']"));
                assertEquals("", getNodeValue(xmlDoc, "//*[name()='pdf:Producer']"));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void removeAllMetadata() throws IOException {
        setUpParams(stronglyEncryptedInput());
        parameters.setRemoveAllMetadata(true);

        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        testContext.directoryOutputTo(parameters);
        execute(parameters);

        testContext.assertTaskCompleted();
        testContext.forEachPdfOutput(document -> {
            try {
                PDDocumentInformation info = document.getDocumentInformation();

                assertEquals(0, info.getMetadataKeys().size());

                assertNull(info.getCreator());
                assertNull(info.getAuthor());
                assertNull(info.getProducer());
                assertNull(info.getModificationDate());
                assertNull(info.getCreationDate());
                assertNull(info.getKeywords());
                assertNull(info.getTitle());

                assertNull(document.getDocumentCatalog().getMetadata());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void doExecute() throws IOException {
        testContext.directoryOutputTo(parameters);
        execute(parameters);
        PDDocument document = testContext.assertTaskCompleted();
        testContext.assertCreator().assertVersion(PdfVersion.VERSION_1_7);
        PDDocumentInformation info = document.getDocumentInformation();
        assertEquals("test_author", info.getAuthor());
        assertEquals("test_keywords", info.getKeywords());
        assertEquals("test_subject", info.getSubject());
        assertEquals("test_title", info.getTitle());
        assertEquals(DateConverter.toCalendar("D:20150814090348+02'00'"), info.getCreationDate());
        assertEquals("custom_field_value", info.getCustomMetadataValue("Custom field"));

        assertEquals("test_producer", info.getProducer());
        assertEquals(DateConverter.toCalendar("D:20170814090348+02'00'"), info.getModificationDate());
    }

    @Override
    public Task<SetMetadataParameters> getTask() {
        return new SetMetadataTask();
    }

}