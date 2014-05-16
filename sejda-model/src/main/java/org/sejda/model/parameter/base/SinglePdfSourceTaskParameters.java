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

import org.sejda.model.input.PdfSource;

/**
 * A parameter with a single source as input for the task.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface SinglePdfSourceTaskParameters extends TaskParameters {

    /**
     * @return the source for this parameter.
     */
    PdfSource<?> getSource();

    /**
     * Set the source for this parameter.
     * 
     * @param source
     */
    void setSource(PdfSource<?> source);
}
