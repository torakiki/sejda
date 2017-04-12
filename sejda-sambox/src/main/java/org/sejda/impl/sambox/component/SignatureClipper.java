/*
 * Created on 18 set 2015
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

import static java.util.Objects.nonNull;

import java.util.Collection;

import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component responsible for removing signature values if found
 * 
 * @author Andrea Vacondio
 */
public final class SignatureClipper {

    private static final Logger LOG = LoggerFactory.getLogger(SignatureClipper.class);

    private SignatureClipper() {
        // utility
    }

    /**
     * Removes signature values if found in the input {@link PDAnnotation}s, in case of a merged widget/field dictionary.
     * 
     * @param annotations
     */
    public static void clipSignatures(Collection<PDAnnotation> annotations) {
        if (nonNull(annotations)) {
            for (PDAnnotation annotation : annotations) {
                if (COSName.WIDGET.getName().equals(annotation.getSubtype())
                        && COSName.SIG.equals(annotation.getCOSObject().getCOSName(COSName.FT))) {
                    clipSignature(annotation.getCOSObject());
                }
            }
        }
    }

    public static void clipSignatures(PDDocument doc) {
        for(PDPage page: doc.getPages()){
            clipSignatures(page.getAnnotations());
        }
    }

    /**
     * Removes signature values if found in the input {@link PDField}.
     * 
     * @param field
     * @return true
     *            if the field was a Signature and it has been clipped
     */
    public static boolean clipSignature(PDField field) {
        if (nonNull(field) && COSName.SIG.getName().equals(field.getFieldType())) {
            clipSignature(field.getCOSObject());
            return true;
        }
        return false;
    }

    private static void clipSignature(COSDictionary item) {
        LOG.info("Removing signature value from the field if any");
        item.removeItem(COSName.V);
        item.removeItem(COSName.SV);
        item.removeItem(COSName.LOCK);
    }
}
