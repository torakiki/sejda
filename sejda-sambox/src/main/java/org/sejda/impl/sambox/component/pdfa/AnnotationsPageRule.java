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
package org.sejda.impl.sambox.component.pdfa;

import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;

import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static org.sejda.commons.util.RequireUtils.require;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;

/**
 * Rule 6.5.2 of ISO 19005-1: Annotation types not defined in PDF Reference shall not be permitted.
 * Rule 6.5.3 of ISO 19005-1: Annotation values restrictions.
 *
 * @author Andrea Vacondio
 */
public class AnnotationsPageRule extends BaseRule<PDPage, TaskException> {

    // From tn0002_color_in_pdfa-1_2008-03-141
    // If PDF annotations specify colors in a C or IC entry (e.g. border
    //color for a link annotation) these colors are always specified in the DeviceRGB
    //color space; PDF 1.4 does not support device-independent color specifications
    //for annotations. Therefore, if the document contains colorized annotations an
    //RGB output intent is required
    private boolean forceRGBIntentOnBorderOrLinkColor;

    public AnnotationsPageRule(ConversionContext conversionContext) {
        super(conversionContext);
    }

    @Override
    public void accept(PDPage page) throws TaskException {

        var annotations = page.getCOSObject().getDictionaryObject(COSName.ANNOTS, COSArray.class);
        var newAnnotations = new COSArray();
        if (nonNull(annotations)) {
            for (int i = 0; i < annotations.size(); i++) {
                var annotation = annotations.getObject(i, COSDictionary.class);
                var subtype = of(annotation).map(a -> a.getCOSName(COSName.SUBTYPE)).map(COSName::getName)
                        .orElse("UNKNOWN");
                if (conversionContext().parameters().conformanceLevel().allowedAnnotationTypes().contains(subtype)) {
                    sanitizeCAValue(annotation);
                    sanitizeFlag(annotation, PDAnnotation.FLAG_PRINTED, true);
                    sanitizeFlag(annotation, PDAnnotation.FLAG_HIDDEN, false);
                    sanitizeFlag(annotation, PDAnnotation.FLAG_INVISIBLE, false);
                    sanitizeFlag(annotation, PDAnnotation.FLAG_NO_VIEW, false);
                    //Text annotations should set the NoZoom and NoRotate flag bits of the F key to 1
                    if (subtype.equals("Text")) {
                        sanitizeFlag(annotation, PDAnnotation.FLAG_NO_ZOOM, true);
                        sanitizeFlag(annotation, PDAnnotation.FLAG_NO_ROTATE, true);
                    }
                    sanitizeAppearance(annotation);
                    sanitizeAdditionalActions(annotation);
                    conversionContext().maybeRemoveForbiddenAction(annotation, "Annotation", COSName.A);
                    if (!conversionContext().hasCorICAnnotationKey()) {
                        conversionContext().hasCorICAnnotationKey(
                                annotation.containsKey(COSName.C) || annotation.containsKey(COSName.IC));
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

    private void sanitizeAppearance(COSDictionary annotation) throws TaskExecutionException {
        var appearanceDictionary = annotation.getDictionaryObject(COSName.AP, COSDictionary.class);
        if (nonNull(appearanceDictionary)) {
            var normalAppearance = appearanceDictionary.getDictionaryObject(COSName.N);
            if (COSName.WIDGET.equals(annotation.getCOSName(COSName.SUBTYPE)) && COSName.BTN.equals(
                    annotation.getCOSName(COSName.FT)) && !(normalAppearance instanceof COSDictionary)) {
                throw new TaskExecutionException(
                        "Appearance of widget annotations of Btn type shall have a N of type dictionary");
            } else {
                require(normalAppearance instanceof COSStream,
                        () -> new TaskExecutionException("Appearance of annotations shall have a N of type stream"));
            }
            if (appearanceDictionary.keySet().size() > 1) {
                conversionContext().maybeFailOnInvalidElement(() -> new TaskExecutionException(
                        "Found an annotation with multiple values in its AP dictionary"));
                COSDictionary newAppearanceDictionary = new COSDictionary();
                newAppearanceDictionary.setItem(COSName.N, normalAppearance);
                annotation.setItem(COSName.AP, newAppearanceDictionary);
                notifyEvent(conversionContext().notifiableMetadata()).taskWarning(
                        "Modified appearance to include only the N key");
            }
        }
    }

    private void sanitizeAdditionalActions(COSDictionary annotation) throws TaskExecutionException {
        if (COSName.WIDGET.equals(annotation.getCOSName(COSName.SUBTYPE))) {
            conversionContext().maybeRemoveForbiddenKeys(annotation, "Widget", COSName.AA, COSName.A);
        }
    }
}
