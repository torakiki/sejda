/*
 * Created on 12 gen 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.service;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.sejda.util.RequireUtils.requireNotBlank;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.sejda.TestUtils;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.support.util.StringUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.FileSource;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.input.StreamSource;
import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.task.CancellationOption;
import org.sejda.model.task.Task;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.graphics.image.PDImageXObject;
import org.sejda.sambox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Andrea Vacondio
 *
 */
@Ignore
public abstract class BaseTaskTest<T extends TaskParameters> implements TestableTask<T> {

    public final TaskTestContext testContext = new TaskTestContext();
    private DefaultTaskExecutionService service = new DefaultTaskExecutionService();

    @Before
    public void setUp() throws TaskException {
        SejdaContext context = mock(DefaultSejdaContext.class);
        TestUtils.setProperty(service, "context", context);
        when(context.getTask(any())).thenReturn((Task) getTask());
    }

    public void execute(TaskParameters parameters) {
        testContext.listenForTaskFailure();
        testContext.listenForTaskWarnings();
        service.execute(parameters);
    }

    public void execute(TaskParameters parameters, CancellationOption cancellationOption) {
        service.execute(parameters, cancellationOption);
    }

    @After
    public void closeContext() {
        IOUtils.closeQuietly(testContext);
    }

    public PdfStreamSource shortInput() {
        return PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/short-test-file.pdf"), "short-test-file.pdf");
    }

    public PdfStreamSource regularInput() {
        return PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/test-pdf.pdf"), "test-file.pdf");
    }

    public PdfStreamSource mediumInput() {
        return PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/medium_test.pdf"), "medium-test-file.pdf");
    }

    public PdfStreamSource largeInput() {
        return PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/large_test.pdf"), "large-test-file.pdf");
    }

    public PdfStreamSource largeOutlineInput() {
        return PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/large_outline.pdf"),
                "large-outline-test-file.pdf");
    }

    public PdfStreamSource encryptedInput() {
        return PdfStreamSource.newInstanceWithPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/encrypted_AES128_user_pwd.pdf"),
                "encrypted-test-file.pdf", "test");
    }

    public PdfStreamSource formInput() {
        return PdfStreamSource.newInstanceNoPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/forms/two_pages_form.pdf"), "test-form.pdf");
    }

    public PdfStreamSource stronglyEncryptedInput() {
        return PdfStreamSource.newInstanceWithPassword(
                getClass().getClassLoader().getResourceAsStream("pdf/encrypted_AES256_user_pwd.pdf"),
                "strongly-encrypted-test-file.pdf", "test");
    }

    public PdfStreamSource customInput(String path) {
        return PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader().getResourceAsStream(path),
                randomAlphanumeric(16) + ".pdf");
    }

    public PdfFileSource customInputAsFileSource(String path) {
        String filename = new File(path).getName();
        InputStream in = getClass().getClassLoader().getResourceAsStream(path);
        return PdfFileSource.newInstanceNoPassword(streamToTmpFile(in, filename));
    }

    public PdfStreamSource customInput(String path, String name) {
        requireNotBlank(name, "Name cannot be blank");
        return PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader().getResourceAsStream(path), name);
    }

    public PdfStreamSource customEncryptedInput(String path, String password) {
        return PdfStreamSource.newInstanceWithPassword(getClass().getClassLoader().getResourceAsStream(path),
                randomAlphanumeric(16) + ".pdf", password);
    }

    public StreamSource customNonPdfInput(String path) {
        String extension = FilenameUtils.getExtension(path);
        String filename = randomAlphanumeric(16) + "." + extension;
        return customNonPdfInput(path, filename);
    }

    public StreamSource customNonPdfInput(String path, String filename) {
        return StreamSource.newInstance(getClass().getClassLoader().getResourceAsStream(path), filename);
    }

    public FileSource customNonPdfInputAsFileSource(String path) {
        String filename = new File(path).getName();
        InputStream in = getClass().getClassLoader().getResourceAsStream(path);
        return FileSource.newInstance(streamToTmpFile(in, filename));
    }

    private File streamToTmpFile(InputStream in, String filename) {
        try {
            File tmp = org.sejda.core.support.io.IOUtils.createTemporaryBufferWithName(filename);
            OutputStream out = new BufferedOutputStream(new FileOutputStream(tmp));
            IOUtils.copy(in, out);
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
            return tmp;
        } catch (IOException | TaskIOException ex) {
            throw new RuntimeException(ex);
        }

    }

    public void withPageText(PDPage page, Consumer<String> callback) {
        PDFTextStripperByArea textStripper;
        try {
            textStripper = new PDFTextStripperByArea();
            PDRectangle pageSize = page.getCropBox();
            Rectangle cropBoxRectangle = new Rectangle(0, 0, (int) pageSize.getWidth(), (int) pageSize.getHeight());
            if(page.getRotation() == 90 || page.getRotation() == 270) {
                cropBoxRectangle = new Rectangle(0, 0, (int) pageSize.getHeight(), (int) pageSize.getWidth());
            }
            textStripper.setSortByPosition(true);
            textStripper.addRegion("area1", cropBoxRectangle);
            textStripper.extractRegions(page);
            callback.accept(textStripper.getTextForRegion("area1"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    public void assertPageText(PDPage page, String text) {
        withPageText(page, pageText -> {
            assertEquals(text, pageText.replaceAll("[^A-Za-z0-9]", ""));
        });
    }

    public void assertPageTextContains(PDPage page, String text) {
        withPageText(page, pageText -> {
            // ignores whitespace
            pageText = StringUtils.normalizeWhitespace(pageText);
            pageText = pageText.replaceAll("\\s", "");
            assertThat(pageText, containsString(text));
        });
    }

    public void assertMediaBox(PDPage page, float width, float height) {
        assertEquals(page.getMediaBox().getWidth(), width, 0.01);
        assertEquals(page.getMediaBox().getHeight(), height, 0.01);
    }

    public <T> List<T> getAnnotationsOf(PDPage page, Class<T> clazz) {
        return iteratorToList(page.getAnnotations().stream()
                .filter(a -> clazz.isInstance(a))
                .map(a -> (T) a)
                .iterator());
    }

    // returns 1-based page numbers
    public List<Integer> getPagesContainingImages(PDDocument doc) {
        List<Integer> result = new ArrayList<>();
        for(int i = 0; i < doc.getNumberOfPages(); i++) {
            PDPage page = doc.getPage(i);
            boolean hasImages = false;
            for(COSName name: page.getResources().getXObjectNames()) {
                try {
                    if(page.getResources().getXObject(name) instanceof PDImageXObject) {
                        hasImages = true;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(hasImages) {
                result.add(i + 1);
            }
        }
        return result;
    }

    public <T> List<T> iteratorToList(Iterator<T> iterator) {
        List<T> result = new ArrayList<>();
        while(iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }
}
