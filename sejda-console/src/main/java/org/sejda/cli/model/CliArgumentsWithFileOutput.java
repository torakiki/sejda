/*
 * Created on Oct 2, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli.model;

import org.sejda.conversion.ExistingOutputPolicyAdapter;
import org.sejda.conversion.FileOutputAdapter;

import com.lexicalscope.jewel.cli.Option;

/**
 * 
 * Base interface for specifying of the command line interface for tasks that have output configured as a file
 * 
 * @author Eduard Weissmann
 * 
 */
public interface CliArgumentsWithFileOutput extends TaskCliArguments {

    @Option(shortName = "o", description = "output file (required)")
    FileOutputAdapter getOutput();

    @Option(description = "overwrite existing output file (optional)")
    boolean getOverwrite();

    @Option(shortName = "j", hidden = true, description = "policy to use when an output file with the same name already exists. {overwrite, fail, rename}. Default is 'fail' (optional)", defaultValue = "fail")
    ExistingOutputPolicyAdapter getExistingOutput();

}
