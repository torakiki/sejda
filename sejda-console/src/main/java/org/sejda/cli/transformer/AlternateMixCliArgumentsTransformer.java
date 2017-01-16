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

import org.sejda.cli.exception.ArgumentValidationException;
import org.sejda.cli.model.AlternateMixTaskCliArguments;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.parameter.AlternateMixMultipleInputParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the AlternateMix task command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class AlternateMixCliArgumentsTransformer extends BaseCliArgumentsTransformer
        implements CommandCliArgumentsTransformer<AlternateMixTaskCliArguments, AlternateMixMultipleInputParameters> {

    /**
     * Transforms {@link AlternateMixTaskCliArguments} to {@link AlternateMixMultipleInputParameters}
     * 
     * @param taskCliArguments
     * @return populated parameters
     */
    @Override
    public AlternateMixMultipleInputParameters toTaskParameters(AlternateMixTaskCliArguments taskCliArguments) {
        List<PdfFileSource> sources = taskCliArguments.getFiles().stream().flatMap(a -> a.getPdfFileSources().stream())
                .collect(Collectors.toList());
        if (sources.size() != 2) {
            throw new ArgumentValidationException(
                    "Please specify two files as input parameters, found " + taskCliArguments.getFiles().size());
        }

        AlternateMixMultipleInputParameters parameters = new AlternateMixMultipleInputParameters();
        parameters.addInput(
                new PdfMixInput(sources.get(0), taskCliArguments.isReverseFirst(), taskCliArguments.getFirstStep()));
        parameters.addInput(
                new PdfMixInput(sources.get(1), taskCliArguments.isReverseSecond(), taskCliArguments.getSecondStep()));
        populateOutputTaskParameters(parameters, taskCliArguments);
        populateAbstractParameters(parameters, taskCliArguments);
        return parameters;
    }
}
