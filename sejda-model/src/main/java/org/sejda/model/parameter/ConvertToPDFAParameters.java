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
import org.sejda.model.pdfa.ICCProfile;
import org.sejda.model.pdfa.InvalidElementPolicy;
import org.sejda.model.pdfa.OutputIntent;

/**
 * @author Andrea Vacondio
 */
public class ConvertToPDFAParameters extends MultiplePdfSourceMultipleOutputParameters {

    @NotNull
    private final InvalidElementPolicy invalidElementPolicy;
    @NotNull
    private final ConformanceLevel conformanceLevel;
    @NotNull
    private OutputIntent outputIntent;
    private boolean forceOutputIntentReplacement = false;
    private ICCProfile defaultRGBProfile;
    private ICCProfile deviceCMYKProfile;

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

    public void setForceOutputIntentReplacement(boolean forceOutputIntentReplacement) {
        this.forceOutputIntentReplacement = forceOutputIntentReplacement;
    }

    public void setOutputIntent(OutputIntent outputIntent) {
        this.outputIntent = outputIntent;
    }

    public OutputIntent getOutputIntent() {
        return outputIntent;
    }

    public boolean isForceOutputIntentReplacement() {
        return forceOutputIntentReplacement;
    }

    public ICCProfile getDefaultRGBProfile() {
        return defaultRGBProfile;
    }

    public ICCProfile getDeviceCMYKProfile() {
        return deviceCMYKProfile;
    }

    public void setDefaultRGBProfile(ICCProfile defaultRGBProfile) {
        this.defaultRGBProfile = defaultRGBProfile;
    }

    public void setDeviceCMYKProfile(ICCProfile deviceCMYKProfile) {
        this.deviceCMYKProfile = deviceCMYKProfile;
    }
}
