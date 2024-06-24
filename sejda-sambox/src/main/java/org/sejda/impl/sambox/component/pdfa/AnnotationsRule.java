package org.sejda.impl.sambox.component.pdfa;
/*
 * Created on 21/06/24
 * Copyright 2024 Sober Lemur S.r.l. and Sejda BV
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

import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;

import java.util.Set;

import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.sambox.cos.COSName.getPDFName;

/**
 * Rule 6.5.2 of ISO 19005-1: Annotation types not defined in PDF Reference shall not be permitted.
 * Rule 6.5.3 of ISO 19005-1: Annotation values restrictions.
 *
 * @author Andrea Vacondio
 */
public class AnnotationsRule extends BaseRule<PDPage, TaskException> {

    private static final Set<COSName> ALLOWED = Set.of(getPDFName("Text"), getPDFName("Link"), getPDFName("FreeText"),
            getPDFName("Line"), getPDFName("Square"), getPDFName("Circle"), getPDFName("Highlight"),
            getPDFName("Underline"), getPDFName("Squiggly"), getPDFName("StrikeOut"), getPDFName("Stamp"),
            getPDFName("Ink"), getPDFName("Popup"), getPDFName("Widget"), getPDFName("PrinterMark"),
            getPDFName("TrapNet"));

    public AnnotationsRule(ConversionContext conversionContext) {
        super(conversionContext);
    }

    @Override
    public void accept(PDPage page) throws TaskException {

        var annotations = page.getCOSObject().getDictionaryObject(COSName.ANNOTS, COSArray.class);
        var newAnnotations = new COSArray();
        if (nonNull(annotations)) {
            for (int i = 0; i < annotations.size(); i++) {
                var annotation = annotations.getObject(i, COSDictionary.class);
                var subtype = of(annotation).map(a -> a.getCOSName(COSName.SUBTYPE))
                        .orElseGet(() -> getPDFName("UNKNOWN"));
                if (ALLOWED.contains(subtype)) {
                    sanitizeCAValue(annotation);
                    sanitizeFlag(annotation, PDAnnotation.FLAG_PRINTED, true);
                    sanitizeFlag(annotation, PDAnnotation.FLAG_HIDDEN, false);
                    sanitizeFlag(annotation, PDAnnotation.FLAG_INVISIBLE, false);
                    sanitizeFlag(annotation, PDAnnotation.FLAG_NO_VIEW, false);
                    //Text annotations should set the NoZoom and NoRotate flag bits of the F key to 1
                    if (subtype.equals(getPDFName("Text"))) {
                        sanitizeFlag(annotation, PDAnnotation.FLAG_NO_ZOOM, true);
                        sanitizeFlag(annotation, PDAnnotation.FLAG_NO_ROTATE, true);
                    }
                    newAnnotations.add(annotation);
                } else {
                    conversionContext().maybeFailOnInvalidElement(
                            () -> new TaskExecutionException("Found a not permitted annotation type: " + subtype));
                    notifyEvent(conversionContext().notifiableMetadata()).taskWarning(
                            "Removed not permitted annotation of type: " + subtype);
                }
            }
            page.getCOSObject().setItem(COSName.ANNOTS, newAnnotations);
        }
    }

    /**
     * An annotation dictionary shall not contain the CA key with a value other than 1.0.
     */
    private void sanitizeCAValue(COSDictionary annotation) throws TaskExecutionException {
        float constantOpacity = annotation.getFloat(COSName.CA, 1.0f);
        if (constantOpacity != 1.0f) {
            conversionContext().maybeFailOnInvalidElement(
                    () -> new TaskExecutionException("Found an annotation with invalid CA value"));
            annotation.setFloat(COSName.CA, 1.0f);
            notifyEvent(conversionContext().notifiableMetadata()).taskWarning(
                    String.format("Invalid CA value %f overridden with 1.0", constantOpacity));
        }
    }

    private void sanitizeFlag(COSDictionary annotation, int flag, boolean expected) throws TaskExecutionException {
        if (expected != annotation.getFlag(COSName.F, flag)) {
            conversionContext().maybeFailOnInvalidElement(
                    () -> new TaskExecutionException("Found an annotation with flag " + flag + " to " + !expected));
            annotation.setFlag(COSName.F, flag, expected);
            notifyEvent(conversionContext().notifiableMetadata()).taskWarning(
                    "Modified annotation flag " + flag + " to " + expected);
        }
    }
}
