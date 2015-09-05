/*
 * Created on Sep 14, 2011
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
    @Override
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
        populateOutputTaskParameters(parameters, taskCliArguments);

        return parameters;
    }
}
