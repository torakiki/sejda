/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
import org.sejda.core.manipulation.model.input.PdfMixInput;
import org.sejda.core.manipulation.model.parameter.AlternateMixParameters;

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
     * @return
     */
    public AlternateMixParameters toTaskParameters(AlternateMixTaskCliArguments taskCliArguments) {
        if (taskCliArguments.getFiles().size() != 2) {
            throw new SejdaRuntimeException("Please specify two files as input parameters, using -f file1 file2");
        }

        PdfFileSourceAdapter f1 = taskCliArguments.getFiles().get(0);
        PdfFileSourceAdapter f2 = taskCliArguments.getFiles().get(1);

        PdfMixInput input1 = new PdfMixInput(f1.getPdfFileSource(), taskCliArguments.isReverseFirst(),
                taskCliArguments.getStepFirst());

        PdfMixInput input2 = new PdfMixInput(f2.getPdfFileSource(), taskCliArguments.isReverseSecond(),
                taskCliArguments.getStepSecond());

        AlternateMixParameters parameters = new AlternateMixParameters(input1, input2, null);
        populateAbstractParameters(parameters, taskCliArguments);
        return parameters;
    }
}
