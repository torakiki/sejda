/*
 * Created on Jul 10, 2011
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

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

/**
 * Specifications for command line options of the AlternateMix task
 * 
 * @author Eduard Weissmann
 * 
 */
@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " alternatemix")
public interface AlternateMixTaskCliArguments extends CliArgumentsWithPdfFileOutput, MultiplePdfSourceTaskCliArguments {

    @Option(description = "reverse first input file (optional)")
    boolean isReverseFirst();

    @Option(description = "reverse second input file (optional)")
    boolean isReverseSecond();

    @Option(shortName = "y", description = "step for the alternate mix of the first file (default is 1) (optional)", defaultValue = "1")
    int getFirstStep();

    @Option(shortName = "z", description = "step for the alternate mix of the second file (default is 1) (optional)", defaultValue = "1")
    int getSecondStep();
}
