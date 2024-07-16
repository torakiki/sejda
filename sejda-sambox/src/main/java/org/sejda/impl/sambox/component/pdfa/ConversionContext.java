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

import org.sejda.model.exception.TaskExecutionException;
import org.sejda.model.parameter.ConvertToPDFAParameters;
import org.sejda.model.pdfa.InvalidElementPolicy;
import org.sejda.model.pdfa.OutputIntent;
import org.sejda.model.task.NotifiableTaskMetadata;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;
import static java.util.Optional.of;
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

    public ConversionContext(ConvertToPDFAParameters parameters, NotifiableTaskMetadata notifiableMetadata) {
        this.parameters = parameters;
        this.notifiableMetadata = notifiableMetadata;
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
     * @throws Exception if the dictionary contains forbidden keys and the parameters policy is {@link InvalidElementPolicy#FAIL}
     */
    <T extends Exception> void maybeRemoveForbiddenKeys(COSDictionary dictionary, String dictionaryDescription,
            Function<String, T> exceptionSupplier, COSName... keys) throws T {
        if (nonNull(dictionary)) {
            for (COSName key : keys) {
                if (dictionary.containsKey(key)) {
                    maybeFailOnInvalidElement(() -> exceptionSupplier.apply(
                            dictionaryDescription + " dictionary shall not include a " + key + " entry"));
                    dictionary.removeItem(key);
                    notifyEvent(notifiableMetadata()).taskWarning(
                            "Removed " + key + " key from " + dictionaryDescription + " dictionary");
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

    /**
     * sets whether the document has annotations with the /C or /IC key
     */
    public void hasCorICAnnotationKey(boolean hasCorICAnnotationKey) {
        this.hasCorICAnnotationKey = hasCorICAnnotationKey;
    }
}
