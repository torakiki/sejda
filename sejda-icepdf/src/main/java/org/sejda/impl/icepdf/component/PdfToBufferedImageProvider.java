/*
 * Created on 21/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
