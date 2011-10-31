/*
 * Created on Jul 1, 2011
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sejda.model.parameter.SplitBySizeParameters;

/**
 * Tests for the SplitBySizeTask command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class SplitBySizeTaskTest extends AbstractTaskTest {

    public SplitBySizeTaskTest() {
        super(TestableTask.SPLIT_BY_SIZE);
    }

    @Test
    public void size_Specified() {
        SplitBySizeParameters parameters = defaultCommandLine().with("-s", "1234567890123456789").invokeSejdaConsole();
        assertEquals(1234567890123456789L, parameters.getSizeToSplitAt());
    }

    @Test
    public void mandatoryParams() {
        defaultCommandLine().without("-s").assertConsoleOutputContains("Option is mandatory: --size");
    }
}
