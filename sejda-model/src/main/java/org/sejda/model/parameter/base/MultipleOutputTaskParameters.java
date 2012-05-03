/*
 * Created on 14/set/2011
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
package org.sejda.model.parameter.base;

import org.sejda.model.output.MultipleTaskOutput;

/**
 * A {@link TaskParameters} parameter whose execution result in multiple output.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface MultipleOutputTaskParameters extends TaskParameters {

    /**
     * @return the prefix to be used to generate names of the multiple outputs.
     */
    String getOutputPrefix();

    /**
     * Set the prefix to be used to generate names of the multiple outputs for this parameter.
     * 
     * @param outputPrefix
     */
    void setOutputPrefix(String outputPrefix);

    /**
     * Sets the output destination
     * 
     * @param output
     */
    void setOutput(MultipleTaskOutput<?> output);
}
