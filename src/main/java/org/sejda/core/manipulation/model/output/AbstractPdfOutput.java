/*
 * Created on 30/mag/2010
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
package org.sejda.core.manipulation.model.output;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Abstract implementation of a pdf output destination where the results of a manipulation will be written.
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractPdfOutput implements Serializable {

    /**
     * @return the type of this output
     */
    public abstract OutputType getOutputType();

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(getOutputType()).toString();
    }
}
