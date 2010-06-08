/*
 * Created on 06/giu/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.manipulation.model.task.itext.component;

import org.apache.log4j.Logger;
import org.sejda.core.manipulation.model.rotation.PageRotation;
import org.sejda.core.manipulation.model.rotation.RotationType;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;

/**
 * Handles rotations on a given PdfReader
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PdfRotationHandler implements OngoingRotation {

    private static final Logger LOG = Logger.getLogger(PdfRotationHandler.class.getPackage().getName());

    private PdfReader reader;
    private PageRotation rotation;

    private PdfRotationHandler(PageRotation rotation) {
        this.rotation = rotation;
    }

    /**
     * entry point to apply a rotation
     * <p>
     * <code>applyRotation(rotation).to(reader);</code>
     * </p>
     * 
     * @param rotation
     * @return
     */
    public static OngoingRotation applyRotation(PageRotation rotation) {
        return new PdfRotationHandler(rotation);
    }

    public void to(PdfReader reader) {
        this.reader = reader;
        executeRotation();
    }

    /**
     * Apply the rotation to the dictionary of the given {@link PdfReader}
     */
    private void executeRotation() {
        RotationType type = rotation.getRotationType();
        LOG.debug(String.format("Applying rotation of %d to pages %s", rotation.getRotation().getDegrees(), type));
        if (RotationType.SINGLE_PAGE.equals(type)) {
            apply(rotation.getPageNumber());
        } else {
            for (int j = 1; j <= reader.getNumberOfPages(); j++) {
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
            PdfDictionary dictionary = reader.getPageN(pageNmber);
            int rotationDegrees = (rotation.getRotation().getDegrees() + reader.getPageRotation(pageNmber)) % 360;
            dictionary.put(PdfName.ROTATE, new PdfNumber(rotationDegrees));
        }
    }
}
