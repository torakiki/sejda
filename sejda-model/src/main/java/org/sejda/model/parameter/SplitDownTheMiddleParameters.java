/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.model.parameter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;
import org.sejda.model.repaginate.Repagination;

public class SplitDownTheMiddleParameters extends MultiplePdfSourceMultipleOutputParameters {

    private Repagination repagination = Repagination.NONE;

    public Repagination getRepagination() {
        return repagination;
    }

    public void setRepagination(Repagination repagination) {
        this.repagination = repagination;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getRepagination())
                .appendSuper(super.hashCode()).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SplitDownTheMiddleParameters)) {
            return false;
        }
        return new EqualsBuilder()
                .append(getRepagination(), ((SplitDownTheMiddleParameters) other).getRepagination())
                .appendSuper(super.equals(other))
                .isEquals();
    }
}
