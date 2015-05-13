/*
 * Created on 21/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.icepdf.component;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.PDimension;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ICEpdf component providing a conversion method for a {@link Document} page to a {@link BufferedImage}. current thread.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PdfToBufferedImageProvider {

    private static final Logger LOG = LoggerFactory.getLogger(PdfToBufferedImageProvider.class);

    private PdfToBufferedImageProvider() {
        // hide
    }

    /**
     * Converts the given {@link Document} page to a {@link BufferedImage}
     * 
     * @param document
     * @param page
     * @param parameters
     * @return the corresponding {@link BufferedImage}
     */
    public static BufferedImage toBufferedImage(Document document, int page, AbstractPdfToImageParameters parameters) {
        try {
            Page currentPage = document.getPageTree().getPage(page);
            currentPage.init();
            PDimension pageDimensions = currentPage.getSize(0, parameters.getUserZoom());
            BufferedImage currentImage = parameters.getOutputImageColorType().createBufferedImage(
                    (int) pageDimensions.getWidth(), (int) pageDimensions.getHeight());
            Graphics2D g = currentImage.createGraphics();
            currentPage.paint(g, GraphicsRenderingHints.PRINT, Page.BOUNDARY_CROPBOX, 0, parameters.getUserZoom());
            g.dispose();
            return currentImage;
        } catch (NullPointerException ex) {
            // works around an ICEPdf bug: if one page fails to convert, don't fail the complete task
            LOG.warn("Failed to convert page to image", ex);
            return null;
        }
    }
}
