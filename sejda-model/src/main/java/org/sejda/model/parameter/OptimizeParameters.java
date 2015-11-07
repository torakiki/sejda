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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;

/**
 * Parameters for the optimize task, which compresses images found in the PDF re-saving them as JPG 60% quality by default.
 * Depending on JPG codecs found on the system, this might not work as expected: PDF files containing images already compressed using better algorithms.
 */
public class OptimizeParameters extends MultiplePdfSourceMultipleOutputParameters {

    private boolean compressImages = true;
    private float compressedImageQuality = 0.6f;

    public boolean isCompressImages() {
        return compressImages;
    }

    public void setCompressImages(boolean compressImages) {
        this.compressImages = compressImages;
    }

    public float getCompressedImageQuality() {
        return compressedImageQuality;
    }

    public void setCompressedImageQuality(float compressedImageQuality) {
        this.compressedImageQuality = compressedImageQuality;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(compressImages)
                .append(compressedImageQuality)
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
                .append(isCompressImages(), parameter.isCompressImages())
                .append(getCompressedImageQuality(), parameter.getCompressedImageQuality())
                .isEquals();
    }
}
