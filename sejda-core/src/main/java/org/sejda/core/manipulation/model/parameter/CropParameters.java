/*
 * Created on 09/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.core.manipulation.model.parameter;

import java.util.Collections;
import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.sejda.core.manipulation.model.RectangularBox;
import org.sejda.core.manipulation.model.parameter.base.SinglePdfSourceSingleOutputParameters;
import org.sejda.core.support.NullSafeSet;
import org.sejda.core.validation.constraint.NotEmpty;

/**
 * Parameter containing rectangular selection to be cropped on an input pdf source.
 * 
 * @author Andrea Vacondio
 * 
 */
public class CropParameters extends SinglePdfSourceSingleOutputParameters {

    @Valid
    @NotEmpty
    private final Set<RectangularBox> cropAreas = new NullSafeSet<RectangularBox>();

    /**
     * @return an unmodifiable view of the crop areas.
     */
    public Set<RectangularBox> getCropAreas() {
        return Collections.unmodifiableSet(cropAreas);
    }

    /**
     * clear crop areas
     */
    public void clearCropAreas() {
        cropAreas.clear();
    }

    /**
     * adds a area to the crop areas set.
     * 
     * @param area
     */
    public void addCropArea(RectangularBox area) {
        cropAreas.add(area);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(cropAreas).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CropParameters)) {
            return false;
        }
        CropParameters parameter = (CropParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(cropAreas, parameter.cropAreas).isEquals();
    }
}
