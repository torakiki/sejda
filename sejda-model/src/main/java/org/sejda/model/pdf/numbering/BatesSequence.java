/*
 * Copyright 2015 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.model.pdf.numbering;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.text.DecimalFormat;

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
