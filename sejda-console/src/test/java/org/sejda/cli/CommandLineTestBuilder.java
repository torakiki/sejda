/*
 * Created on Jul 10, 2011
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
package org.sejda.cli;

import java.util.HashMap;
import java.util.Map;

/**
 * Build for command line arguments, used in tests
 * 
 * @author Eduard Weissmann
 * 
 */
public class CommandLineTestBuilder {

    private final String taskName;
    private final Map<String, String> optionsAndValues = new HashMap<String, String>();

    public CommandLineTestBuilder(String taskName) {
        this.taskName = taskName;
        defaultInputAndOutput();
    }

    /**
     * Populates default inputs and output
     */
    private void defaultInputAndOutput() {
        with("-f", "inputs/input.pdf");
        with("-o", "./outputs");
    }

    /**
     * Adds a new option and it's value to the command
     * 
     * @param option
     * @param value
     * @return
     */
    public CommandLineTestBuilder with(String option, String value) {
        optionsAndValues.put(option, value);
        return this;
    }

    public CommandLineTestBuilder with(String option) {
        optionsAndValues.put(option, null);
        return this;
    }

    /**
     * Builds a command line string, that calls the task specified as input, using the collected options & values
     * 
     * @param taskName
     * @return
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(taskName);

        for (Map.Entry<String, String> eachOptionAndValue : optionsAndValues.entrySet()) {
            result.append(" ").append(eachOptionAndValue.getKey());
            if (eachOptionAndValue.getValue() != null) {
                result.append(" ").append(eachOptionAndValue.getValue());
            }
        }

        return result.toString();
    }
}
