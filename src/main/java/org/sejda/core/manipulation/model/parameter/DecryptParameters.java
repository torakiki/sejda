/*
 * Created on 15/set/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Parameter class for the decrypt manipulation. Accepts a list of {@link org.sejda.core.manipulation.model.input.PdfSource} that will be decrypted.
 * 
 * @author Andrea Vacondio
 * 
 */
public class DecryptParameters extends PdfSourceListParameters {

    private String outputPrefix = "";

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(outputPrefix).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DecryptParameters)) {
            return false;
        }
        DecryptParameters parameter = (DecryptParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(outputPrefix, parameter.getOutputPrefix())
                .isEquals();
    }
}
