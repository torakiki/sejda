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
package org.sejda.cli;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.model.parameter.SetMetadataParameters;
import org.sejda.core.manipulation.model.pdf.PdfMetadataKey;

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
     * @return
     */
    public SetMetadataParameters toTaskParameters(SetMetadataTaskCliArguments taskCliArguments) {
        final SetMetadataParameters parameters = new SetMetadataParameters();
        Map<PdfMetadataKey, String> metadataProps = new HashMap<PdfMetadataKey, String>(PdfMetadataKey.values().length);
        metadataProps.put(PdfMetadataKey.AUTHOR, taskCliArguments.getAuthor());
        metadataProps.put(PdfMetadataKey.TITLE, taskCliArguments.getTitle());
        metadataProps.put(PdfMetadataKey.KEYWORDS, taskCliArguments.getKeywords());
        metadataProps.put(PdfMetadataKey.SUBJECT, taskCliArguments.getSubject());
        metadataProps.put(PdfMetadataKey.CREATOR, taskCliArguments.getCreator());

        for (Entry<PdfMetadataKey, String> eachEntry : metadataProps.entrySet()) {
            if (!StringUtils.isBlank(eachEntry.getValue())) {
                parameters.put(eachEntry.getKey(), eachEntry.getValue());
            }
        }

        if (parameters.keySet().isEmpty()) {
            throw new SejdaRuntimeException("Please specify at least one metadata option to be set");
        }

        populateAbstractParameters(parameters, taskCliArguments);
        populateSourceParameters(parameters, taskCliArguments);

        return parameters;
    }
}
