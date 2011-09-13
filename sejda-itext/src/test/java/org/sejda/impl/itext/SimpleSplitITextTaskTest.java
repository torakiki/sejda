/*
 * Created on 29/lug/2011
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
package org.sejda.impl.itext;

import org.sejda.core.manipulation.model.parameter.SimpleSplitParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.service.SimpleSplitTaskTest;
import org.sejda.impl.itext.SplitByPageNumbersTask;

/**
 * @author Andrea Vacondio
 * 
 */
public class SimpleSplitITextTaskTest extends SimpleSplitTaskTest {

    public Task<SimpleSplitParameters> getTask() {
        return new SplitByPageNumbersTask<SimpleSplitParameters>();
    }

}
