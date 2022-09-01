/*
 * Created on 13 dic 2015
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

import org.apache.commons.io.FileUtils;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.UnpackParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDDocumentNameDictionary;
import org.sejda.sambox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.sejda.sambox.pdmodel.common.PDNameTreeNode;
import org.sejda.sambox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationFileAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.IOUtils.closeQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.model.util.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;

/**
 * SAMBox implementation of a task that unpacks files attached to a collection of input documents.
 * 
 * @author Andrea Vacondio
 *
 */
public class UnpackTask extends BaseTask<UnpackParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(UnpackTask.class);
    private int totalSteps;
    private PDDocumentHandler sourceDocumentHandler = null;
    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    @Override
    public void before(UnpackParameters parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(UnpackParameters parameters) throws TaskException {
        int currentStep = 0;

        for (PdfSource<?> source : parameters.getSourceList()) {
            currentStep++;
            try {
                LOG.debug("Opening {}", source);
                executionContext().notifiableTaskMetadata().setCurrentSource(source);
                sourceDocumentHandler = source.open(documentLoader);

                Map<String, PDComplexFileSpecification> names = new HashMap<>();
                PDEmbeddedFilesNameTreeNode ef = ofNullable(
                        sourceDocumentHandler.getUnderlyingPDDocument().getDocumentCatalog().getNames()).map(
                        PDDocumentNameDictionary::getEmbeddedFiles).orElse(null);
                collectNamesVisitingTree(ef, names);
                Stream.concat(names.values().stream(),
                        sourceDocumentHandler.getPages().stream().flatMap(p -> p.getAnnotations().stream())
                                .filter(a -> a instanceof PDAnnotationFileAttachment)
                                .map(a -> (PDAnnotationFileAttachment) a).map(PDAnnotationFileAttachment::getFile)
                                .filter(f -> f instanceof PDComplexFileSpecification)
                                .map(f -> (PDComplexFileSpecification) f)).forEach(this::unpack);

            } finally {
                closeQuietly(sourceDocumentHandler);
            }

            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        executionContext().notifiableTaskMetadata().clearCurrentSource();
        parameters.getOutput().accept(outputWriter);
        LOG.debug("Attachments unpacked and written to {}", parameters.getOutput());
    }

    private void unpack(PDComplexFileSpecification file) {

        ofNullable(file.getBestEmbeddedFile()).ifPresent(e -> {
            try {
                File tmpFile = createTemporaryBuffer();
                LOG.debug("Created output temporary buffer {}", tmpFile);
                try (InputStream is = e.createInputStream()) {
                    FileUtils.copyInputStreamToFile(is, tmpFile);
                    LOG.debug("Attachment '{}' unpacked to temporary buffer", file.getFilename());
                }
                outputWriter.addOutput(file(tmpFile).name(file.getFilename()));
            } catch (IOException | TaskIOException ioe) {
                LOG.error("Unable to extract file", ioe);
            }
        });
    }

    private void collectNamesVisitingTree(PDNameTreeNode<PDComplexFileSpecification> node,
            Map<String, PDComplexFileSpecification> names) throws TaskIOException {
        try {
            if (nonNull(node)) {
                Map<String, PDComplexFileSpecification> nodeNames = node.getNames();
                if (nodeNames != null) {
                    names.putAll(nodeNames);
                } else {
                    for (PDNameTreeNode<PDComplexFileSpecification> currNode : node.getKids()) {
                        collectNamesVisitingTree(currNode, names);
                    }
                }
            }
        } catch (IOException ioe) {
            throw new TaskIOException(ioe);

        }
    }

    @Override
    public void after() {
        closeQuietly(sourceDocumentHandler);
    }
}
