/*
 * Created on Oct 2, 2011
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

import org.sejda.cli.model.adapter.PdfVersionAdapter;

import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Trait for cli tasks that output pdf files
 * 
 * @author Eduard Weissmann
 * 
 */
public interface CliArgumentsWithPdfOutput extends TaskCliArguments {

    @Option(description = "compress output file (optional)")
    boolean getCompressed();

    @Option(shortName = "v", description = "pdf version of the output document/s {2, 3, 4, 5, 6 or 7}. Default is 6. (optional)", defaultValue = "6")
    PdfVersionAdapter getPdfVersion();
}
