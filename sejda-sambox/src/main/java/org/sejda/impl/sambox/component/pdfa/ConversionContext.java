package org.sejda.impl.sambox.component.pdfa;
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

import org.sejda.model.parameter.ConvertToPDFAParameters;
import org.sejda.model.pdfa.InvalidElementPolicy;
import org.sejda.model.task.NotifiableTaskMetadata;

import java.util.function.Supplier;

/**
 * @author Andrea Vacondio
 */
public class ConversionContext {

    private final ConvertToPDFAParameters parameters;
    private final NotifiableTaskMetadata notifiableMetadata;

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
}
