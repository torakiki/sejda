/*
 * Created on Oct 11, 2011
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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.sejda.core.exception.SejdaRuntimeException;

import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * 
 * Test verifying that short names are not repeated for each task cli interface
 * 
 * @author Eduard Weissmann
 * 
 */
// TODO: add a similar test that verifies that all options have (optional) or (required) in the description
public class ShortNamesUniqueTraitTest extends AcrossAllTasksTraitTest {

    public ShortNamesUniqueTraitTest(TestableTask testableTask) {
        super(testableTask);
    }

    @Test
    public void testShortNamesAreUnique() {
        Class<?> cliCommandClass = testableTask.getCorrespondingCliCommand().getCliArgumentsClass();
        Map<String, Method> shortNamesMapping = new HashMap<String, Method>();

        for (Method eachMethod : cliCommandClass.getMethods()) {
            final Option optionAnnotation = eachMethod.getAnnotation(Option.class);

            if (optionAnnotation == null) {
                continue;
            }

            final String[] shortNames = optionAnnotation.shortName();

            for (String eachShortName : shortNames) {
                if (StringUtils.isBlank(eachShortName)) {
                    continue;
                }
                if (shortNamesMapping.containsKey(eachShortName)) {
                    throw new SejdaRuntimeException(getTaskName() + " has duplicate short names: '" + eachShortName
                            + "' defined on " + eachMethod.getName() + "() and on "
                            + shortNamesMapping.get(eachShortName).getName() + "()");
                }
                shortNamesMapping.put(eachShortName, eachMethod);
            }
        }
    }
}
