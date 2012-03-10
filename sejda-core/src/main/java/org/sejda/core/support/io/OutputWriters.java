/*
 * Created on 18/ott/2011
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
package org.sejda.core.support.io;

/**
 * Provides factory methods for available output writers.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class OutputWriters {

    private OutputWriters() {
        // hide
    }

    /**
     * Factory method for a {@link SingleOutputWriter}.
     * 
     * @param overwrite
     *            true if the writer should overwrite existing output
     * @return a new instace of the default {@link SingleOutputWriter}
     */
    public static SingleOutputWriter newSingleOutputWriter(boolean overwrite) {
        return new DefaultSingleOutputWriter(overwrite);
    }

    /**
     * Factory method for a {@link MultipleOutputWriter}.
     * 
     * @param overwrite
     *            true if the writer should overwrite existing output
     * @return a new instace of the default {@link MultipleOutputWriter}
     */
    public static MultipleOutputWriter newMultipleOutputWriter(boolean overwrite) {
        return new DefaultMultipleOutputWriter(overwrite);
    }
}
