/*
 * Created on Sep 14, 2011
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

import java.util.List;

import org.sejda.conversion.PdfFileSourceAdapter;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

/**
 * Specifications for command line options of the SetMetadata task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " setmetadata")
public interface SetMetadataTaskCliArguments extends CliArgumentsWithPdfFileOutput {

    @Option(shortName = "t", description = "document title (optional)")
    String getTitle();

    boolean isTitle();

    @Option(shortName = "a", description = "document author (optional)")
    String getAuthor();

    boolean isAuthor();

    @Option(shortName = "s", description = "document subject (optional)")
    String getSubject();

    boolean isSubject();

    @Option(shortName = "k", description = "document keywords (optional)")
    String getKeywords();

    boolean isKeywords();

    // override default -f option that is described as expecting a list of files with a description stating that it is expecting a single file
    @Override
    @Option(shortName = "f", description = FILES_OPTION_DESCRIPTION_WHEN_EXPECTING_A_SINGLE_FILE)
    List<PdfFileSourceAdapter> getFiles();
}