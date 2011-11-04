/*
 * Created on Sep 2, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import java.util.List;

import org.sejda.model.input.AbstractPdfSource;

/**
 * A parameter whose execution inputs consists of a list of pdf documents.
 * 
 * @author Eduard Weissmann
 * 
 */
public interface MultiplePdfSourceTaskParameters {

    /**
     * adds the input source to the source list.
     * 
     * @param input
     */
    void addSource(AbstractPdfSource input);

    /**
     * @return a view of the source list
     */
    List<AbstractPdfSource> getSourceList();
}
