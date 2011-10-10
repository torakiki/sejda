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
package org.sejda.cli.transformer;

import org.sejda.cli.model.EncryptTaskCliArguments;
import org.sejda.core.manipulation.model.parameter.EncryptParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the Encrypt task command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class EncryptCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<EncryptTaskCliArguments, EncryptParameters> {

    /**
     * Transforms {@link EncryptTaskCliArguments} to {@link EncryptParameters}
     * 
     * @param taskCliArguments
     * @return
     */
    public EncryptParameters toTaskParameters(EncryptTaskCliArguments taskCliArguments) {
        EncryptParameters parameters = new EncryptParameters(taskCliArguments.getEncryptionType().getEnumValue());
        populateAbstractParameters(parameters, taskCliArguments);
        populateSourceParameters(parameters, taskCliArguments);
        parameters.setOutputPrefix(taskCliArguments.getOutputPrefix());
        parameters.setOwnerPassword(taskCliArguments.getAdminstratorPassword());
        parameters.setUserPassword(taskCliArguments.getUserPassword());
        return parameters;
    }
}
