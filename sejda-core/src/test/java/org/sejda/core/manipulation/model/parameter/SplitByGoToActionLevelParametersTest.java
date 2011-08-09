/*
 * Created on 06/ago/2011
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
package org.sejda.core.manipulation.model.parameter;

import org.junit.Test;
import org.sejda.core.TestUtils;

/**
 * @author Andrea Vacondio
 * 
 */
public class SplitByGoToActionLevelParametersTest {

    @Test
    public void testEquals() {
        SplitByGoToActionLevelParameters eq1 = new SplitByGoToActionLevelParameters(10);
        SplitByGoToActionLevelParameters eq2 = new SplitByGoToActionLevelParameters(10);
        SplitByGoToActionLevelParameters eq3 = new SplitByGoToActionLevelParameters(10);
        SplitByGoToActionLevelParameters diff = new SplitByGoToActionLevelParameters(1);
        diff.setOutputPrefix("prefix");
        diff.setMatchingTitleRegEx("string");
        TestUtils.testEqualsAndHashCodes(eq1, eq2, eq3, diff);
    }
}
