/*
 * Created on 09/07/24
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

import org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.pdfa.DefaultRGBOutputIntent;
import org.sejda.model.pdfa.ICCProfile;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSInteger;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.graphics.color.PDOutputIntent;

import java.io.IOException;
import java.util.zip.DeflaterInputStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.sambox.cos.COSDictionary.of;

/**
 * @author Andrea Vacondio
 */
public class OutputIntentDocumentRule extends BaseRule<PDDocument, TaskException> {

    // From tn0002_color_in_pdfa-1_2008-03-141
    // If PDF annotations specify colors in a C or IC entry (e.g. border
    //color for a link annotation) these colors are always specified in the DeviceRGB
    //color space; PDF 1.4 does not support device-independent color specifications
    //for annotations. Therefore, if the document contains colorized annotations an
    //RGB output intent is required.
    //This flag tells if the rule should enforce and RGB output intent in case of C or IC entry in annotations
    private final boolean forceRGBIntentOnBorderOrLinkColor;

    public OutputIntentDocumentRule(ConversionContext conversionContext, boolean forceRGBIntentOnBorderOrLinkColor) {
        super(conversionContext);
        this.forceRGBIntentOnBorderOrLinkColor = forceRGBIntentOnBorderOrLinkColor;
    }

    @Override
    public void accept(PDDocument document) throws TaskException {
        var intents = document.getDocumentCatalog().getOutputIntents();
        COSStream existingICC = intents.stream()
                .filter(o -> COSName.GTS_PDFA1.equals(o.getCOSObject().getCOSName(COSName.S))).findAny()
                .map(PDOutputIntent::getDestOutputIntent).orElse(null);
        //If a file's OutputIntents array contains more than one entry, then all entries that contain a DestOutputProfile
        //key shall have as the value of that key the same indirect object, which shall be a valid ICC profile stream
        if (!conversionContext().parameters().isForceOutputIntentReplacement() && nonNull(existingICC)
                && intents.stream().allMatch(i -> existingICC.equals(i.getDestOutputIntent()))) {
            try {
                if (!forceRGBIntentOnBorderOrLinkColor || (conversionContext().hasCorICAnnotationKey()
                        && existingICC.getInt(COSName.N) == 3)) {
                    var intent = ICCProfile.fromInputStream(existingICC.getInt(COSName.N),
                            existingICC.getUnfilteredStream());
                    //parse the icc profile to make sure "a valid ICC profile stream as the value its DestOutputProfile key"
                    intent.colorSpace();
                    conversionContext().outputIntent(() -> intent);
                } else {
                    conversionContext().maybeFailOnInvalidElement(() -> new TaskExecutionException(
                            "Invalid DestOutputProfile icc profile of the OutputIntents array, an RGB profile is required"));
                    notifyEvent(conversionContext().notifiableMetadata()).taskWarning(
                            "Replacing the existing DestOutputProfile icc profile of the OutputIntents array, an RGB profile is required");
                }
            } catch (IOException e) {
                conversionContext().maybeFailOnInvalidElement(() -> new TaskExecutionException(
                        "Invalid DestOutputProfile icc profile of the OutputIntents array", e));
                notifyEvent(conversionContext().notifiableMetadata()).taskWarning(
                        "There was an error parsing the DestOutputProfile icc profile of the OutputIntents array... replacing it",
                        e);
            }
        }
        if (isNull(conversionContext().outputIntent())) {
            conversionContext().outputIntent(ofNullable(conversionContext().parameters().getOutputIntent()).orElseGet(
                    DefaultRGBOutputIntent::new));
            if (forceRGBIntentOnBorderOrLinkColor && (!conversionContext().hasCorICAnnotationKey()
                    || conversionContext().outputIntent().profile().components() != 3)) {
                conversionContext().outputIntent(new DefaultRGBOutputIntent());
                notifyEvent(conversionContext().notifiableMetadata()).taskWarning(
                        "Replacing user output intent with the default RGB one. An RGB profile is required");
            }

            var destOutputIntent = new ReadOnlyFilteredCOSStream(of(COSName.FILTER, COSName.FLATE_DECODE, COSName.N,
                    COSInteger.get(conversionContext().outputIntent().profile().components())),
                    () -> new DeflaterInputStream(conversionContext().outputIntent().profile().profileData()), -1);
            var outputIntent = of(COSName.TYPE, COSName.OUTPUT_INTENT, COSName.S, COSName.GTS_PDFA1,
                    COSName.DEST_OUTPUT_PROFILE, destOutputIntent);
            outputIntent.setString(COSName.REGISTRY_NAME, conversionContext().outputIntent().registryName());
            outputIntent.setString(COSName.OUTPUT_CONDITION, conversionContext().outputIntent().outputCondition());
            outputIntent.setString(COSName.OUTPUT_CONDITION_IDENTIFIER,
                    conversionContext().outputIntent().outputConditionIdentifier());
            outputIntent.setString(COSName.INFO, conversionContext().outputIntent().info());
            document.getDocumentCatalog().getCOSObject().setItem(COSName.OUTPUT_INTENTS, new COSArray(outputIntent));
        }
    }
}
