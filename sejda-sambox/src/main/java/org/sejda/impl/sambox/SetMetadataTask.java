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

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SetMetadataParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDDocumentCatalog;
import org.sejda.sambox.pdmodel.PDDocumentInformation;
import org.sejda.sambox.pdmodel.common.PDMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TimeZone;

import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.IOUtils.closeQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.model.util.IOUtils.createTemporaryBuffer;

/**
 * SAMBox implementation of a task setting metadata on an input {@link PdfSource}.
 * 
 * @author Eduard Weissmann
 * 
 */
public class SetMetadataTask extends BaseTask<SetMetadataParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetMetadataTask.class);

    private PDDocumentHandler documentHandler = null;
    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    @Override
    public void before(SetMetadataParameters parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(SetMetadataParameters parameters) throws TaskException {
        int totalSteps = parameters.getSourceList().size();

        for (int sourceIndex = 0; sourceIndex < parameters.getSourceList().size(); sourceIndex++) {
            PdfSource<?> source = parameters.getSourceList().get(sourceIndex);
            int fileNumber = executionContext().incrementAndGetOutputDocumentsCounter();
            
            try {
                LOG.debug("Opening {}", source);
                executionContext().notifiableTaskMetadata().setCurrentSource(source);

                documentHandler = source.open(documentLoader);
                documentHandler.setUpdateProducerModifiedDate(parameters.isUpdateProducerModifiedDate());
                if (parameters.isUpdateProducerModifiedDate()) {
                    documentHandler.setCreatorOnPDDocument();
                }

                File tmpFile = createTemporaryBuffer(parameters.getOutput());

                PDDocument doc = documentHandler.getUnderlyingPDDocument();
                doc.setOnBeforeWriteAction(() -> {
                    LOG.debug("Setting metadata on temporary document.");

                    PDDocument doc1 = documentHandler.getUnderlyingPDDocument();
                    PDDocumentCatalog catalog = doc1.getDocumentCatalog();

                    if (parameters.isRemoveAllMetadata()) {
                        doc1.setDocumentInformation(new PDDocumentInformation());
                        catalog.setMetadata(null);

                        return;
                    }

                    PDDocumentInformation actualMeta = doc1.getDocumentInformation();
                    for (Entry<String, String> meta : parameters.getMetadata().entrySet()) {
                        LOG.trace("'{}' -> '{}'", meta.getKey(), meta.getValue());
                        actualMeta.setCustomMetadataValue(meta.getKey(), meta.getValue());
                    }

                    for (String keyToRemove : parameters.getToRemove()) {
                        LOG.trace("Removing '{}'", keyToRemove);
                        actualMeta.removeMetadataField(keyToRemove);
                    }

                    if (catalog.getMetadata() != null) {
                        LOG.debug("Document has XMP metadata stream");
                        
                        try {
                            updateXmpMetadata(catalog, actualMeta);
                        } catch (RuntimeException ex) {
                            if(exceptionStackContains("Namespace for prefix 'xmp' has not been declared", ex)) {
                                // try to fix the metadata and retry
                                fixMissingXmpNamespace(catalog);
                                updateXmpMetadata(catalog, actualMeta);
                            } else {
                                throw ex;
                            }
                        }
                    }
                });

                documentHandler.setVersionOnPDDocument(parameters.getVersion());
                documentHandler.setCompress(parameters.isCompress());
                documentHandler.savePDDocument(tmpFile, parameters.getOutput().getEncryptionAtRestPolicy());

                String outName = ofNullable(parameters.getSpecificResultFilename(fileNumber)).orElseGet(
                        () -> nameGenerator(parameters.getOutputPrefix()).generate(
                                nameRequest().originalName(source.getName()).fileNumber(fileNumber)));

                outputWriter.addOutput(file(tmpFile).name(outName));

            } finally {
                closeQuietly(documentHandler);
            }

            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(fileNumber).outOf(totalSteps);
        }

        executionContext().notifiableTaskMetadata().clearCurrentSource();
        parameters.getOutput().accept(outputWriter);
        LOG.debug("Metadata set and written to {}", parameters.getOutput());

    }
    
    private XPathFactory newXPathFactory() {
        try {
            XPathFactory f = XPathFactory.newInstance();
            f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            return f;
        } catch (XPathFactoryConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void updateDateNode(String tagName, Document document, Calendar calendar) throws XPathExpressionException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        String value = "";
        if(calendar != null) {
            value = dateFormat.format(calendar.getTime());
        }
        updateTextNode(tagName, document, value);
    }
    
    private void updateTextNode(String tagName, Document document, String value) throws XPathExpressionException {
        XPath xPath = newXPathFactory().newXPath();
        Node node = (Node) xPath.compile("//*[name()='" + tagName + "']").evaluate(document, XPathConstants.NODE);
        if(value == null) {
            value = "";
        }

        if(node != null) {
            node.setTextContent(value);
        }
    }

    private boolean exceptionStackContains(String msg, Throwable ex) {
        if (ex != null && ex.getMessage() != null && ex.getMessage().contains(msg)){
            return true;
        }

        if (ex != null && ex.getCause() != null) {
            return exceptionStackContains(msg, ex.getCause());
        }

        return false;
    }

    private void deleteAttr(String path, String attrName, Document document) throws XPathExpressionException {
        XPath xPath = newXPathFactory().newXPath();
        Node node = (Node) xPath.compile(path).evaluate(document, XPathConstants.NODE);
        if(node != null && node.getAttributes().getNamedItem(attrName) != null) {
            node.getAttributes().removeNamedItem(attrName);
        }
    }
    
    private void fixMissingXmpNamespace(PDDocumentCatalog catalog) throws IOException {
        try(InputStream is = catalog.getMetadata().createInputStream()) {
            String metadataAsString = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            if (!metadataAsString.contains("xmlns:xmp=\"http://ns.adobe.com/xap/1.0/\"")) {
                LOG.warn("Metadata seems to be missing xmlns:xmp namespace definition, adding it");
                metadataAsString = metadataAsString.replaceAll("<rdf:Description", "<rdf:Description xmlns:xmp=\"http://ns.adobe.com/xap/1.0/\"");

                catalog.setMetadata(new PDMetadata(new ByteArrayInputStream(metadataAsString.getBytes(StandardCharsets.UTF_8))));
            } 
        }
    }
    
    private void updateXmpMetadata(PDDocumentCatalog catalog, PDDocumentInformation metadata) {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            f.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            //f.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            f.setFeature("http://xml.org/sax/features/external-general-entities", false);
            f.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            f.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            f.setXIncludeAware(false);
            f.setExpandEntityReferences(false);

            DocumentBuilder b = f.newDocumentBuilder();
            Document document = b.parse(catalog.getMetadata().createInputStream());

            // handle scenario where these are attributes on the rdf:Description element, instead of nodes inside it
            deleteAttr("//*[@CreateDate]", "xmp:CreateDate", document);
            deleteAttr("//*[@ModifyDate]", "xmp:ModifyDate", document);

            deleteAttr("//*[@Producer]", "pdf:Producer", document);
            deleteAttr("//*[@CreatorTool]", "xmp:CreatorTool", document);
            deleteAttr("//*[@Keywords]", "pdf:Keywords", document);

            deleteAttr("//*[@MetadataDate]", "xmp:MetadataDate", document);
            
            // update metadata
            updateDateNode("xmp:CreateDate", document, metadata.getCreationDate());
            updateDateNode("xmp:ModifyDate", document, metadata.getModificationDate());

            updateTextNode("pdf:Producer", document, metadata.getProducer());
            updateTextNode("xmp:CreatorTool", document, metadata.getCreator());
            updateTextNode("pdf:Keywords", document, metadata.getKeywords());

            // TODO: update title, description

            Calendar nowCalendar = Calendar.getInstance();
            nowCalendar.setTime(new Date());
            updateDateNode("xmp:MetadataDate", document, nowCalendar);

            // write the DOM object to the file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            transformerFactory.setURIResolver(new NoopURIResolver());

            Transformer transformer = transformerFactory.newTransformer();
            StringWriter writer = new StringWriter();
            DOMSource domSource = new DOMSource(document);

            StreamResult streamResult = new StreamResult(writer);
            transformer.transform(domSource, streamResult);

            String updatedXml = writer.getBuffer().toString();
            catalog.setMetadata(new PDMetadata(new ByteArrayInputStream(updatedXml.getBytes(StandardCharsets.UTF_8))));
            
        } catch (SAXParseException ex) {
            LOG.warn("Failed to parse XMP metadata, skipping update", ex);
            notifyEvent(executionContext().notifiableTaskMetadata()).taskWarning("Some metadata elements could not be updated");
        } catch (Exception ex) {
            throw new RuntimeException("Failed to update XMP metadata", ex);    
        }
    }

    @Override
    public void after() {
        closeQuietly(documentHandler);
    }
    
    private static class NoopURIResolver implements URIResolver {
        @Override
        public Source resolve(String href, String base) throws TransformerException {
            return null;
        }
    }

}
