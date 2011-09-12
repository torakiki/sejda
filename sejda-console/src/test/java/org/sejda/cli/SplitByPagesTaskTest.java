/*
 * Created on Sep 12, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import java.util.Arrays;

import org.junit.Test;
import org.sejda.core.manipulation.model.parameter.SplitByPagesParameters;

/**
 * Tests for the SplitByPagesTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class SplitByPagesTaskTest extends AbstractTaskTest {

    public SplitByPagesTaskTest() {
        super(TestableTask.SPLIT_BY_PAGES);
    }

    @Test
    public void pages_Specified() {
        SplitByPagesParameters parameters = defaultCommandLine().with("-n", "1 2 56 99 101").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(1, 2, 56, 99, 101), parameters.getPages(Integer.MAX_VALUE));
    }

    @Test
    public void mandatoryParams() {
        defaultCommandLine().without("-n").assertConsoleOutputContains("Option is mandatory: --pageNumbers");
    }
}
