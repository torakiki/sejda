/*
 * Created on Jun 30, 2011
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
package org.sejda.cli;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

//TODO: EW: review javadoc
/**
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = "sejda-console decrypt")
public interface EncryptCommandOptions extends CommandOptions {

    @Option(description = "compress output file (optional)")
    boolean isCompressed();

    @Option(shortName = "?", description = "prints usage to stdout; exits (optional)")
    boolean isHelp();
}
