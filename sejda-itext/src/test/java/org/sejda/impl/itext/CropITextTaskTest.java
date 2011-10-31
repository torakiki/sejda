/*
 * Created on 10/set/2011
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
package org.sejda.impl.itext;

import org.sejda.core.service.CropTaskTest;
import org.sejda.impl.itext.CropTask;
import org.sejda.model.parameter.CropParameters;
import org.sejda.model.task.Task;

/**
 * @author Andrea Vacondio
 * 
 */
public class CropITextTaskTest extends CropTaskTest {

    public Task<CropParameters> getTask() {
        return new CropTask();
    }

}
