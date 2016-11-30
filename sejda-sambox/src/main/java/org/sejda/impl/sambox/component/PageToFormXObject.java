/*
 * Created on 29 lug 2016
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
package org.sejda.impl.sambox.component;

import static java.util.Optional.ofNullable;
import static org.sejda.util.RequireUtils.requireNotNullArg;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.function.Function;

import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.common.PDStream;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;

/**
 * a {@link Function} capable of transforming a {@link PDPage} to a {@link PDFormXObject}
 * 
 * @author Andrea Vacondio
 */
public class PageToFormXObject {

    /**
     * @return a {@link PDFormXObject} corresponding to the given {@link PDPage} or null if an error occurred
     */
    public PDFormXObject apply(PDPage page) throws IOException {
        requireNotNullArg(page, "Cannot convert a null page");
        PDStream stream = getStream(page);

        PDFormXObject form = new PDFormXObject(stream);
        form.setResources(page.getResources());
        PDRectangle mediaBox = page.getMediaBox();
        PDRectangle boundingBox = ofNullable(page.getTrimBox()).orElse(mediaBox);

        // this comes from PDFBox Superimpose class
        AffineTransform at = form.getMatrix().createAffineTransform();
        at.translate(mediaBox.getLowerLeftX() - boundingBox.getLowerLeftX(),
                mediaBox.getLowerLeftY() - boundingBox.getLowerLeftY());
        switch (page.getRotation()) {
        case 90:
            // at.scale(boundingBox.getWidth() / boundingBox.getHeight(),
            // boundingBox.getHeight() / boundingBox.getWidth());
            at.translate(0, boundingBox.getWidth());
            at.rotate(-Math.PI / 2.0);
            break;
        case 180:
            at.translate(boundingBox.getWidth(), boundingBox.getHeight());
            at.rotate(-Math.PI);
            break;
        case 270:
            // at.scale(boundingBox.getWidth() / boundingBox.getHeight(),
            // boundingBox.getHeight() / boundingBox.getWidth());
            at.translate(boundingBox.getHeight(), 0);
            at.rotate(-Math.PI * 1.5);
            break;
        default:
            // no additional transformations necessary
        }
        // Compensate for Crop Boxes not starting at 0,0
        at.translate(-boundingBox.getLowerLeftX(), -boundingBox.getLowerLeftY());
        if (!at.isIdentity()) {
            form.setMatrix(at);
        }

        form.setBBox(new PDRectangle(boundingBox.getLowerLeftX(), boundingBox.getLowerLeftY(),
                boundingBox.getUpperRightX(), boundingBox.getUpperRightY()));
        return form;
    }

    private PDStream getStream(PDPage page) throws IOException {
        COSBase base = page.getCOSObject().getDictionaryObject(COSName.CONTENTS);
        if (base instanceof COSStream) {
            // the common case, 1 content stream, we just pass it with a cloned dictionary, so we can change dictionary values (ex. add the Form type) but we don't need to read the
            // stream into memory
            COSStream existing = (COSStream) base;
            return new PDStream(new ReadOnlyFilteredCOSStream(existing.duplicate(), () -> existing.getFilteredStream(),
                    existing.getFilteredLength()));
        } else if (base instanceof COSArray && ((COSArray) base).size() > 0) {
            return new PDStream(page.getContents(), COSName.FLATE_DECODE);
        }
        return null;
    }

}
