package org.sejda.model.parameter;
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

import jakarta.validation.constraints.NotNull;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.pdfa.ConformanceLevel;
import org.sejda.model.pdfa.InvalidElementPolicy;

/**
 * @author Andrea Vacondio
 */
public class ConvertToPDFAParameters extends MultiplePdfSourceMultipleOutputParameters {

    @NotNull
    private final InvalidElementPolicy invalidElementPolicy;
    @NotNull
    private final ConformanceLevel conformanceLevel;

    public ConvertToPDFAParameters(InvalidElementPolicy invalidElementPolicy, ConformanceLevel conformanceLevel) {
        this.invalidElementPolicy = invalidElementPolicy;
        this.conformanceLevel = conformanceLevel;
    }

    public InvalidElementPolicy invalidElementPolicy() {
        return invalidElementPolicy;
    }

    public ConformanceLevel conformanceLevel() {
        return conformanceLevel;
    }
}
