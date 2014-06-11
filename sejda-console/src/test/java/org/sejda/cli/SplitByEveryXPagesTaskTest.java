/*
 * Created on 11/giu/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
import org.sejda.model.parameter.SplitByEveryXPagesParameters;

/**
 * Tests for the SplitByEveryXPageTask command line interface
 * 
 * @author Andrea Vacondio
 * 
 */
public class SplitByEveryXPagesTaskTest extends AbstractTaskTest {

    public SplitByEveryXPagesTaskTest() {
        super(TestableTask.SPLIT_BY_EVERY);
    }

    @Test
    public void pages_Specified() {
        SplitByEveryXPagesParameters parameters = defaultCommandLine().with("-n", "5").invokeSejdaConsole();
        assertContainsAll(Arrays.asList(5, 10, 15, 20, 25), parameters.getPages(27));
    }

    @Test
    public void mandatoryParams() {
        defaultCommandLine().without("-n").assertConsoleOutputContains("Option is mandatory: --pages");
    }
}
