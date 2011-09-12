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
import org.sejda.core.manipulation.model.parameter.SimpleSplitParameters;

/**
 * Tests for the SplitByPagesTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class SimpleSplitTaskTest extends AbstractTaskTest {

    public SimpleSplitTaskTest() {
        super(TestableTask.SIMPLE_SPLIT);
    }

    @Test
    public void pageSelection_ALL_PAGES() {
        SimpleSplitParameters parameters = defaultCommandLine().with("-m", "ALL_PAGES").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(1, 2, 3, 4, 5), parameters.getPages(5));
    }

    @Test
    public void pageSelection_ODD_PAGES() {
        SimpleSplitParameters parameters = defaultCommandLine().with("-m", "ODD_PAGES").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(1, 3, 5), parameters.getPages(5));
    }

    @Test
    public void pageSelection_EVEN_PAGES() {
        SimpleSplitParameters parameters = defaultCommandLine().with("-m", "EVEN_PAGES").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(2, 4), parameters.getPages(5));
    }

    @Test
    public void mandatoryParams() {
        defaultCommandLine().without("-m").assertConsoleOutputContains("Option is mandatory: --pageSelection");
    }
}
