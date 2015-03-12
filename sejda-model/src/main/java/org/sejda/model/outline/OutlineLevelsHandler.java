/*
 * Created on 07/ago/2011
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
package org.sejda.model.outline;

/**
 * Component providing methods to retrieve outline information based on depth level.
 * 
 * @author Andrea Vacondio
 */
public interface OutlineLevelsHandler {

    /**
     * @return the max depth level in the pdf document outline associated to this handler.
     */
    int getMaxOutlineDepth();

    /**
     * @param level
     * @return page destinations found at the given outline level
     */
    OutlinePageDestinations getPageDestinationsForLevel(int level);
}
