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
package org.sejda.tests.tasks;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.sejda.commons.util.IOUtils;
import org.sejda.core.Sejda;
import org.sejda.core.notification.context.GlobalNotificationContext;
import org.sejda.io.SeekableSource;
import org.sejda.io.SeekableSources;
import org.sejda.model.SejdaFileExtensions;
import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.TaskExecutionFailedEvent;
import org.sejda.model.notification.event.TaskExecutionWarningEvent;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.output.FileOrDirectoryTaskOutput;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.parameter.base.MultipleOutputTaskParameters;
import org.sejda.model.parameter.base.SingleOrMultipleOutputTaskParameters;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.sejda.sambox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.sejda.tests.TestListenerFactory;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Context to be used in tests
 *
 * @author Andrea Vacondio
 */
public class TaskTestContext implements Closeable {

    private ByteArrayOutputStream streamOutput;
    private File fileOutput;
    private PDDocument outputDocument;

    /**
     * Initialize the given params with a {@link FileTaskOutput}
     *
     * @param params
     * @return
     */
    public TaskTestContext pdfOutputTo(SingleOutputTaskParameters params) throws IOException {
        this.fileOutput = File.createTempFile("SejdaTest", ".pdf");
        this.fileOutput.deleteOnExit();
        params.setOutput(new FileTaskOutput(fileOutput));
        return this;
    }

    /**
     * Initialize the given params with a {@link FileTaskOutput} on a file with the given extension
     *
     * @param params
     * @param extension
     * @return
     * @throws IOException
     */
    public TaskTestContext fileOutputTo(SingleOutputTaskParameters params, String extension) throws IOException {
        this.fileOutput = File.createTempFile("SejdaTest", extension);
        this.fileOutput.deleteOnExit();
        params.setOutput(new FileTaskOutput(fileOutput));
        return this;
    }

    /**
     * Initialize the given params with a {@link DirectoryTaskOutput}
     *
     * @param params
     * @return
     * @throws IOException
     */
    public TaskTestContext directoryOutputTo(MultipleOutputTaskParameters params) throws IOException {
        this.fileOutput = Files.createTempDirectory("SejdaTest").toFile();
        this.fileOutput.deleteOnExit();
        params.setOutput(new DirectoryTaskOutput(fileOutput));
        return this;
    }

    /**
     * Initialize the given params with a directory as {@link FileOrDirectoryTaskOutput}
     *
     * @param params
     * @return
     */
    public TaskTestContext directoryOutputTo(SingleOrMultipleOutputTaskParameters params) throws IOException {
        this.fileOutput = Files.createTempDirectory("SejdaTest").toFile();
        this.fileOutput.deleteOnExit();
        params.setOutput(new FileOrDirectoryTaskOutput(fileOutput));
        return this;
    }

    /**
     * Initialize the given params with a pdf file as {@link FileOrDirectoryTaskOutput}
     *
     * @param params
     * @return
     */
    public TaskTestContext pdfOutputTo(SingleOrMultipleOutputTaskParameters params) throws IOException {
        this.fileOutput = Files.createTempFile("SejdaTest", ".pdf").toFile();
        this.fileOutput.deleteOnExit();
        params.setOutput(new FileOrDirectoryTaskOutput(fileOutput));
        return this;
    }

    /**
     * asserts the creator has been set to the info dictionary
     *
     * @return
     */
    public TaskTestContext assertCreator() {
        requirePDDocument();
        Assertions.assertEquals(Sejda.CREATOR, outputDocument.getDocumentInformation().getCreator());
        return this;
    }

    /**
     * asserts the PDF version of the output is the given one
     *
     * @return
     */
    public TaskTestContext assertVersion(PdfVersion version) {
        requirePDDocument();
        Assertions.assertEquals(version.getVersionString(), outputDocument.getVersion(), "Wrong output PDF version");
        return this;
    }

    /**
     * asserts the output document has that number of pages
     *
     * @return
     */
    public TaskTestContext assertPages(int expected) {
        requirePDDocument();
        Assertions.assertEquals(expected, outputDocument.getNumberOfPages(), "Wrong number of pages");
        return this;
    }

    /**
     * asserts the output document with the given filename exists and has that number of pages. This assert will work only for multiple output task.
     *
     * @return
     * @throws IOException
     * @see this{@link #assertPages(int)}
     */
    public TaskTestContext assertPages(String filename, int expected) throws IOException {
        assertOutputContainsFilenames(filename);
        try (PDDocument doc = PDFParser.parse(SeekableSources.seekableSourceFrom(new File(fileOutput, filename)))) {
            Assertions.assertEquals(expected, doc.getNumberOfPages());
        }
        return this;
    }

    /**
     * @param hasOutline
     */
    public TaskTestContext assertHasOutline(boolean hasOutline) {
        requirePDDocument();
        if (hasOutline) {
            assertNotNull(outputDocument.getDocumentCatalog().getDocumentOutline());
        } else {
            assertNull(outputDocument.getDocumentCatalog().getDocumentOutline());
        }
        return this;
    }

    /**
     * assert that the document has an acroform with some field in it
     *
     * @param hasForms
     */
    public TaskTestContext assertHasAcroforms(boolean hasForms) {
        requirePDDocument();
        if (hasForms) {
            assertNotNull(outputDocument.getDocumentCatalog().getAcroForm());
            assertTrue(outputDocument.getDocumentCatalog().getAcroForm().getFields().size() > 0);
        } else {
            assertNull(outputDocument.getDocumentCatalog().getAcroForm());
        }
        return this;
    }

    /**
     * assert the document outline contains an item with the given title
     *
     * @param title
     * @return
     */
    public TaskTestContext assertOutlineContains(String title) {
        requirePDDocument();
        PDDocumentOutline outline = outputDocument.getDocumentCatalog().getDocumentOutline();
        assertNotNull(outline);
        if (!findOutlineItem(title, outline)) {
            fail("Unable to find outline node with title: " + title);
        }
        return this;
    }

    /**
     * assert the document outline doesn't contain an item with the given title
     *
     * @param title
     * @return
     */
    public TaskTestContext assertOutlineDoesntContain(String title) {
        requirePDDocument();
        PDDocumentOutline outline = outputDocument.getDocumentCatalog().getDocumentOutline();
        if (nonNull(outline)) {
            if (findOutlineItem(title, outline)) {
                fail("Found outline node with title: " + title);
            }
        }
        return this;

    }

    private boolean findOutlineItem(String title, PDOutlineNode node) {
        boolean found = false;
        if (node.hasChildren()) {
            for (PDOutlineItem current : node.children()) {
                found = findOutlineItem(title, current);
                if (found) {
                    return true;
                }
            }
        }
        if (node instanceof PDOutlineItem && title.equals(((PDOutlineItem) node).getTitle())) {
            return true;
        }
        return false;
    }

    /**
     * asserts that a multiple output task has generated the given number of output files, hidden files don't count
     *
     * @return
     * @throws IOException
     */
    public TaskTestContext assertOutputSize(int size) throws IOException {
        if (size == 0) {
            return assertEmptyMultipleOutput();
        }
        requireMultipleOutputs();
        assertEquals(size, Files.list(fileOutput.toPath()).filter(p -> {
            if (IS_OS_WINDOWS) {
                try {
                    return !(Boolean) Files.getAttribute(p, "dos:hidden");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
            if (p.getFileName().toString().startsWith(".")) {
                return false;
            }
            return true;
        }).count(), "An unexpected number of output files has been created");
        return this;
    }

    /**
     * asserts that a multiple output task has generated the given number of output files, including hidden files (in case the task is expected to have some hidden leftover)
     *
     * @param size
     * @return
     */
    public TaskTestContext assertOutputSizeIncludingHidden(int size) {
        if (size == 0) {
            return assertEmptyMultipleOutput();
        }
        requireMultipleOutputs();

        String[] files = fileOutput.list();
        assertEquals(size, files.length,
                "An unexpected number of output files has been created: " + StringUtils.join(files, ","));
        return this;
    }

    /**
     * asserts that a multiple output task has generated no output
     *
     * @return
     */
    public TaskTestContext assertEmptyMultipleOutput() {
        assertNotNull(fileOutput);
        assertTrue(fileOutput.isDirectory(), "Expected an output directory");
        assertEquals(0, fileOutput.listFiles((d, n) -> !n.endsWith(".tmp")).length,
                "Found output files while expecting none");
        return this;
    }

    /**
     * asserts that a multiple output task has generated the given file names
     *
     * @param filenames
     * @return
     */
    public TaskTestContext assertOutputContainsFilenames(String... filenames) {
        requireMultipleOutputs();
        Set<String> outputFiles = Arrays.stream(fileOutput.listFiles()).map(File::getName).collect(Collectors.toSet());
        Arrays.stream(filenames).forEach(f -> assertTrue(outputFiles.contains(f),
                f + " missing but expected. Files were: " + StringUtils.join(outputFiles)));
        return this;
    }

    /**
     * Applies the given consumer to every generated output
     *
     * @param consumer
     * @return
     * @throws IOException
     */
    public TaskTestContext forEachPdfOutput(Consumer<PDDocument> consumer) throws IOException {
        if (nonNull(outputDocument)) {
            requirePDDocument();
            consumer.accept(outputDocument);
        } else if (nonNull(fileOutput) && fileOutput.isDirectory()) {
            requireMultipleOutputs();
            for (File current : fileOutput.listFiles()) {
                try (PDDocument doc = PDFParser.parse(SeekableSources.seekableSourceFrom(current))) {
                    consumer.accept(doc);
                }
            }
        } else {
            fail("No output to apply to");
        }
        return this;
    }

    /**
     * Applies the given consumer to generated single output PDF document
     *
     * @param consumer
     * @return
     * @throws IOException
     */
    public TaskTestContext forPdfOutput(Consumer<PDDocument> consumer) {
        requirePDDocument();
        consumer.accept(outputDocument);
        return this;
    }

    /**
     * Applies the given consumer to generated output PDF document with the given name
     *
     * @param consumer
     * @return
     * @throws IOException
     */
    public TaskTestContext forPdfOutput(String filename, Consumer<PDDocument> consumer) throws IOException {
        requireMultipleOutputs();
        assertTrue(isNotEmpty(filename) && filename.toLowerCase().endsWith(SejdaFileExtensions.PDF_EXTENSION),
                "Not a PDF output");
        try (PDDocument doc = PDFParser.parse(SeekableSources.seekableSourceFrom(new File(fileOutput, filename)))) {
            consumer.accept(doc);
        }
        return this;
    }

    /**
     * Applies the given consumer to every generated output
     *
     * @param consumer
     * @return
     * @throws IOException
     */
    public TaskTestContext forEachRawOutput(Consumer<Path> consumer) throws IOException {
        requireMultipleOutputs();
        Files.list(fileOutput.toPath()).forEach(consumer);
        return this;
    }

    /**
     * Applies the given consumer to a single generated output
     *
     * @param consumer
     * @return
     * @throws IOException
     */
    public TaskTestContext forRawOutput(Consumer<Path> consumer) {
        assertNotNull(fileOutput);
        consumer.accept(fileOutput.toPath());
        return this;
    }

    public TaskTestContext forRawOutput(String filename, Consumer<Path> consumer) {
        requireMultipleOutputs();
        File file = new File(fileOutput, filename);
        assertTrue(file.exists());
        consumer.accept(file.toPath());
        return this;
    }

    private void requireMultipleOutputs() {
        assertNotNull(fileOutput);
        assertTrue(fileOutput.isDirectory(), "Expected an output directory");
        assertTrue(fileOutput.listFiles().length > 0, "No output has been created");
    }

    private void requirePDDocument() {
        Assertions.assertNotNull(outputDocument,
                "No output document, make sure to call TaskTestContext::assertTaskCompleted before any other assert method");
    }

    /**
     * Asserts that the task has completed and generated some output. If a single output task, then the output is paresed and a {@link PDDocument} is returned
     *
     * @return
     * @throws IOException
     */
    public PDDocument assertTaskCompleted() throws IOException {
        return this.assertTaskCompleted(null);
    }

    /**
     * Asserts that the task has completed and generated some output. If the task generated a single output, then the output is parsed and a {@link PDDocument} is returned
     *
     * @param password
     * @return
     * @throws IOException
     */
    public PDDocument assertTaskCompleted(String password) throws IOException {
        // First and foremost, check if the task failed.
        this.assertTaskDidNotFail();

        // look at outputs
        // TODO: ignore leftover tmp buffers while doing this
        if (nonNull(fileOutput)) {
            if (fileOutput.isDirectory()) {
                File[] files = fileOutput.listFiles();
                assertTrue(files.length > 0, "No output has been created");
                if (files.length == 1) {
                    initOutputFromSource(files[0], password);
                }
            } else {
                initOutputFromSource(fileOutput, password);
            }
        } else if (nonNull(streamOutput)) {
            org.sejda.commons.util.IOUtils.close(streamOutput);
            initOutputFromSource(SeekableSources.inMemorySeekableSourceFrom(streamOutput.toByteArray()), password);
        }
        return outputDocument;
    }

    Throwable taskFailureCause = null;
    String failedSource;
    List<String> taskWarnings = new ArrayList<>();

    private TestListenerFactory.TestListenerStart startListener = TestListenerFactory.newStartListener();

    private EventListener<TaskExecutionWarningEvent> warningsListener = new EventListener<TaskExecutionWarningEvent>() {
        @Override
        public void onEvent(TaskExecutionWarningEvent event) {
            taskWarnings.add(event.getWarning());
        }
    };

    private EventListener<TaskExecutionFailedEvent> failureListener = new EventListener<>() {
        @Override
        public void onEvent(TaskExecutionFailedEvent event) {
            taskFailureCause = event.getFailingCause();
            failedSource = event.getNotifiableTaskMetadata().getCurrentSource();
        }
    };

    public void listenForTaskFailure() {
        taskFailureCause = null;
        GlobalNotificationContext.getContext().removeListener(failureListener);
        GlobalNotificationContext.getContext().addListener(failureListener);
    }

    public void listenForTaskStart() {
        GlobalNotificationContext.getContext().removeListener(startListener);
        GlobalNotificationContext.getContext().addListener(startListener);
    }

    public TaskTestContext assertTaskFailed(String message) {
        assertNotNull(taskFailureCause);
        assertThat(taskFailureCause.getMessage(), startsWith(message));
        return this;
    }

    public TaskTestContext assertTaskFailed(Class<? extends Throwable> cause) {
        assertNotNull(taskFailureCause);
        assertThat(taskFailureCause, instanceOf(cause));
        return this;
    }

    public TaskTestContext assertTaskFailed() {
        assertNotNull(taskFailureCause);
        return this;
    }

    public TaskTestContext assertFailedSource(String source) {
        assertEquals(source, failedSource);
        return this;
    }

    public TaskTestContext assertTaskStarted() {
        Assertions.assertTrue(startListener.isStarted());
        return this;
    }

    public TaskTestContext assertTaskDidNotFail() {
        assertNull(taskFailureCause, "Task failed");
        return this;
    }

    @Deprecated
    public void expectTaskWillProduceWarnings() {
        listenForTaskWarnings();
    }

    public void listenForTaskWarnings() {
        taskWarnings = new ArrayList<>();
        GlobalNotificationContext.getContext().removeListener(warningsListener);
        GlobalNotificationContext.getContext().addListener(warningsListener);
    }

    public TaskTestContext assertTaskWarning(String message) {
        assertThat(taskWarnings, hasItem(containsString(message)));
        return this;
    }

    public TaskTestContext assertTaskWarnings(List<String> messages) {
        assertEquals(taskWarnings, messages);
        return this;
    }

    public TaskTestContext assertNoTaskWarnings() {
        assertEquals(taskWarnings.size(), 0, "Expected no warnings but got: " + StringUtils.join(taskWarnings, ","));
        return this;
    }

    private void initOutputFromSource(File source, String password) throws IOException {
        if (source.getName().toLowerCase().endsWith(SejdaFileExtensions.PDF_EXTENSION)) {
            this.outputDocument = PDFParser.parse(SeekableSources.seekableSourceFrom(source), password);
            assertNotNull(outputDocument);
        }
    }

    private void initOutputFromSource(SeekableSource source, String password) throws IOException {
        this.outputDocument = PDFParser.parse(source, password);
        assertNotNull(outputDocument);
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(streamOutput);
        this.streamOutput = null;
        IOUtils.closeQuietly(outputDocument);
        this.outputDocument = null;
        if (nonNull(fileOutput)) {
            if (fileOutput.isDirectory()) {
                Files.walkFileTree(fileOutput.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }

                });
            }
            Files.deleteIfExists(fileOutput.toPath());
        }
        this.fileOutput = null;
    }

    public File getFileOutput() {
        return fileOutput;
    }
}
