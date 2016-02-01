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

import org.sejda.conversion.OptimizationAdapter;
import org.sejda.conversion.PdfFileSourceAdapter;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

@CommandLineInterface(application = TaskCliArguments.EXECUTABLE_NAME + " compress")
public interface CompressTaskCliArguments
        extends CliArgumentsWithPdfAndDirectoryOutput, CliArgumentsWithPrefixableOutput {

    @Option(shortName = "d", description = "image DPI. Defaults to 72. (Ex --imageDpi 140) (optional)", defaultValue = "72")
    Integer getImageDpi();

    @Option(shortName = "s", description = "image maximum width or height. Images larger than that will be resized down, keeping their aspect ratio. Defaults to 1280 (optional)", defaultValue = "1280")
    Integer getImageMaxWidthOrHeight();

    @Option(shortName = "q", description = "image JPEG quality. Defaults to 0.8. Ex: --imageQuality 0.3 (optional)", defaultValue = "0.8")
    Float getImageQuality();

    @Override
    @Option(shortName = "f", description = FILES_OPTION_DESCRIPTION_WHEN_EXPECTING_A_LIST_OPTIONAL)
    List<PdfFileSourceAdapter> getFiles();

    @Option(shortName = "z", description = "list of optimizations to perform. { discard_metadata, discard_outline, discard_threads, discard_spider_info, discard_piece_info, discard_mc_props, discard_alternate_images, compress_images, discard_unused_images, discard_struct_tree, discard_thumbnails }. If omitted it performs all the optimizations except discard_outline (optional)")
    List<OptimizationAdapter> getOptimizations();

    boolean isOptimizations();
}
