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
package org.sejda.cli;

import org.sejda.cli.adapters.PdfFileSourceAdapter;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.model.parameter.base.AbstractPdfOutputParameters;
import org.sejda.core.manipulation.model.parameter.base.MultiplePdfSourceTaskParameters;
import org.sejda.core.manipulation.model.parameter.base.SinglePdfSourceTaskParameters;

/**
 * @author Eduard Weissmann
 * 
 */
public class BaseCliArgumentsTransformer {

    /**
     * Populates common parameters for a task with output as directory
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateAbstractParameters(AbstractPdfOutputParameters parameters,
            CliArgumentsWithDirectoryOutput taskCliArguments) {
        parameters.setOutput(taskCliArguments.getOutput().getPdfDirectoryOutput());
        populateCommonAbstractParameters(parameters, taskCliArguments);
    }

    /**
     * Populates common parameters for a task with output as directory
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateAbstractParameters(AbstractPdfOutputParameters parameters,
            CliArgumentsWithFileOutput taskCliArguments) {
        parameters.setOutput(taskCliArguments.getOutput().getFileOutput());
        populateCommonAbstractParameters(parameters, taskCliArguments);
    }

    private void populateCommonAbstractParameters(AbstractPdfOutputParameters parameters,
            TaskCliArguments taskCliArguments) {
        parameters.setCompress(taskCliArguments.getCompressed());
        parameters.setVersion(taskCliArguments.getPdfVersion());
        parameters.setOverwrite(taskCliArguments.getOverwrite());
    }

    /**
     * Populates pdf source parameters for tasks that support <i>more than</i> one input file
     * 
     * @param parameters
     * @param taskCliArguments
     */
    protected void populateSourceParameters(MultiplePdfSourceTaskParameters parameters, TaskCliArguments taskCliArguments) {
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
            throw new SejdaRuntimeException("Only one input file expected, received "
                    + taskCliArguments.getFiles().size());
        }
        parameters.setSource(taskCliArguments.getFiles().get(0).getPdfFileSource());
    }
}
