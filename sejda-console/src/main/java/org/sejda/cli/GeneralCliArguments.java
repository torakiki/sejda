/*
 * Created on Jun 30, 2011
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

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;
import uk.co.flamingpenguin.jewel.cli.Unparsed;

/**
 * Specification for the general options of the command line interface<br/>
 * In a nutshell, those are:
 * <ul>
 * <li>print general help, detailing all possible commands supported, Eg: {@code sejda-console -h} or simply {@code sejda-console}</li>
 * <li>execute specific command help. Eg: {@code sejda-console -h command_name}</li>
 * <li>execute specific command. Eg: {@code sejda-console command_name command_options}</li>
 * </ul>
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = SejdaConsole.EXECUTABLE_NAME)
public interface GeneralCliArguments {

    // TODO: find a way to derive the commands supported from the CliCommand enumeration
    // TODO: stop printing [options] command to execute because user can think the order of params is different
    // TODO: get rid of this class completely
    // TODO: support -version, -license
    @Unparsed(name = "command to execute {[concat], [split],"
            + " [encrypt], [mix], [unpack], [setviewer], [slideshow], [decrypt], [rotate], [pagelabels]}")
    CliCommandAdapter getCommand();

    boolean isCommand();

    @Option(shortName = "h", description = "prints this usage information. Can be used to detail options for a command '-h command' (optional)")
    boolean isHelp();
}
