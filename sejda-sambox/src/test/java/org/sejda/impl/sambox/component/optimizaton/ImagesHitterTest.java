/*
 * Created on 01 feb 2016
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

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream;
import org.sejda.io.SeekableSources;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;

public class ImagesHitterTest {

    private ImagesHitter victim = new ImagesHitter();

    @Test
    public void testAccept() throws Exception {
        PDDocument document = PDFParser.parse(SeekableSources.inMemorySeekableSourceFrom(
                getClass().getClassLoader().getResourceAsStream("pdf/draw_w_transparency.pdf")));
        document.getPages().forEach(victim::accept);
        PDPage page = document.getPage(0);
        COSDictionary pageRes = page.getResources().getCOSObject();
        assertTrue(((COSDictionary) pageRes.getDictionaryObject(COSName.XOBJECT))
                .getDictionaryObject(COSName.getPDFName("x5")).getCOSObject() instanceof ReadOnlyFilteredCOSStream);
        PDFormXObject form = (PDFormXObject) page.getResources().getXObject(COSName.getPDFName("x7"));
        PDFormXObject nestedForm = (PDFormXObject) form.getResources().getXObject(COSName.getPDFName("x10"));
        COSDictionary nestedFormRes = nestedForm.getResources().getCOSObject();
        assertTrue("Hitter should discover images nested in form xobjects",
                ((COSDictionary) nestedFormRes.getDictionaryObject(COSName.XOBJECT))
                        .getDictionaryObject(COSName.getPDFName("x17"))
                        .getCOSObject() instanceof ReadOnlyFilteredCOSStream);
    }

}
