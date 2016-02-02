/*
 * Created on 02 feb 2016
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
package org.sejda.impl.sambox.component.optimizaton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.sejda.io.SeekableSources;
import org.sejda.model.optimization.Optimization;
import org.sejda.model.parameter.OptimizeParameters;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSInteger;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.graphics.image.PDImageXObject;

/**
 * @author Andrea Vacondio
 *
 */
public class ImagesOptimizerTest {
    private OptimizeParameters params;
    private PDDocument document;

    @Before
    public void setUp() throws IOException {
        params = new OptimizeParameters();
        document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/draw_w_transparency.pdf")));
    }

    @Test
    public void metadataAreRemoved() throws Exception {
        params.addOptimization(Optimization.DISCARD_METADATA);
        PDPage page = document.getPage(0);
        PDImageXObject image = (PDImageXObject) page.getResources().getXObject(COSName.getPDFName("x5"));
        image.getCOSStream().setItem(COSName.METADATA, COSInteger.ZERO);
        PDFormXObject form = (PDFormXObject) page.getResources().getXObject(COSName.getPDFName("x7"));
        form.getCOSStream().setItem(COSName.METADATA, COSInteger.ONE);
        PDFormXObject nestedForm = (PDFormXObject) form.getResources().getXObject(COSName.getPDFName("x10"));
        nestedForm.getCOSStream().setItem(COSName.METADATA, COSInteger.TWO);
        assertNotNull(image.getCOSStream().getItem(COSName.METADATA));
        assertNotNull(form.getCOSStream().getItem(COSName.METADATA));
        assertNotNull(nestedForm.getCOSStream().getItem(COSName.METADATA));
        new ImagesOptimizer(params).accept(page);
        assertNull(image.getCOSStream().getItem(COSName.METADATA));
        assertNull(form.getCOSStream().getItem(COSName.METADATA));
        assertNull(nestedForm.getCOSStream().getItem(COSName.METADATA));
    }

    @Test
    public void alternateAreRemoved() throws Exception {
        params.addOptimization(Optimization.DISCARD_ALTERNATE_IMAGES);
        PDPage page = document.getPage(0);
        PDImageXObject image = (PDImageXObject) page.getResources().getXObject(COSName.getPDFName("x5"));
        image.getCOSStream().setItem(COSName.getPDFName("Alternates"), COSInteger.ZERO);
        assertNotNull(image.getCOSStream().getItem(COSName.getPDFName("Alternates")));
        new ImagesOptimizer(params).accept(page);
        assertNull(image.getCOSStream().getItem(COSName.getPDFName("Alternates")));
    }

    @Test
    public void pieceInfoAreRemoved() throws Exception {
        params.addOptimization(Optimization.DISCARD_PIECE_INFO);
        PDPage page = document.getPage(0);
        PDFormXObject form = (PDFormXObject) page.getResources().getXObject(COSName.getPDFName("x7"));
        form.getCOSStream().setItem(COSName.getPDFName("PieceInfo"), COSInteger.ONE);
        PDFormXObject nestedForm = (PDFormXObject) form.getResources().getXObject(COSName.getPDFName("x10"));
        nestedForm.getCOSStream().setItem(COSName.getPDFName("PieceInfo"), COSInteger.TWO);
        assertNotNull(form.getCOSStream().getItem(COSName.getPDFName("PieceInfo")));
        assertNotNull(nestedForm.getCOSStream().getItem(COSName.getPDFName("PieceInfo")));
        new ImagesOptimizer(params).accept(page);
        assertNull(form.getCOSStream().getItem(COSName.getPDFName("PieceInfo")));
        assertNull(nestedForm.getCOSStream().getItem(COSName.getPDFName("PieceInfo")));
    }

    @Test
    public void optimizeReuseImages() throws Exception {
        params.addOptimization(Optimization.COMPRESS_IMAGES);
        params.setImageQuality(0.8f);
        params.setImageDpi(72);
        params.setImageMaxWidthOrHeight(1280);
        document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/test_optimize_repeated_images.pdf")));
        ImagesOptimizer optimizer = new ImagesOptimizer(params);
        COSBase image = getImage(document.getPage(0), "X0");
        assertNotEquals(image, getImage(document.getPage(1), "X0"));
        assertNotEquals(image, getImage(document.getPage(2), "X0"));
        assertNotEquals(image, getImage(document.getPage(3), "X0"));
        document.getPages().forEach(optimizer::accept);
        COSBase compressed = getImage(document.getPage(0), "X0");
        assertEquals(compressed, getImage(document.getPage(0), "X0"));
        assertEquals(compressed, getImage(document.getPage(1), "X0"));
        assertEquals(compressed, getImage(document.getPage(2), "X0"));
        assertEquals(compressed, getImage(document.getPage(3), "X0"));

    }

    private COSBase getImage(PDPage page, String name) {
        return ((COSDictionary) page.getResources().getCOSObject().getDictionaryObject(COSName.XOBJECT))
                .getDictionaryObject(COSName.getPDFName(name)).getCOSObject();
    }
}
