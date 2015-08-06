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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli.transformer;

import org.sejda.cli.exception.ArgumentValidationException;
import org.sejda.cli.model.AlternateMixTaskCliArguments;
import org.sejda.conversion.PdfFileSourceAdapter;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.parameter.AlternateMixParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the AlternateMix task command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class AlternateMixCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<AlternateMixTaskCliArguments, AlternateMixParameters> {

    /**
     * Transforms {@link AlternateMixTaskCliArguments} to {@link AlternateMixParameters}
     * 
     * @param taskCliArguments
     * @return populated parameters
     */
    public AlternateMixParameters toTaskParameters(AlternateMixTaskCliArguments taskCliArguments) {
        if (taskCliArguments.getFiles().size() != 2) {
            throw new ArgumentValidationException("Please specify two files as input parameters, found "
                    + taskCliArguments.getFiles().size());
        }

        PdfFileSourceAdapter f1 = taskCliArguments.getFiles().get(0);
        PdfFileSourceAdapter f2 = taskCliArguments.getFiles().get(1);

        PdfMixInput input1 = new PdfMixInput(f1.getPdfFileSource(), taskCliArguments.isReverseFirst(),
                taskCliArguments.getFirstStep());

        PdfMixInput input2 = new PdfMixInput(f2.getPdfFileSource(), taskCliArguments.isReverseSecond(),
                taskCliArguments.getSecondStep());

        AlternateMixParameters parameters = new AlternateMixParameters(input1, input2);
        populateOutputTaskParameters(parameters, taskCliArguments);
        populateAbstractParameters(parameters, taskCliArguments);
        return parameters;
    }
}
