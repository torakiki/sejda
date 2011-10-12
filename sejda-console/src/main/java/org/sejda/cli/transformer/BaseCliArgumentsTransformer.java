/*
 * Created on Jul 9, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.cli.transformer;

import org.sejda.cli.exception.ArgumentValidationException;
import org.sejda.cli.model.CliArgumentsWithImageAndDirectoryOutput;
import org.sejda.cli.model.CliArgumentsWithImageFileOutput;
import org.sejda.cli.model.CliArgumentsWithImageOutput;
import org.sejda.cli.model.CliArgumentsWithPdfAndDirectoryOutput;
import org.sejda.cli.model.CliArgumentsWithPdfFileOutput;
import org.sejda.cli.model.CliArgumentsWithPdfOutput;
import org.sejda.cli.model.CliArgumentsWithPrefixableOutput;
import org.sejda.cli.model.TaskCliArguments;
import org.sejda.cli.model.adapter.PdfFileSourceAdapter;
import org.sejda.core.manipulation.model.parameter.base.AbstractParameters;
import org.sejda.core.manipulation.model.parameter.base.AbstractPdfOutputParameters;
import org.sejda.core.manipulation.model.parameter.base.MultipleOutputTaskParameters;
import org.sejda.core.manipulation.model.parameter.base.MultiplePdfSourceTaskParameters;
import org.sejda.core.manipulation.model.parameter.base.SinglePdfSourceTaskParameters;
import org.sejda.core.manipulation.model.parameter.image.AbstractPdfToImageParameters;
import org.sejda.core.manipulation.model.parameter.image.AbstractPdfToMultipleImageParameters;
import org.sejda.core.manipulation.model.parameter.image.AbstractPdfToSingleImageParameters;

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
     * Populates common parameters for a task with output pdf files into a directory
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateAbstractParameters(AbstractPdfOutputParameters parameters,
            CliArgumentsWithPdfAndDirectoryOutput taskCliArguments) {
        parameters.setOutput(taskCliArguments.getOutput().getPdfDirectoryOutput());
        populateCommonPdfOutputParameters(parameters, taskCliArguments);
    }

    /**
     * Populates common parameters for a task with output a single pdf file
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateAbstractParameters(AbstractPdfOutputParameters parameters,
            CliArgumentsWithPdfFileOutput taskCliArguments) {
        parameters.setOutput(taskCliArguments.getOutput().getFileOutput());
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
        parameters.setOutput(taskCliArguments.getOutput().getPdfDirectoryOutput());
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
        populateCommonImageOutputParameters(parameters, taskCliArguments);
    }

    private void populateCommonImageOutputParameters(AbstractPdfToImageParameters parameters,
            CliArgumentsWithImageOutput taskCliArguments) {
        if (taskCliArguments.isResolution()) {
            parameters.setResolutionInDpi(taskCliArguments.getResolution());
        }

        // todo: hmmm, should populate also taskCliArguments.getColorType() but it's wired by constructor not setter... hmmm

        populateCommonAbstractParameters(parameters, taskCliArguments);
    }

    private void populateCommonAbstractParameters(AbstractParameters parameters, TaskCliArguments taskCliArguments) {
        parameters.setOverwrite(taskCliArguments.getOverwrite());
    }

    private void populateCommonPdfOutputParameters(AbstractPdfOutputParameters parameters,
            CliArgumentsWithPdfOutput taskCliArguments) {
        parameters.setCompress(taskCliArguments.getCompressed());
        parameters.setVersion(taskCliArguments.getPdfVersion().getEnumValue());

        populateCommonAbstractParameters(parameters, taskCliArguments);
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
    protected void populateSourceParameters(SinglePdfSourceTaskParameters parameters, TaskCliArguments taskCliArguments) {
        if (taskCliArguments.getFiles().size() != 1) {
            throw new ArgumentValidationException("Only one input file expected, received "
                    + taskCliArguments.getFiles().size());
        }
        parameters.setSource(taskCliArguments.getFiles().get(0).getPdfFileSource());
    }
}
