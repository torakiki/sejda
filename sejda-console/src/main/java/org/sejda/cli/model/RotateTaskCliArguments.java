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
package org.sejda.cli.model;

import org.sejda.cli.model.adapter.PageRotationAdapter;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Specifications for command line options of the Rotate task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " rotate")
public interface RotateTaskCliArguments extends CliArgumentsWithPdfAndDirectoryOutput, CliArgumentsWithPrefixableOutput {

    @Option(shortName = "r", description = "pages rotation. You can set pages rotation. Accepted string is "
            + "\"pages:rotationdegrees\" where pages can be one among 'all',"
            + "'odd', 'even' and where rotationdegrees can be '90', '180' or"
            + "'270'. Pages will be rotate clockwise (required)")
    PageRotationAdapter getPageRotation();
}
