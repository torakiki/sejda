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
import org.sejda.cli.model.*;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.base.*;
import org.sejda.model.parameter.image.AbstractPdfToMultipleImageParameters;
import org.sejda.model.parameter.image.AbstractPdfToSingleImageParameters;
import org.sejda.model.parameter.image.PdfToImageParameters;

/**
 * @author Eduard Weissmann
 * 
 */
public class BaseCliArgumentsTransformer {

    protected void populateOutputPrefix(MultipleOutputTaskParameters parameters,
            CliArgumentsWithPrefixableOutput taskCliArguments) {
        parameters.setOutputPrefix(taskCliArguments.getOutputPrefix());
    }

    protected void populateOutputPrefix(SingleOrMultipleOutputTaskParameters parameters,
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

    protected void populateOutputTaskParameters(SingleOrMultipleOutputTaskParameters parameters,
                                                CliArgumentsWithPdfAndFileOrDirectoryOutput taskCliArguments) {
        populateCommonMultipleOutputParameters(parameters, taskCliArguments);

    }

    protected void populateOutputTaskParameters(SingleOrMultipleOutputTaskParameters parameters,
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
        if (taskCliArguments.getOverwrite()) {
            parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        } else {
            parameters.setExistingOutputPolicy(taskCliArguments.getExistingOutput().getEnumValue());
        }
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
        populateCommonParameters(parameters, taskCliArguments);
    }

    /**
     * Populates common parameters for a task with output image files into a directory
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateAbstractMultipleImageParameters(AbstractPdfToMultipleImageParameters parameters,
            CliArgumentsWithImageAndDirectoryOutput taskCliArguments) {
        populateCommonParameters(parameters, taskCliArguments);
        populateCommonMultipleOutputParameters(parameters, taskCliArguments);
        populateCommonImageOutputParameters(parameters, taskCliArguments);
    }

    /**
     * Populates common parameters for a task whose output can be optimized
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateOptimizableOutputParameters(OptimizableOutputTaskParameters parameters,
            CliArgumentsWithOptimizableOutput taskCliArguments) {
        parameters.setOptimizationPolicy(taskCliArguments.getOptimize().getEnumValue());
    }

    /**
     * Populates common parameters for a task where the output outline can be discarded
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateDiscardableOutlineParameters(DiscardableOutlineTaskParameters parameters,
            CliArgumentWithDiscardableOutline taskCliArguments) {
        parameters.discardOutline(taskCliArguments.isDiscardOutline());
    }

    /**
     * Populates common parameters for a task with output a single image file
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateAbstractSingleImageParameters(AbstractPdfToSingleImageParameters parameters,
            CliArgumentsWithImageFileOutput taskCliArguments) {
        populateCommonParameters(parameters, taskCliArguments);
        parameters.setOutput(taskCliArguments.getOutput().getFileOutput());
        if (taskCliArguments.getOverwrite()) {
            parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        } else {
            parameters.setExistingOutputPolicy(taskCliArguments.getExistingOutput().getEnumValue());
        }
        populateCommonImageOutputParameters(parameters, taskCliArguments);
    }

    private void populateCommonImageOutputParameters(PdfToImageParameters parameters,
            CliArgumentsWithImageOutput taskCliArguments) {
        if (taskCliArguments.isResolution()) {
            parameters.setResolutionInDpi(taskCliArguments.getResolution());
        }
    }

    private void populateCommonPdfOutputParameters(AbstractPdfOutputParameters parameters,
            CliArgumentsWithPdfOutput taskCliArguments) {
        parameters.setVersion(taskCliArguments.getPdfVersion().getVersion());
    }

    /**
     * Populates attributes common to every {@link AbstractParameters}
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateCommonParameters(AbstractParameters parameters, TaskCliArguments taskCliArguments) {
        parameters.setLenient(taskCliArguments.isLenient());
    }

    /**
     * Populates pdf source parameters for tasks that support <i>more than</i> one input file
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateSourceParameters(MultiplePdfSourceTaskParameters parameters,
            MultiplePdfSourceTaskCliArguments taskCliArguments) {
        taskCliArguments.getFiles().stream().flatMap(a -> a.getPdfFileSources().stream())
                .forEach(parameters::addSource);
    }

    /**
     * Populates pdf source parameters for tasks that support <i>more than</i> one input file
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateSourceParameters(MultiplePdfSourceTaskParameters parameters,
            MultipleOptionalPdfSourceTaskCliArguments taskCliArguments) {
        taskCliArguments.getFiles().stream().flatMap(a -> a.getPdfFileSources().stream())
                .forEach(parameters::addSource);
    }

    /**
     * Populates pdf source parameters for tasks that support <i>only</i> one input file
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateSourceParameters(SinglePdfSourceTaskParameters parameters,
            SinglePdfSourceTaskCliArguments taskCliArguments) {
        if (taskCliArguments.getFiles().size() != 1) {
            throw new ArgumentValidationException(
                    "Only one input file expected, received " + taskCliArguments.getFiles().size());
        }
        parameters.setSource(taskCliArguments.getFiles().get(0).getPdfFileSource());
    }

    /**
     * Populates output directory and existing output policy for tasks with multiple output
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateCommonMultipleOutputParameters(MultipleOutputTaskParameters parameters,
            CliArgumentsWithDirectoryOutput taskCliArguments) {
        parameters.setOutput(taskCliArguments.getOutput().getPdfDirectoryOutput());
        parameters.setExistingOutputPolicy(taskCliArguments.getExistingOutput().getEnumValue());
        if(taskCliArguments.getOverwrite()){
            parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        }
    }

    protected void populateCommonMultipleOutputParameters(SingleOrMultipleOutputTaskParameters parameters,
                                                          CliArgumentsWithFileOrDirectoryOutput taskCliArguments) {
        parameters.setOutput(taskCliArguments.getOutput().getOutput());
        parameters.setExistingOutputPolicy(taskCliArguments.getExistingOutput().getEnumValue());
        if(taskCliArguments.getOverwrite()) {
            parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        }
    }

    protected void populateCommonMultipleOutputParameters(SingleOrMultipleOutputTaskParameters parameters,
                                                          CliArgumentsWithDirectoryOutput taskCliArguments) {
        parameters.setOutput(taskCliArguments.getOutput().getPdfFileOrDirectoryOutput());
        parameters.setExistingOutputPolicy(taskCliArguments.getExistingOutput().getEnumValue());
        if(taskCliArguments.getOverwrite()){
            parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        }
    }

}
