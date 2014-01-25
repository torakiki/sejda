/*
 * Created on Aug 22, 2011
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

import org.sejda.conversion.PdfDirectoryOutputAdapter;

import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * 
 * Base interface for specifying of the command line interface for tasks that have output configured as a directory
 * 
 * @author Eduard Weissmann
 * 
 */
public interface CliArgumentsWithDirectoryOutput extends TaskCliArguments {

    @Option(shortName = "o", description = "output directory (required)")
    PdfDirectoryOutputAdapter getOutput();
}
