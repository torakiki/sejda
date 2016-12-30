/*
 * Created on Oct 2, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import org.sejda.cli.model.PdfToSingleTiffTaskCliArguments;
import org.sejda.model.parameter.image.PdfToSingleTiffParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the PdfToSingleTiff task command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class PdfToSingleTiffCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<PdfToSingleTiffTaskCliArguments, PdfToSingleTiffParameters> {

    /**
     * Transforms {@link PdfToSingleTiffParameters} to {@link PdfToSingleTiffParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    @Override
    public PdfToSingleTiffParameters toTaskParameters(PdfToSingleTiffTaskCliArguments taskCliArguments) {
        PdfToSingleTiffParameters parameters = new PdfToSingleTiffParameters(taskCliArguments.getColorType()
                .getEnumValue());

        parameters.setCompressionType(taskCliArguments.getCompressionType().getEnumValue());

        populateSourceParameters(parameters, taskCliArguments);
        populateAbstractSingleImageParameters(parameters, taskCliArguments);

        return parameters;
    }
}
