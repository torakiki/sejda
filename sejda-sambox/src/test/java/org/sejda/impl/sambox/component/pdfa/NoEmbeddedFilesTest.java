package org.sejda.impl.sambox.component.pdfa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.parameter.ConvertToPDFAParameters;
import org.sejda.model.pdfa.ConformanceLevel;
import org.sejda.model.pdfa.InvalidElementPolicy;
import org.sejda.model.task.NotifiableTaskMetadata;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDDocumentNameDictionary;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
 * Created on 17/06/24
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
class NoEmbeddedFilesTest {

    private PDDocument document;

    @BeforeEach
    public void setUp() {
        document = new PDDocument();
    }

    @Test
    void shouldRemoveEmbeddedFilesKey() throws TaskExecutionException {
        var params = new ConvertToPDFAParameters(InvalidElementPolicy.FIX, ConformanceLevel.PDFA_1B);
        var victim = new NoEmbeddedFiles(new ConversionContext(params, NotifiableTaskMetadata.NULL));
        var names = new COSDictionary();
        var embeddedFiles = new COSDictionary();
        names.setItem(COSName.EMBEDDED_FILES, embeddedFiles);
        document.getDocumentCatalog().setNames(new PDDocumentNameDictionary(names));

        victim.accept(document);
        assertFalse(names.containsKey(COSName.EMBEDDED_FILES));
    }

    @Test
    void failPolicy() throws TaskExecutionException {

        var params = new ConvertToPDFAParameters(InvalidElementPolicy.FAIL, ConformanceLevel.PDFA_1B);
        var victim = new NoEmbeddedFiles(new ConversionContext(params, NotifiableTaskMetadata.NULL));
        var names = new COSDictionary();
        var embeddedFiles = new COSDictionary();
        names.setItem(COSName.EMBEDDED_FILES, embeddedFiles);
        document.getDocumentCatalog().setNames(new PDDocumentNameDictionary(names));

        var e = assertThrows(TaskExecutionException.class, () -> victim.accept(document));
        assertThat(e.getMessage(), stringContainsInOrder("shall not contain the EmbeddedFiles"));
    }

    @Test
    void nullNamesItemIsHandled() throws TaskExecutionException {
        var params = new ConvertToPDFAParameters(InvalidElementPolicy.FIX, ConformanceLevel.PDFA_1B);
        var victim = new NoEmbeddedFiles(new ConversionContext(params, NotifiableTaskMetadata.NULL));

        victim.accept(document);
    }

    @Test
    void nullEmbeddedFilesItemIsHandled() throws TaskExecutionException {

        var params = new ConvertToPDFAParameters(InvalidElementPolicy.FIX, ConformanceLevel.PDFA_1B);
        var victim = new NoEmbeddedFiles(new ConversionContext(params, NotifiableTaskMetadata.NULL));
        var names = new COSDictionary();
        document.getDocumentCatalog().setNames(new PDDocumentNameDictionary(names));

        victim.accept(document);
    }

}