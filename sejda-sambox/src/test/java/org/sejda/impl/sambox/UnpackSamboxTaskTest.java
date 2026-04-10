/*
 * Created on 17 dic 2015
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
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
import org.junit.jupiter.api.io.TempDir;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.UnpackParameters;
import org.sejda.model.task.Task;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDDocumentNameDictionary;
import org.sejda.sambox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.sejda.sambox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.sejda.tests.tasks.BaseTaskTest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.sejda.tests.TestUtils.customInput;

/**
 * @author Andrea Vacondio
 */
public class UnpackSamboxTaskTest extends BaseTaskTest<UnpackParameters> {

    private UnpackParameters parameters;
    @TempDir
    public Path folder;

    @Test
    public void unpackAnnotations() throws IOException {
        executeTest("pdf/attachments_as_annots.pdf");
    }

    @Test
    public void unpackNamedTree() throws IOException {
        executeTest("pdf/attachments_as_named_tree.pdf");
    }

    public void executeTest(String filename) throws IOException {
        File out = Files.createTempDirectory(folder, "sejda").toFile();
        parameters = new UnpackParameters(new DirectoryTaskOutput(out));
        parameters.addSource(customInput(filename));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        execute(parameters);
        assertEquals(1, out.list().length);
    }

    @Test
    public void pathTraversalFilenameIsSanitized() throws IOException {
        var filename = "../../evil.txt";
        var tmpPdf = createPdfWithEmbeddedFile(filename);
        var out = Files.createTempDirectory(folder, "sejda").toFile();
        parameters = new UnpackParameters(new DirectoryTaskOutput(out));
        parameters.addSource(PdfFileSource.newInstanceNoPassword(tmpPdf));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        execute(parameters);

        var outputFiles = out.listFiles();
        assertNotNull(outputFiles);
        assertEquals(1, outputFiles.length);
        assertTrue(outputFiles[0].getCanonicalPath().startsWith(out.getCanonicalPath()));
        assertFalse(outputFiles[0].getName().contains("/"));
        assertFalse(outputFiles[0].getName().contains("\\"));
    }

    @Test
    public void allSeparatorsFilenameStripsToBlankAndFallsBackToAttachment() throws IOException {
        var filename = "///\\\\";
        var tmpPdf = createPdfWithEmbeddedFile(filename);
        var out = Files.createTempDirectory(folder, "sejda").toFile();
        parameters = new UnpackParameters(new DirectoryTaskOutput(out));
        parameters.addSource(PdfFileSource.newInstanceNoPassword(tmpPdf));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        execute(parameters);

        var outputFiles = out.listFiles();
        assertNotNull(outputFiles);
        assertEquals(1, outputFiles.length);
        assertEquals("attachment", outputFiles[0].getName());
    }

    @Test
    public void allDotsFilenameFallsBackToAttachment() throws IOException {
        var filename = "...";
        var tmpPdf = createPdfWithEmbeddedFile(filename);
        var out = Files.createTempDirectory(folder, "sejda").toFile();
        parameters = new UnpackParameters(new DirectoryTaskOutput(out));
        parameters.addSource(PdfFileSource.newInstanceNoPassword(tmpPdf));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        execute(parameters);

        var outputFiles = out.listFiles();
        assertNotNull(outputFiles);
        assertEquals(1, outputFiles.length);
        assertEquals("attachment", outputFiles[0].getName());
    }

    private File createPdfWithEmbeddedFile(String filename) throws IOException {
        var fileSpec = new PDComplexFileSpecification(new COSDictionary());
        fileSpec.setFile(filename);
        fileSpec.setFileUnicode(filename);
        var embeddedFile = new PDEmbeddedFile(new ByteArrayInputStream("content".getBytes()));
        fileSpec.setEmbeddedFile(embeddedFile);
        fileSpec.setEmbeddedFileUnicode(embeddedFile);
        var tmpPdf = Files.createTempFile(folder, "test", ".pdf").toFile();
        try (var doc = new PDDocument()) {
            doc.addPage(new PDPage());
            var efTree = new PDEmbeddedFilesNameTreeNode();
            efTree.setNames(Map.of(filename, fileSpec));
            var names = new PDDocumentNameDictionary(doc.getDocumentCatalog());
            names.setEmbeddedFiles(efTree);
            doc.getDocumentCatalog().setNames(names);
            doc.writeTo(tmpPdf);
        }
        return tmpPdf;
    }

    @Override
    public Task<UnpackParameters> getTask() {
        return new UnpackTask();
    }

}
