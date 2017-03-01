/*
 * Copyright 2017 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import com.lexicalscope.jewel.cli.Option;
import org.sejda.conversion.ExistingOutputPolicyAdapter;
import org.sejda.conversion.FileOrDirectoryOutputAdapter;

public interface CliArgumentsWithFileOrDirectoryOutput extends TaskCliArguments {

    @Option(shortName = "o", description = "output file or directory (required)")
    FileOrDirectoryOutputAdapter getOutput();

    @Option(shortName = "j", description = "policy to use when an output file with the same name already exists. {overwrite, skip, fail, rename}. Default is 'fail' (optional)", defaultValue = "fail")
    ExistingOutputPolicyAdapter getExistingOutput();

    @Option(description = "overwrite existing output files (shorthand for -j overwrite) (optional)")
    boolean getOverwrite();
}
