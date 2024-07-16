/*
 * Created on 02/07/24
 * Copyright 2024 Sober Lemur S.r.l. and Sejda BV
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
package org.sejda.impl.sambox.component.pdfa;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.xml.DomXmpParser;
import org.apache.xmpbox.xml.XmpParsingException;
import org.apache.xmpbox.xml.XmpSerializer;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.common.PDMetadata;

import javax.xml.transform.TransformerException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.xmpbox.schema.PDFAIdentificationSchema.CONFORMANCE;

/**
 * Rule 6.7.3 of ISO 19005-1: A document information dictionary may appear within a conforming file.
 * If it does appear, then all of its entries that have analogous properties in predefined XMP schemas, as defined
 * by Table 1, shall also be embedded in the file in XMP form with equivalent values.
 * <p>
 * Rule 6.7.11 of ISO 19005-1: The PDF/A version and conformance level of a file shall be specified using the
 * PDF/A Identification extension schema defined in this subclause
 *
 * @author Andrea Vacondio
 */
public class XMPMetadataDocumentRule extends BaseRule<PDDocument, TaskException> {

    public XMPMetadataDocumentRule(ConversionContext conversionContext) {
        super(conversionContext);
    }

    @Override
    public void accept(PDDocument document) throws TaskExecutionException {
        var metadata = getOrCreateXmpMetadata(document);
        PDFAIdentificationSchema pdfaIdSchema = ofNullable(metadata.getPDFAIdentificationSchema()).orElseGet(
                metadata::createAndAddPDFAIdentificationSchema);
        pdfaIdSchema.setPart(1);
        pdfaIdSchema.addProperty(pdfaIdSchema.createTextType(CONFORMANCE, "B"));
        var metadataStream = new PDMetadata();
        try (var metadataOutputStream = new BufferedOutputStream(
                metadataStream.getCOSObject().createUnfilteredStream())) {
            new XmpSerializer().serialize(metadata, metadataOutputStream, true);
            document.getDocumentCatalog().setMetadata(metadataStream);
        } catch (IOException | TransformerException e) {
            throw new TaskExecutionException("Unable to set document level metadata", e);
        }
    }

    private XMPMetadata getOrCreateXmpMetadata(PDDocument document) throws TaskExecutionException {
        var metadata = document.getDocumentCatalog().getMetadata();
        if (nonNull(metadata)) {
            try {
                var parser = new DomXmpParser();
                parser.setStrictParsing(false);
                return parser.parse(new BufferedInputStream(metadata.createInputStream()));
            } catch (XmpParsingException | IOException e) {
                conversionContext().maybeFailOnInvalidElement(
                        () -> new TaskExecutionException("Error parsing document level metadata", e));
            } finally {
                metadata.getCOSObject().unDecode();
            }
        }
        return XMPMetadata.createXMPMetadata();
    }
}
