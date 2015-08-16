/*
 * Created on 06/jun/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.itext.component;

import static org.sejda.model.rotation.Rotation.getRotation;

import java.util.Set;

import org.sejda.model.rotation.Rotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfReader;

/**
 * Handles rotations on a given PdfReader
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PdfRotator implements OngoingRotation {

    private static final Logger LOG = LoggerFactory.getLogger(PdfRotator.class);

    private PdfReader reader;
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
     * @return the ongoing apply rotation exposing methods to set the reader you want to apply the rotation to.
     */
    public static OngoingRotation applyRotation(Rotation rotation, Set<Integer> pages) {
        return new PdfRotator(rotation, pages);
    }

    public void to(PdfReader reader) {
        this.reader = reader;
        executeRotation();
    }

    /**
     * Apply the rotation to the dictionary of the given {@link PdfReader}
     */
    private void executeRotation() {
        LOG.debug("Applying rotation of {} degrees to {} pages", rotation.getDegrees(), pages.size());
        for (int p: pages) {
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
            PdfDictionary dictionary = reader.getPageN(pageNmber);
            dictionary.put(PdfName.ROTATE,
                    new PdfNumber(rotation.addRotation(getRotation(reader.getPageRotation(pageNmber)))
                            .getDegrees()));
        }
    }
}
