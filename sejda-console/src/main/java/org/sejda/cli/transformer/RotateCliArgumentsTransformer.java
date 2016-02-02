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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sejda.cli.exception.ArgumentValidationException;
import org.sejda.cli.model.RotateTaskCliArguments;
import org.sejda.conversion.RotationAdapter;
import org.sejda.model.parameter.RotateParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PredefinedSetOfPages;
import org.sejda.model.rotation.Rotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link CommandCliArgumentsTransformer} for the Rotate task command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class RotateCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<RotateTaskCliArguments, RotateParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(RotateCliArgumentsTransformer.class);

    /**
     * Transforms {@link RotateTaskCliArguments} to {@link RotateParameters}
     * 
     * @param taskCliArguments
     * @return populated task parameters
     */
    @Override
    public RotateParameters toTaskParameters(RotateTaskCliArguments taskCliArguments) {
        RotateParameters parameters;
        Rotation rotation = Rotation.DEGREES_0;

        if(!taskCliArguments.isRotation() && !taskCliArguments.isPageRotations()) {
            throw new ArgumentValidationException(
                    "Please specify at least one option that defines rotation: either -r or -k");
        }

        if(taskCliArguments.isRotation()){
            rotation = taskCliArguments.getRotation().getEnumValue();
        }

        if (taskCliArguments.isPredefinedPages()
                && taskCliArguments.getPredefinedPages().getEnumValue() != PredefinedSetOfPages.NONE) {
            parameters = new RotateParameters(rotation, taskCliArguments.getPredefinedPages().getEnumValue());
        } else if (taskCliArguments.isPageSelection()) {
            parameters = new RotateParameters(rotation, PredefinedSetOfPages.NONE);
            Set<PageRange> pageRanges = taskCliArguments.getPageSelection().getPageRangeSet();
            if(taskCliArguments.isPageRotations()) {
                List<RotationAdapter> pageRotations = taskCliArguments.getPageRotations();
                Iterator<RotationAdapter> pageRotationsIterator = pageRotations.iterator();

                for (PageRange range : pageRanges) {
                    if (pageRotationsIterator.hasNext()) {
                        Rotation pageRotation = pageRotationsIterator.next().getEnumValue();
                        LOG.debug("Adding " + range.toString() + " and " + pageRotation);
                        parameters.addPageRange(range, pageRotation);
                    } else {
                        LOG.debug("Adding " + range.toString());
                        parameters.addPageRange(range);
                    }
                }

            } else {
                LOG.debug("Adding add pageRanges");
                parameters.addAllPageRanges(pageRanges);
            }

        } else {
            throw new ArgumentValidationException(
                    "Please specify at least one option that defines pages to be rotated: either -s or -m");
        }

        populateAbstractParameters(parameters, taskCliArguments);
        populateSourceParameters(parameters, taskCliArguments);
        populateOutputTaskParameters(parameters, taskCliArguments);
        populateOutputPrefix(parameters, taskCliArguments);
        return parameters;
    }
}
