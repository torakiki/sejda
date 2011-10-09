/*
 * Created on Jul 10, 2011
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

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Specifications for command line options of the AlternateMix task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = SejdaConsole.EXECUTABLE_NAME + " alternatemix")
public interface AlternateMixTaskCliArguments extends CliArgumentsWithPdfFileOutput {

    @Option(description = "reverse first input file (optional)")
    boolean isReverseFirst();

    @Option(description = "reverse second input file (optional)")
    boolean isReverseSecond();

    @Option(description = "step for the alternate mix of the first file (default is 1) (optional)", defaultValue = "1")
    int getFirstStep();

    @Option(description = "step for the alternate mix of the second file (default is 1) (optional)", defaultValue = "1")
    int getSecondStep();

    // pdfsam-incompatibility {f1 and f2 are specified, vs now -f -f}
}
