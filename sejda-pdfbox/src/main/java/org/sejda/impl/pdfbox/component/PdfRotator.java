/*
 * Created on Jul 2, 2011
 * Copyright 2010 by Nero Couvalli (angelthepunisher@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sejda.impl.pdfbox.component;

import static org.sejda.model.rotation.Rotation.getRotation;

import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.sejda.model.rotation.Rotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles rotations on a given PDDocument.
 * 
 * @author Nero Couvalli
 * 
 */
public final class PdfRotator implements OngoingRotation {

    private static final Logger LOG = LoggerFactory.getLogger(PdfRotator.class);

    private PDDocument document;
    private Rotation rotation;
    private Set<Integer> pages;

    private PdfRotator(Rotation rotation, Set<Integer> pages) {
        this.rotation = rotation;
        this.pages = pages;
    }

    /**
     * DSL entry point to apply a rotation to a set of pages
     * <p>
     * <code>applyRotation(rotation, pages).to(document);</code>
     * </p>
     * 
     * @param rotation
     * @param pages
     * @return the ongoing apply rotation exposing methods to set the document you want to apply the rotation to.
     */
    public static OngoingRotation applyRotation(Rotation rotation, Set<Integer> pages) {
        return new PdfRotator(rotation, pages);
    }

    public void to(PDDocument document) {
        this.document = document;
        executeRotation();
    }

    /**
     * Apply the rotation to the given {@link PDDocument}
     */
    private void executeRotation() {
        LOG.debug("Applying rotation of {} degrees to {} pages", rotation.getDegrees(), pages.size());
        for (int p : pages) {
            apply(p);
        }
    }

    /**
     * apply the rotation to the given page if necessary
     * 
     * @param pageNmber
     */
    private void apply(int pageNmber) {
        if (pages.contains(pageNmber)) {
            PDPage page = (PDPage) document.getDocumentCatalog().getAllPages().get(pageNmber - 1);
            page.setRotation(rotation.addRotation(getRotation(page.findRotation())).getDegrees());
        }
    }
}