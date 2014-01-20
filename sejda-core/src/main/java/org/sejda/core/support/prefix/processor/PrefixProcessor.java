/*
 * Created on 29/giu/2010
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
package org.sejda.core.support.prefix.processor;

import org.sejda.core.support.prefix.model.NameGenerationRequest;

/**
 * A Processor takes a prefix string and applies the transformation it's designed for. Please consider that we currently reuse instances across multiple parameters execution
 * therefor implementations have to be stateless to avoid unexpected behavior.
 * 
 * @author Andrea Vacondio
 */
interface PrefixProcessor {

    /**
     * Process the input prefix String based on the input request returning the processed String
     * 
     * @param inputPrefix
     *            input prefix String
     * @param request
     *            name generation request. It can be null, the processor should handle it without throwing a NullPointerException.
     * @return the post processed inputPrefix
     */
    String process(String inputPrefix, NameGenerationRequest request);
}
