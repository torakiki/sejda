/*
 * Created on 13/mar/2013
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import org.sejda.cli.model.PdfToJpegTaskCliArguments;
import org.sejda.model.parameter.image.PdfToJpegParameters;

/**
 * {@link CommandCliArgumentsTransformer} for the PdfToJpegTask task command line interface
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfToJpegCliArgumentsTransformer extends BaseCliArgumentsTransformer
        implements CommandCliArgumentsTransformer<PdfToJpegTaskCliArguments, PdfToJpegParameters> {

    /**
     * Transforms {@link PdfToJpegTaskCliArguments} to {@link PdfToJpegParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    @Override
    public PdfToJpegParameters toTaskParameters(PdfToJpegTaskCliArguments taskCliArguments) {
        PdfToJpegParameters parameters = new PdfToJpegParameters(taskCliArguments.getColorType().getEnumValue());
        populateSourceParameters(parameters, taskCliArguments);
        populateAbstractMultipleImageParameters(parameters, taskCliArguments);
        populateOutputPrefix(parameters, taskCliArguments);
        parameters.setQuality(taskCliArguments.getQuality());

        if (taskCliArguments.isPageSelection()) {
            parameters.addAllPageRanges(taskCliArguments.getPageSelection().getPageRangeSet());
        }

        return parameters;
    }

}
