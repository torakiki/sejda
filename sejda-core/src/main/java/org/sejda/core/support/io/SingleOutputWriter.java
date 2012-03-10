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
package org.sejda.core.support.io;

import org.sejda.core.support.io.model.PopulatedFileOutput;
import org.sejda.model.output.TaskOutputDispatcher;

/**
 * Statefull component responsible for writing multiple task generated files to a task output.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface SingleOutputWriter extends TaskOutputDispatcher {

    /**
     * Sets the given file output (typically a temporary file) as the output ready to be written.
     * 
     * @param fileOutput
     */
    void setOutput(PopulatedFileOutput fileOutput);
}
