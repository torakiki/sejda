/*
 * Created on Jul 1, 2011
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
package org.sejda.cli.transformer;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.sejda.cli.model.MergeTaskCliArguments;
import org.sejda.conversion.MultiplePdfMergeInputAdapter;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.parameter.MergeParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the Merge task command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class MergeCliArgumentsTransformer extends BaseCliArgumentsTransformer
        implements CommandCliArgumentsTransformer<MergeTaskCliArguments, MergeParameters> {

    /**
     * Transforms {@link MergeTaskCliArguments} to {@link MergeParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    @Override
    public MergeParameters toTaskParameters(MergeTaskCliArguments taskCliArguments) {
        MergeParameters parameters = new MergeParameters();
        parameters.setAcroFormPolicy(taskCliArguments.getAcroForms().getEnumValue());
        parameters.setBlankPageIfOdd(taskCliArguments.isAddBlanks());
        parameters.setOutlinePolicy(taskCliArguments.getBookmarks().getEnumValue());
        parameters.setTableOfContentsPolicy(taskCliArguments.getToc().getEnumValue());
        parameters.setFilenameFooter(taskCliArguments.getFooter());
        populateAbstractParameters(parameters, taskCliArguments);
        populateOutputTaskParameters(parameters, taskCliArguments);

        MultiplePdfMergeInputAdapter mergeInputsAdapter = extractPdfMergeInputs(taskCliArguments);

        for (PdfMergeInput eachMergeInput : mergeInputsAdapter.getPdfMergeInputs()) {
            parameters.addInput(eachMergeInput);
        }

        return parameters;
    }

    private MultiplePdfMergeInputAdapter extractPdfMergeInputs(MergeTaskCliArguments taskCliArguments) {
        // input files can be specified in 3 ways: explicitly, via a folder or via a config file
        List<PdfFileSource> inputFiles = null;
        if (taskCliArguments.isDirectory()) {
            if (taskCliArguments.isMatchingRegEx()) {
                inputFiles = taskCliArguments.getDirectory().filter(taskCliArguments.getMatchingRegEx())
                        .getFileSourceList();
            } else {
                inputFiles = taskCliArguments.getDirectory().getFileSourceList();
            }

        } else if (taskCliArguments.isFiles()) {
            inputFiles = taskCliArguments.getFiles().stream().flatMap(a -> a.getPdfFileSources().stream())
                    .collect(Collectors.toList());
        } else if (taskCliArguments.isFilesListConfig()) {
            inputFiles = taskCliArguments.getFilesListConfig().getFileSourceList();
        }

        if (!BooleanUtils.or(new boolean[] { taskCliArguments.isDirectory(), taskCliArguments.isFiles(),
                taskCliArguments.isFilesListConfig() })) {
            throw new SejdaRuntimeException(
                    "No option given for input. Please use one of the following options: --directory --filesListConfig --file");
        }

        if (!BooleanUtils.xor(new boolean[] { taskCliArguments.isDirectory(), taskCliArguments.isFiles(),
                taskCliArguments.isFilesListConfig() })) {
            throw new SejdaRuntimeException(
                    "Too many options given for input. Please use only one of the following options: --directory --filesListConfig --file");
        }

        if (inputFiles == null) {
            throw new SejdaRuntimeException("No input files specified");
        }

        MultiplePdfMergeInputAdapter mergeInputsAdapter = new MultiplePdfMergeInputAdapter(inputFiles,
                taskCliArguments.getPageSelection().ranges());
        return mergeInputsAdapter;
    }
}
