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
package org.sejda.core.manipulation.model.task.pdfbox.component;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.sejda.core.manipulation.model.rotation.PageRotation;
import org.sejda.core.manipulation.model.rotation.RotationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.lowagie.text.pdf.PdfDictionary;
//import com.lowagie.text.pdf.PdfName;
//import com.lowagie.text.pdf.PdfNumber;
//import com.lowagie.text.pdf.PdfReader;

/**
 * Handles rotations on a given PDDocument.
 *
 * @author Nero Couvalli
 *
 */
public final class PdfRotationHandler implements OngoingRotation {

    private static final Logger LOG = LoggerFactory.getLogger(PdfRotationHandler.class);
    private static final int DEGREES_360 = 360;

    private PDDocument document;
    private PageRotation rotation;

    private PdfRotationHandler(PageRotation rotation) {
        this.rotation = rotation;
    }

    /**
     * DSL entry point to apply a rotation
     * <p>
     * <code>applyRotation(rotation).to(reader);</code>
     * </p>
     *
     * @param rotation
     * @return the ongoing apply rotation exposing methods to set the document you want to apply the rotation to.
     */
    public static OngoingRotation applyRotation(PageRotation rotation) {
        return new PdfRotationHandler(rotation);
    }

    public void to(PDDocument document) {
        this.document = document;
        executeRotation();
    }

    /**
     * Apply the rotation to the dictionary of the given {@link PdfReader}
     */
    private void executeRotation() {
        RotationType type = rotation.getRotationType();
        LOG.debug("Applying rotation of {} to pages {}", rotation.getRotation().getDegrees(), type);
        if (RotationType.SINGLE_PAGE.equals(type)) {
            apply(rotation.getPageNumber());
        } else {
            for (int j = 1; j <= document.getNumberOfPages(); j++) {
                apply(j);
            }
        }
    }

    /**
     * apply the rotation to the given page if necessary
     *
     * @param pageNmber
     */
    private void apply(int pageNmber) {
        if (rotation.accept(pageNmber)) {
            PDPage page = (PDPage) document.getDocumentCatalog().getAllPages().get(pageNmber-1);
            int rotationDegrees = (rotation.getRotation().getDegrees() + page.findRotation())
                    % DEGREES_360;
            page.setRotation(rotationDegrees);
        }
    }
}