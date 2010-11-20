/*
 * Created on 19/giu/2010
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
package org.sejda.core.support.io.model;

import java.io.File;

/**
 * DSL interface used to expose getters for the fully populated {@link FileOutput}
 * 
 * @see FileOutput
 * @author Andrea Vacondio
 * 
 */
public interface PopulatedFileOutput {

    /**
     * @return the temporary file
     */
    File getFile();

    /**
     * @return the new name
     */
    String getName();
}
