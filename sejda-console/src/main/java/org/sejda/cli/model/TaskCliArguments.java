/*
 * Created on Jun 30, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import java.util.List;

import org.sejda.conversion.PdfFileSourceAdapter;

import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Base interface for specifications of the command line interface for {@link org.sejda.model.task.Task}s
 * 
 * @author Eduard Weissmann
 * 
 */
public interface TaskCliArguments {
    String EXECUTABLE_NAME = "sejda-console";
    String FILES_OPTION_DESCRIPTION_WHEN_EXPECTING_A_LIST = "pdf files to operate on: a list of existing pdf files (EX. -f /tmp/file1.pdf or -f /tmp/password_protected_file2.pdf:secret123) (required)";
    String FILES_OPTION_DESCRIPTION_WHEN_EXPECTING_A_LIST_OPTIONAL = "pdf files to operate on: a list of existing pdf files (EX. -f /tmp/file1.pdf or -f /tmp/password_protected_file2.pdf:secret123) (optional)";
    String FILES_OPTION_DESCRIPTION_WHEN_EXPECTING_A_SINGLE_FILE = "pdf file to operate on: a single pdf file (EX. -f /tmp/file1.pdf or -f /tmp/password_protected_file2.pdf:secret123) (required)";

    @Option(shortName = "f", description = FILES_OPTION_DESCRIPTION_WHEN_EXPECTING_A_LIST)
    List<PdfFileSourceAdapter> getFiles();

    @Option(description = "overwrite existing output file (optional)")
    boolean getOverwrite();

    @Option(shortName = "h", description = "prints usage information. Can be used to detail options for a command '-h command' (optional)")
    boolean isHelp();
}
