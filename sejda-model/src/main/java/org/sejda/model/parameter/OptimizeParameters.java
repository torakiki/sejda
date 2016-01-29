/*
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.parameter;

import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.common.collection.NullSafeSet;
import org.sejda.model.optimization.Optimization;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;

/**
 * Parameters for the optimize task, which compresses images found in the PDF changing their DPI to 144 by default. Depending on JPG codecs found on the system, this might not work
 * as expected: PDF files containing images already compressed using better algorithms.
 */
public class OptimizeParameters extends MultiplePdfSourceMultipleOutputParameters {

    private float imageQuality = 1.0f;
    private int imageDpi = 72;
    private int imageMaxWidthOrHeight = 1280;
    private Set<Optimization> optimizations = new NullSafeSet<>();

    public float getImageQuality() {
        return imageQuality;
    }

    public int getImageDpi() {
        return imageDpi;
    }

    public void setImageDpi(int imageDpi) {
        this.imageDpi = imageDpi;
    }

    public void setImageQuality(float imageQuality) {
        this.imageQuality = imageQuality;
    }

    public int getImageMaxWidthOrHeight() {
        return imageMaxWidthOrHeight;
    }

    public void setImageMaxWidthOrHeight(int imageMaxWidthOrHeight) {
        this.imageMaxWidthOrHeight = imageMaxWidthOrHeight;
    }

    public Set<Optimization> getOptimizations() {
        return optimizations;
    }

    public void setOptimizations(Set<Optimization> optimizations) {
        this.optimizations = optimizations;
    }

    public void addOptimization(Optimization optimization) {
        this.optimizations.add(optimization);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(optimizations).append(imageQuality)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OptimizeParameters)) {
            return false;
        }
        OptimizeParameters parameter = (OptimizeParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other))
                .append(getOptimizations(), parameter.getOptimizations())
                .append(getImageQuality(), parameter.getImageQuality()).append(getImageDpi(), parameter.getImageDpi())
                .append(getImageMaxWidthOrHeight(), parameter.getImageMaxWidthOrHeight()).isEquals();
    }
}
