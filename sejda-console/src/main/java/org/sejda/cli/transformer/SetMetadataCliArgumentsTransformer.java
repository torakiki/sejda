/*
 * Created on Sep 14, 2011
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

import org.sejda.cli.exception.ArgumentValidationException;
import org.sejda.cli.model.SetMetadataTaskCliArguments;
import org.sejda.model.parameter.SetMetadataParameters;
import org.sejda.model.pdf.PdfMetadataKey;

/**
 * {@link CommandCliArgumentsTransformer} for the SetMetadata task command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class SetMetadataCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<SetMetadataTaskCliArguments, SetMetadataParameters> {

    /**
     * Transforms {@link SetMetadataTaskCliArguments} to {@link SetMetadataParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    public SetMetadataParameters toTaskParameters(SetMetadataTaskCliArguments taskCliArguments) {
        final SetMetadataParameters parameters = new SetMetadataParameters();
        if (taskCliArguments.isAuthor()) {
            parameters.put(PdfMetadataKey.AUTHOR, taskCliArguments.getAuthor());
        }
        if (taskCliArguments.isTitle()) {
            parameters.put(PdfMetadataKey.TITLE, taskCliArguments.getTitle());
        }
        if (taskCliArguments.isKeywords()) {
            parameters.put(PdfMetadataKey.KEYWORDS, taskCliArguments.getKeywords());
        }
        if (taskCliArguments.isSubject()) {
            parameters.put(PdfMetadataKey.SUBJECT, taskCliArguments.getSubject());
        }

        if (parameters.keySet().isEmpty()) {
            throw new ArgumentValidationException("Please specify at least one metadata option to be set");
        }

        populateAbstractParameters(parameters, taskCliArguments);
        populateSourceParameters(parameters, taskCliArguments);

        return parameters;
    }
}
