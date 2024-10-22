/*
 * Created on 29/05/24
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
import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.parameter.ConvertToPDFAParameters;
import org.sejda.model.pdfa.CompactCGATSTR001;
import org.sejda.model.pdfa.ICCProfile;
import org.sejda.model.pdfa.InvalidElementPolicy;
import org.sejda.model.pdfa.OutputIntent;
import org.sejda.model.pdfa.SRGB2014;
import org.sejda.model.task.NotifiableTaskMetadata;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSInteger;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.IndirectCOSObjectIdentifier;
import org.sejda.sambox.pdmodel.ResourceCache;
import org.sejda.sambox.pdmodel.font.PDFontFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.zip.DeflaterInputStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.RequireUtils.requireNotNullArg;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;

/**
 * @author Andrea Vacondio
 */
public class ConversionContext {

    private final ConvertToPDFAParameters parameters;
    private final NotifiableTaskMetadata notifiableMetadata;
    private OutputIntent outputIntent;
    private boolean hasCorICAnnotationKey = false;
    private final COSArray defaultCMYK;
    private final COSArray defaultRGB;
    private final Map<IndirectCOSObjectIdentifier, PdfAFont> fonts = new HashMap<>();
    private PdfAFont currentFont;

    public ConversionContext(ConvertToPDFAParameters parameters, NotifiableTaskMetadata notifiableMetadata) {
        this.parameters = parameters;
        this.notifiableMetadata = notifiableMetadata;
        defaultCMYK = new COSArray(COSName.ICCBASED, new ReadOnlyFilteredCOSStream(
                COSDictionary.of(COSName.FILTER, COSName.FLATE_DECODE, COSName.N, COSInteger.get(4), COSName.ALTERNATE,
                        COSName.DEVICECMYK), () -> new DeflaterInputStream(
                ofNullable(parameters.getDeviceCMYKProfile()).map(ICCProfile::profileData)
                        .orElseGet(() -> new CompactCGATSTR001().profileData())), -1));
        defaultRGB = new COSArray(COSName.ICCBASED, new ReadOnlyFilteredCOSStream(
                COSDictionary.of(COSName.FILTER, COSName.FLATE_DECODE, COSName.N, COSInteger.get(3), COSName.ALTERNATE,
                        COSName.DEVICERGB), () -> new DeflaterInputStream(
                ofNullable(parameters.getDefaultRGBProfile()).map(ICCProfile::profileData)
                        .orElseGet(() -> new SRGB2014().profileData())), -1));
    }

    public NotifiableTaskMetadata notifiableMetadata() {
        return notifiableMetadata;
    }

    /**
     * Executes a validation check on whether to fail on an invalid element based on the invalid element policy specified in the parameters.
     * If the invalid element policy is set to "FAIL", the method throws the specified exception.
     *
     * @throws T the specified exception if the invalid element policy is set to "FAIL"
     */
    <T extends Exception> void maybeFailOnInvalidElement(Supplier<T> exceptionSupplier) throws T {
        if (parameters.invalidElementPolicy() == InvalidElementPolicy.FAIL) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Sanitize a dictionary making sure it doesn't contain values for the given keys.
     *
     * @param dictionaryDescription description used in the exception message, Ex: "Catalog", "Widget annotation"
     * @throws TaskExecutionException if the dictionary contains forbidden keys and the parameters policy is {@link InvalidElementPolicy#FAIL}
     */
    void maybeRemoveForbiddenKeys(COSDictionary dictionary, String dictionaryDescription, COSName... keys)
            throws TaskExecutionException {
        maybeRemoveForbiddenKeys(dictionary, dictionaryDescription, TaskExecutionException::new, keys);
    }

    /**
     * Sanitize a dictionary making sure it doesn't contain values for the given keys.
     *
     * @param dictionaryDescription description used in the exception message, Ex: "Catalog", "Widget annotation"
     * @throws T if the dictionary contains forbidden keys and the parameters policy is {@link InvalidElementPolicy#FAIL}
     */
    <T extends Exception> void maybeRemoveForbiddenKeys(COSDictionary dictionary, String dictionaryDescription,
            Function<String, T> exceptionSupplier, COSName... keys) throws T {
        if (nonNull(dictionary)) {
            for (COSName key : keys) {
                if (dictionary.containsKey(key)) {
                    maybeFailOnInvalidElement(() -> exceptionSupplier.apply(
                            dictionaryDescription + " dictionary shall not include a " + key.getName() + " entry"));
                    dictionary.removeItem(key);
                    notifyEvent(notifiableMetadata()).taskWarning(
                            "Removed " + key.getName() + " key from " + dictionaryDescription + " dictionary");
                }
            }
        }
    }

    void maybeRemoveForbiddenAction(COSDictionary dictionary, String dictionaryDescription, COSName key)
            throws TaskExecutionException {
        if (nonNull(dictionary)) {
            var action = dictionary.getDictionaryObject(key, COSDictionary.class);
            if (nonNull(action)) {
                var type = of(action).map(a -> a.getCOSName(COSName.S)).map(COSName::getName).orElse("UNKNOWN");
                if (!parameters().conformanceLevel().allowedActionTypes().contains(type)) {
                    maybeFailOnInvalidElement(() -> new TaskExecutionException(
                            dictionaryDescription + " dictionary shall not include an action of type " + type));
                    dictionary.removeItem(key);
                    notifyEvent(notifiableMetadata()).taskWarning(
                            "Removed " + key + " key from " + dictionaryDescription + " dictionary");
                } else {
                    if ("Named".equals(type)) {
                        var name = of(action).map(a -> a.getCOSName(COSName.N)).map(COSName::getName).orElse("UNKNOWN");
                        if (!parameters().conformanceLevel().allowedNamedActions().contains(name)) {
                            maybeFailOnInvalidElement(() -> new TaskExecutionException(
                                    dictionaryDescription + " dictionary shall not include an named action with name "
                                            + name));
                            dictionary.removeItem(key);
                            notifyEvent(notifiableMetadata()).taskWarning(
                                    "Removed " + key + " key from " + dictionaryDescription + " dictionary");
                        }
                    }
                }
            }
        }
    }

    /**
     * If necessary, it adds a default color space to the input color space resources (COSName.COLORSPACE branch of the resource dictionary)
     */
    void maybeAddDefaultColorSpaceFor(COSBase colorSpace, COSDictionary csResources) throws IOException {
        //TODO detect cycles
        //only the Device and Pattern color spaces are defined as a COSName, all the others are COSArray
        if (COSName.DEVICECMYK.equals(colorSpace) && outputIntent().profile().components() == 3) {
            //add default for device cmyk
            csResources.computeIfAbsent(COSName.DEFAULT_CMYK, k -> defaultCMYK(), COSArray.class);
        }
        if (COSName.DEVICERGB.equals(colorSpace) && outputIntent().profile().components() == 4) {
            //add default for device rgb
            csResources.computeIfAbsent(COSName.DEFAULT_RGB, k -> defaultRGB(), COSArray.class);
        }
        if (colorSpace instanceof COSArray array && !array.isEmpty()) {
            if (array.getObject(0) instanceof COSName name) {
                if (COSName.SEPARATION.equals(name) && array.size() > 2) {
                    maybeAddDefaultColorSpaceFor(array.getObject(2), csResources);
                }
                if (COSName.INDEX.equals(name) && array.size() > 1) {
                    maybeAddDefaultColorSpaceFor(array.getObject(1), csResources);
                }
                if (COSName.DEVICEN.equals(name) && array.size() > 2) {
                    ofNullable(array.getObject(1, COSArray.class)).filter(a -> a.size() > 8).orElseThrow(
                            () -> new IOException("Maximum number of colorants in DeviceN colorspace is 8"));
                    maybeAddDefaultColorSpaceFor(array.getObject(2), csResources);
                }
                if (COSName.PATTERN.equals(name) && array.size() > 1) {
                    maybeAddDefaultColorSpaceFor(array.getObject(1), csResources);
                }
            }
        }
    }

    void sanitizeRenderingIntents(COSDictionary dictionary) throws IOException {
        var intent = dictionary.getNameAsString(COSName.INTENT);
        if (nonNull(intent) && !parameters().conformanceLevel().allowedRenderingIntents().contains(intent)) {
            maybeFailOnInvalidElement(() -> new IOException("Found invalid rendering intent value " + intent));
            dictionary.setItem(COSName.INTENT, COSName.RELATIVE_COLORIMETRIC);
            notifyEvent(notifiableMetadata()).taskWarning(
                    "Invalid rendering intent set to " + COSName.RELATIVE_COLORIMETRIC.getName());
        }
    }

    public ConvertToPDFAParameters parameters() {
        return parameters;
    }

    public OutputIntent outputIntent() {
        return this.outputIntent;
    }

    public void outputIntent(OutputIntent outputIntent) {
        requireNotNullArg(outputIntent, "Output intent cannot be null");
        this.outputIntent = outputIntent;
    }

    public boolean hasCorICAnnotationKey() {
        return hasCorICAnnotationKey;
    }

    public COSArray defaultRGB() {
        return defaultRGB;
    }

    public COSArray defaultCMYK() {
        return defaultCMYK;
    }

    public void maybeFixFontsWidths() throws TaskExecutionException {
        for (var pdfaFont : fonts.values()) {
            if (pdfaFont.wrongWidth()) {
                maybeFailOnInvalidElement(
                        () -> new TaskExecutionException("Font " + pdfaFont.name() + " has inconsistent width"));
                try {
                    pdfaFont.regenerateFontWidths();
                } catch (IOException e) {
                    throw new TaskExecutionException("Unable to generate width values from font", e);
                }
            }
        }
    }

    PdfAFont currentFont() {
        return currentFont;
    }

    /**
     * sets whether the document has annotations with the /C or /IC key
     */
    public void hasCorICAnnotationKey(boolean hasCorICAnnotationKey) {
        this.hasCorICAnnotationKey = hasCorICAnnotationKey;
    }

    public void setCurrentFont(COSDictionary fontDictionary, String fontName, ResourceCache resourceCache)
            throws IOException {
        if (fontDictionary.hasId()) {
            if (isNull(fonts.get(fontDictionary.id()))) {
                fonts.put(fontDictionary.id(),
                        PdfAFont.getInstance(PDFontFactory.createFont(fontDictionary, resourceCache), fontName));
            }
            currentFont = fonts.get(fontDictionary.id());
        }
    }
}
