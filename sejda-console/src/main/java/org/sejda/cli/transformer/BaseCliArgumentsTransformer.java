/*
 * Created on Jul 9, 2011
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
package org.sejda.cli.transformer;

import org.sejda.cli.exception.ArgumentValidationException;
import org.sejda.cli.model.CliArgumentsWithDirectoryOutput;
import org.sejda.cli.model.CliArgumentsWithImageAndDirectoryOutput;
import org.sejda.cli.model.CliArgumentsWithImageFileOutput;
import org.sejda.cli.model.CliArgumentsWithImageOutput;
import org.sejda.cli.model.CliArgumentsWithPdfAndDirectoryOutput;
import org.sejda.cli.model.CliArgumentsWithPdfFileOutput;
import org.sejda.cli.model.CliArgumentsWithPdfOutput;
import org.sejda.cli.model.CliArgumentsWithPrefixableOutput;
import org.sejda.cli.model.TaskCliArguments;
import org.sejda.conversion.PdfFileSourceAdapter;
import org.sejda.model.parameter.base.AbstractPdfOutputParameters;
import org.sejda.model.parameter.base.MultipleOutputTaskParameters;
import org.sejda.model.parameter.base.MultiplePdfSourceTaskParameters;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;
import org.sejda.model.parameter.base.SinglePdfSourceTaskParameters;
import org.sejda.model.parameter.image.AbstractPdfToImageParameters;
import org.sejda.model.parameter.image.AbstractPdfToMultipleImageParameters;
import org.sejda.model.parameter.image.AbstractPdfToSingleImageParameters;

/**
 * @author Eduard Weissmann
 * 
 */
public class BaseCliArgumentsTransformer {

    protected void populateOutputPrefix(MultipleOutputTaskParameters parameters,
            CliArgumentsWithPrefixableOutput taskCliArguments) {
        parameters.setOutputPrefix(taskCliArguments.getOutputPrefix());
    }

    /**
     * Populates task output parameters for a task with output pdf files into a directory
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateOutputTaskParameters(MultipleOutputTaskParameters parameters,
            CliArgumentsWithPdfAndDirectoryOutput taskCliArguments) {
        populateCommonMultipleOutputParameters(parameters, taskCliArguments);

    }

    /**
     * Populates task output parameters for a task with output a single pdf file
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateOutputTaskParameters(SingleOutputTaskParameters parameters,
            CliArgumentsWithPdfFileOutput taskCliArguments) {
        parameters.setOutput(taskCliArguments.getOutput().getFileOutput());
        parameters.setExistingOutputPolicy(taskCliArguments.getExistingOutput().getEnumValue());
    }

    /**
     * Populate commons parameter for {@link AbstractPdfOutputParameters}s
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateAbstractParameters(AbstractPdfOutputParameters parameters,
            CliArgumentsWithPdfOutput taskCliArguments) {
        populateCommonPdfOutputParameters(parameters, taskCliArguments);
    }

    /**
     * Populates common parameters for a task with output image files into a directory
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateAbstractParameters(AbstractPdfToMultipleImageParameters parameters,
            CliArgumentsWithImageAndDirectoryOutput taskCliArguments) {
        populateCommonMultipleOutputParameters(parameters, taskCliArguments);
        populateCommonImageOutputParameters(parameters, taskCliArguments);
    }

    /**
     * Populates common parameters for a task with output a single image file
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateAbstractParameters(AbstractPdfToSingleImageParameters parameters,
            CliArgumentsWithImageFileOutput taskCliArguments) {
        parameters.setOutput(taskCliArguments.getOutput().getFileOutput());
        parameters.setExistingOutputPolicy(taskCliArguments.getExistingOutput().getEnumValue());
        populateCommonImageOutputParameters(parameters, taskCliArguments);
    }

    private void populateCommonImageOutputParameters(AbstractPdfToImageParameters parameters,
            CliArgumentsWithImageOutput taskCliArguments) {
        if (taskCliArguments.isResolution()) {
            parameters.setResolutionInDpi(taskCliArguments.getResolution());
        }
        if (taskCliArguments.isUserZoom()) {
            parameters.setUserZoom(taskCliArguments.getUserZoom());
        }

        // todo: hmmm, should populate also taskCliArguments.getColorType() but it's wired by constructor not setter... hmmm
    }

    private void populateCommonPdfOutputParameters(AbstractPdfOutputParameters parameters,
            CliArgumentsWithPdfOutput taskCliArguments) {
        parameters.setCompress(taskCliArguments.getCompressed());
        parameters.setVersion(taskCliArguments.getPdfVersion().getVersion());
    }

    /**
     * Populates pdf source parameters for tasks that support <i>more than</i> one input file
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateSourceParameters(MultiplePdfSourceTaskParameters parameters,
            TaskCliArguments taskCliArguments) {
        for (PdfFileSourceAdapter eachAdapter : taskCliArguments.getFiles()) {
            parameters.addSource(eachAdapter.getPdfFileSource());
        }
    }

    /**
     * Populates pdf source parameters for tasks that support <i>only</i> one input file
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateSourceParameters(SinglePdfSourceTaskParameters parameters,
            TaskCliArguments taskCliArguments) {
        if (taskCliArguments.getFiles().size() != 1) {
            throw new ArgumentValidationException(
                    "Only one input file expected, received " + taskCliArguments.getFiles().size());
        }
        parameters.setSource(taskCliArguments.getFiles().get(0).getPdfFileSource());
    }

    /**
     * Populates output directory and existing putput policy for tasks with multiple output
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateCommonMultipleOutputParameters(MultipleOutputTaskParameters parameters,
            CliArgumentsWithDirectoryOutput taskCliArguments) {
        parameters.setOutput(taskCliArguments.getOutput().getPdfDirectoryOutput());
        parameters.setExistingOutputPolicy(taskCliArguments.getExistingOutput().getEnumValue());
    }

}
