/*
 * Copyright 2015 by Eduard Weissmann (edi.weissmann@gmail.com).
 *
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
package org.sejda.model.pdf.numbering;

import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BatesSequence {

    private long current;
    private final int step;
    private final DecimalFormat decimalFormat;

    public BatesSequence() {
        this(1, 1, 6);
    }

    public BatesSequence(long startFrom, int step, int digits) {
        this.current = startFrom;
        this.step = step;
        this.decimalFormat = new DecimalFormat(StringUtils.repeat('0', digits));
    }

    public String next() {
        String result = decimalFormat.format(current);
        current += step;
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("current", current).append("step", step).append("format", decimalFormat)
            .toString();
    }

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder.HashCodeBuilder()
                .append(this.current)
                .append(this.step)
                .append(this.decimalFormat)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BatesSequence other = (BatesSequence) obj;
        return new org.apache.commons.lang3.builder.EqualsBuilder()
                .append(this.current, other.current)
                .append(this.step, other.step)
                .append(this.decimalFormat, other.decimalFormat)
                .isEquals();
    }
}
