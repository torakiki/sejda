/*
 * Created on 22 gen 2016
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
package org.sejda.impl.sambox;

import static java.util.Objects.requireNonNull;
import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.io.SeekableSources.inMemorySeekableSourceFrom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DeflaterInputStream;

import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.io.SeekableSource;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.input.PdfURLSource;
import org.sejda.model.parameter.PortfolioParameters;
import org.sejda.model.pdf.viewerpreference.PdfPageMode;
import org.sejda.model.task.BaseTask;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.pdmodel.PDDocumentNameDictionary;
import org.sejda.sambox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.sejda.sambox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.sejda.sambox.util.SpecVersionUtils;
import org.sejda.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAMBox implementation for a task that creates a PDF portfolio containing the set of input PDF documents
 * 
 * @author Andrea Vacondio
 *
 */
public class PortfolioTask extends BaseTask<PortfolioParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(PortfolioTask.class);

    private int totalSteps;
    private SingleOutputWriter outputWriter;
    private PDDocumentHandler destinationDocument;

    @Override
    public void before(PortfolioParameters parameters) {
        totalSteps = parameters.getSourceList().size();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.getExistingOutputPolicy());
    }

    @Override
    public void execute(PortfolioParameters parameters) throws TaskException {
        int currentStep = 0;
        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {} ", tmpFile);

        destinationDocument = new PDDocumentHandler();
        destinationDocument.setCreatorOnPDDocument();
        destinationDocument.setVersionOnPDDocument(parameters.getVersion());
        destinationDocument.getUnderlyingPDDocument().requireMinVersion(SpecVersionUtils.V1_7);

        destinationDocument.setCompress(parameters.isCompress());
        destinationDocument.setPageModeOnDocument(PdfPageMode.USE_ATTACHMENTS);
        destinationDocument.addBlankPage(PDRectangle.A4);

        PDEmbeddedFilesNameTreeNode embeddedFiles = new PDEmbeddedFilesNameTreeNode();
        Map<String, PDComplexFileSpecification> names = new HashMap<>();

        for (PdfSource<?> source : parameters.getSourceList()) {
            stopTaskIfCancelled();
            PDComplexFileSpecification fileSpec = new PDComplexFileSpecification(null);
            fileSpec.setFileUnicode(source.getName());
            fileSpec.setFile(source.getName());

            // TODO set CI
            PDEmbeddedFile embeddedFile = new PDEmbeddedFile(
                    source.open(new PdfSourceOpener<EmbeddedPdfSourceStream>() {

                        @Override
                        public EmbeddedPdfSourceStream open(PdfURLSource source) throws TaskIOException {
                            try {
                                return new EmbeddedPdfSourceStream(source.getSource().openStream());
                            } catch (IOException e) {
                                throw new TaskIOException(e);
                            }
                        }

                        @Override
                        public EmbeddedPdfSourceStream open(PdfFileSource source) throws TaskIOException {
                            EmbeddedPdfSourceStream retVal;
                            try {
                                retVal = new EmbeddedPdfSourceStream(new FileInputStream(source.getSource()));

                                retVal.setEmbeddedInt(COSName.PARAMS.getName(), COSName.SIZE.getName(),
                                        source.getSource().length());
                                return retVal;
                            } catch (FileNotFoundException e) {
                                throw new TaskIOException(e);
                            }
                        }

                        @Override
                        public EmbeddedPdfSourceStream open(PdfStreamSource source) throws TaskIOException {
                            return new EmbeddedPdfSourceStream(source.getSource());
                        }
                    }));
            embeddedFile.setCreationDate(new GregorianCalendar());
            fileSpec.setEmbeddedFileUnicode(embeddedFile);
            fileSpec.setEmbeddedFile(embeddedFile);

            names.put(currentStep + source.getName(), fileSpec);
            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
            LOG.debug("Added embedded file from {}", source);
        }

        embeddedFiles.setNames(names);

        PDDocumentNameDictionary nameDictionary = new PDDocumentNameDictionary(destinationDocument.catalog());
        nameDictionary.setEmbeddedFiles(embeddedFiles);
        destinationDocument.catalog().setNames(nameDictionary);
        destinationDocument.savePDDocument(tmpFile);
        nullSafeCloseQuietly(destinationDocument);

        outputWriter.setOutput(file(tmpFile).name(parameters.getOutputName()));
        parameters.getOutput().accept(outputWriter);
        LOG.debug("Created portfolio with {} files and written to {}", parameters.getSourceList().size(),
                parameters.getOutput());
    }

    @Override
    public void after() {
        nullSafeCloseQuietly(destinationDocument);
    }

    /**
     * A read only {@link COSStream} that reads from the underlying {@link InputStream}, is always compressed and has a length written as indirect object
     * 
     * @author Andrea Vacondio
     *
     */
    private static class EmbeddedPdfSourceStream extends COSStream {

        private InputStream stream;

        EmbeddedPdfSourceStream(InputStream stream) {
            requireNonNull(stream, "input stream cannot be null");
            this.stream = stream;
            setItem(COSName.FILTER, COSName.FLATE_DECODE);
        }

        @Override
        protected InputStream doGetFilteredStream() {
            return new DeflaterInputStream(stream);
        }

        @Override
        public long getFilteredLength() throws IOException {
            throw new IOException("Embedded files filtered length cannot be requested");
        }

        @Override
        public long getUnfilteredLength() throws IOException {
            throw new IOException("Embedded files unfiltered length cannot be requested");
        }

        @Override
        public InputStream getUnfilteredStream() {
            return stream;
        }

        @Override
        public SeekableSource getUnfilteredSource() throws IOException {
            return inMemorySeekableSourceFrom(stream);
        }

        @Override
        public OutputStream createFilteredStream() {
            throw new SejdaRuntimeException("createFilteredStream cannot be called on this stream");
        }

        @Override
        public OutputStream createFilteredStream(COSBase filters) {
            throw new SejdaRuntimeException("createFilteredStream cannot be called on this stream");
        }

        @Override
        public void setFilters(COSBase filters) {
            throw new SejdaRuntimeException("setFilters cannot be called on this stream");
        }

        @Override
        public void addCompression() {
            // do nothing, it's already supposed to be compressed
        }

        @Override
        public boolean encryptable() {
            return true;
        }

        @Override
        public void encryptable(boolean encryptable) {
            // do nothing, it can be encrypted
        }

        @Override
        public OutputStream createUnfilteredStream() {
            throw new SejdaRuntimeException("createUnfilteredStream cannot be called on this stream");
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean indirectLength() {
            return true;
        }

        @Override
        public void indirectLength(boolean indirectLength) {
            // do nothing, it's always written as indirect
        }

        @Override
        public void close() {
            IOUtils.closeQuietly(stream);
        }
    }
}
