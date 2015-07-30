/*
 * Created on Jul 11, 2011
 * Copyright 2011 by Nero Couvalli (angelthepunisher@gmail.com).
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
package org.sejda.impl.sambox;

import org.sejda.core.service.SetMetadataTaskTest;
import org.sejda.model.parameter.SetMetadataParameters;
import org.sejda.model.task.Task;

/**
 * set metadata task test for the pdfbox implementation
 *
 * @author Nero Couvalli
 *
 */
public class SetMetadataSamboxTaskTest extends SetMetadataTaskTest {

    public Task<SetMetadataParameters> getTask() {
        return new SetMetadataTask();
    }

}