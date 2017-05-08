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

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream.readOnlyEmbeddedFile;

import java.io.File;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.sambox.component.AttachmentsSummaryCreator;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.Source;
import org.sejda.model.parameter.AttachmentsCollectionParameters;
import org.sejda.model.pdf.viewerpreference.PdfPageMode;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSInteger;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocumentNameDictionary;
import org.sejda.sambox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.sejda.sambox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.sejda.sambox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationFileAttachment;
import org.sejda.sambox.util.SpecVersionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAMBox implementation for a task that creates a collection of attachments from a list of PDF documents. See Chap 12.3.5 of PDF spec 32000-1:2008
 * 
 * @author Andrea Vacondio
 *
 */
public class AttachmentsCollectionTask extends BaseTask<AttachmentsCollectionParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(AttachmentsCollectionTask.class);
    private static final COSName COLLECTION_ITEM_ORDER_FIELD = COSName.getPDFName("Sejda-Order");

    private int totalSteps;
    private SingleOutputWriter outputWriter;
    private PDDocumentHandler destinationDocument;
    private AttachmentsSummaryCreator tocCreator;

    @Override
    public void before(AttachmentsCollectionParameters parameters, TaskExecutionContext executionContext)
            throws TaskException {
        super.before(parameters, executionContext);
        totalSteps = parameters.getSourceList().size();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(AttachmentsCollectionParameters parameters) throws TaskException {
        int currentStep = 0;
        File tmpFile = createTemporaryBuffer(parameters.getOutput());
        outputWriter.taskOutput(tmpFile);
        LOG.debug("Temporary output set to {}", tmpFile);

        destinationDocument = new PDDocumentHandler();
        destinationDocument.setCreatorOnPDDocument();
        destinationDocument.setVersionOnPDDocument(parameters.getVersion());
        destinationDocument.getUnderlyingPDDocument().requireMinVersion(SpecVersionUtils.V1_7);

        destinationDocument.setCompress(parameters.isCompress());
        destinationDocument.setPageModeOnDocument(PdfPageMode.USE_ATTACHMENTS);
        this.tocCreator = new AttachmentsSummaryCreator(this.destinationDocument.getUnderlyingPDDocument());

        PDEmbeddedFilesNameTreeNode embeddedFiles = new PDEmbeddedFilesNameTreeNode();
        Map<String, PDComplexFileSpecification> names = new HashMap<>();
        COSDictionary collection = new COSDictionary();
        collection.setName(COSName.getPDFName("View"), parameters.getInitialView().value);
        collection.setItem(COSName.TYPE, COSName.getPDFName("Collection"));
        collection.setItem(COSName.getPDFName("Sort"), createSortDictionary());
        LOG.trace("Added sort dictionary");
        collection.setItem(COSName.getPDFName("Schema"), createSchemaDictionary());
        LOG.trace("Added schema dictionary");
        for (Source<?> source : parameters.getSourceList()) {
            executionContext().assertTaskNotCancelled();
            PDComplexFileSpecification fileSpec = new PDComplexFileSpecification(null);
            fileSpec.setFileUnicode(source.getName());
            fileSpec.setFile(source.getName());
            COSDictionary collectionItem = new COSDictionary();
            collectionItem.setInt(COLLECTION_ITEM_ORDER_FIELD, currentStep);
            fileSpec.setCollectionItem(collectionItem);
            PDEmbeddedFile embeddedFile = embeddedFileFromSource(source);
            fileSpec.setEmbeddedFileUnicode(embeddedFile);
            fileSpec.setEmbeddedFile(embeddedFile);
            names.put(currentStep + source.getName(), fileSpec);
            collection.putIfAbsent(COSName.D, currentStep + source.getName());

            tocCreator.appendItem(FilenameUtils.getName(source.getName()), attachmentAnnotation(fileSpec));

            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
            LOG.debug("Added embedded file from {}", source);
        }

        embeddedFiles.setNames(names);

        PDDocumentNameDictionary nameDictionary = new PDDocumentNameDictionary(destinationDocument.catalog());
        nameDictionary.setEmbeddedFiles(embeddedFiles);
        destinationDocument.catalog().setNames(nameDictionary);
        destinationDocument.catalog().getCOSObject().setItem(COSName.getPDFName("Collection"), collection);
        LOG.debug("Adding generated ToC");
        tocCreator.addToC();
        destinationDocument.savePDDocument(tmpFile);
        nullSafeCloseQuietly(destinationDocument);

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Created portfolio with {} files and written to {}", parameters.getSourceList().size(),
                parameters.getOutput());
    }

    private COSDictionary createSortDictionary() {
        COSDictionary sortDictionary = new COSDictionary();
        sortDictionary.setItem(COSName.S, COLLECTION_ITEM_ORDER_FIELD);
        return sortDictionary;
    }

    private COSDictionary createSchemaDictionary() {
        COSDictionary schemaDictionary = new COSDictionary();
        COSDictionary fileDateFieldDictionary = new COSDictionary();
        fileDateFieldDictionary.setItem(COSName.SUBTYPE, COSName.F);
        fileDateFieldDictionary.setString(COSName.N, "File");
        fileDateFieldDictionary.setInt(COSName.O, 0);
        schemaDictionary.setItem(COSName.F, fileDateFieldDictionary);
        COSDictionary creationDateFieldDictionary = new COSDictionary();
        creationDateFieldDictionary.setItem(COSName.SUBTYPE, COSName.CREATION_DATE);
        creationDateFieldDictionary.setString(COSName.N, "Created");
        creationDateFieldDictionary.setInt(COSName.O, 1);
        schemaDictionary.setItem(COSName.CREATION_DATE, creationDateFieldDictionary);
        COSDictionary modDateFieldDictionary = new COSDictionary();
        modDateFieldDictionary.setItem(COSName.SUBTYPE, COSName.MOD_DATE);
        modDateFieldDictionary.setString(COSName.N, "Modified");
        modDateFieldDictionary.setInt(COSName.O, 2);
        schemaDictionary.setItem(COSName.MOD_DATE, modDateFieldDictionary);
        COSDictionary sizeDateFieldDictionary = new COSDictionary();
        sizeDateFieldDictionary.setItem(COSName.SUBTYPE, COSName.SIZE);
        sizeDateFieldDictionary.setString(COSName.N, "Size");
        sizeDateFieldDictionary.setInt(COSName.O, 3);
        schemaDictionary.setItem(COSName.SIZE, sizeDateFieldDictionary);
        COSDictionary sortFieldDictionary = new COSDictionary();
        sortFieldDictionary.setItem(COSName.SUBTYPE, COSName.N);
        sortFieldDictionary.setString(COSName.N, "Order");
        sortFieldDictionary.setInt(COSName.O, 4);
        schemaDictionary.setItem(COLLECTION_ITEM_ORDER_FIELD, sortFieldDictionary);
        return schemaDictionary;
    }

    private PDEmbeddedFile embeddedFileFromSource(Source<?> source) throws TaskIOException {
        PDEmbeddedFile embeddedFile = new PDEmbeddedFile(readOnlyEmbeddedFile(source));
        embeddedFile.setCreationDate(new GregorianCalendar());
        // TODO find type
        // embeddedFile.setSubtype("application/pdf");
        return embeddedFile;
    }

    private PDAnnotationFileAttachment attachmentAnnotation(PDComplexFileSpecification fileSpec) {
        PDAnnotationFileAttachment attachmentAnnot = new PDAnnotationFileAttachment();
        attachmentAnnot.setFile(fileSpec);
        attachmentAnnot.setBorder(new COSArray(COSInteger.ZERO, COSInteger.ZERO, COSInteger.ZERO));
        return attachmentAnnot;
    }

    @Override
    public void after() {
        nullSafeCloseQuietly(destinationDocument);
    }

}
