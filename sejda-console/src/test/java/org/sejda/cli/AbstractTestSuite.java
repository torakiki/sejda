/*
 * Created on Aug 18, 2011
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

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.Collection;

/**
 * Base class for test suites, provides helper methods to ease testing
 * 
 * @author Eduard Weissmann
 * 
 */
public abstract class AbstractTestSuite {

    public <T> void assertContainsAll(Collection<T> expectedItems, Collection<T> actualItems) {
        for (T eachExpectedItem : expectedItems) {
            assertThat(actualItems, hasItem(eachExpectedItem));
        }
    }

}
