/*
 * Created on 25 gen 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli.model;

import org.sejda.conversion.InitialViewAdapter;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

/**
 * Specification for CLI arguments of a task that creates a collection of attachments
 * 
 * @author Andrea Vacondio
 *
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " portfolio")
public interface AttachmentsCollectionTaskCliArguments extends CliArgumentsWithPdfFileOutput {

    @Option(shortName = "i", description = "value for the initial view of the collection. {details, tiles, hidden}. If omitted it uses tiles (optional)", defaultValue = "tiles")
    InitialViewAdapter getInitialView();
}
