/*
 * Created on 12/mag/2010
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
package org.sejda.model.task;

import javax.validation.Valid;

import org.junit.Ignore;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.output.TaskOutput;
import org.sejda.model.parameter.base.AbstractPdfOutputParameters;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;

/**
 * @author Andrea Vacondio
 * 
 */
@Ignore
public class TestTaskParameter extends AbstractPdfOutputParameters implements SingleOutputTaskParameters {

    @Valid
    private SingleTaskOutput<?> output;

    public TaskOutput<?> getOutput() {
        return output;
    }

    public String getOutputName() {
        return "test";
    }

    public void setOutput(SingleTaskOutput<?> output) {
        this.output = output;
    }

}
