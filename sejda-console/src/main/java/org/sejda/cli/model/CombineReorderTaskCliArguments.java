/*
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

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;

@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " combinereorder")
public interface CombineReorderTaskCliArguments
        extends CliArgumentsWithPdfFileOutput, MultiplePdfSourceTaskCliArguments {

    @Option(shortName = "n", description = "pages in expected order, indexed by their source file, with optional rotation, as in 'fileIndex:pageNumber:rotationDegrees'. (Ex --pages 0:100 1:50:270 denotes page 100 from the first file and page 50 from the second file specified in --files, rotated 270 degrees clockwise.) (required)")
    List<String> getPages();
}
