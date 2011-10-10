/*
 * Created on Jul 10, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli.model.adapter;

import org.sejda.cli.exception.ArgumentValidationException;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.model.rotation.PageRotation;
import org.sejda.core.manipulation.model.rotation.Rotation;
import org.sejda.core.manipulation.model.rotation.RotationType;

/**
 * 
 * Adapter for {@link PageRotation}. Main role is to be a string-based constructor for the underlying model object
 * 
 * @author Eduard Weissmann
 * 
 */
public class PageRotationAdapter {
    private PageRotation pageRotation;

    public PageRotationAdapter(String input) {
        try {
            doParse(input);
        } catch (SejdaRuntimeException e) {
            throw new ArgumentValidationException("Unparsable page rotation definition: '" + input + "'. "
                    + e.getMessage(), e);
        }
    }

    /**
     * @param input
     */
    private void doParse(String input) {
        final String[] tokens = AdapterUtils.splitAndTrim(input);
        if (tokens.length < 2) {
            throw new ArgumentValidationException("Invalid input: '" + input
                    + "'. Expected format: 'pageDefinition:rotation'");
        }

        final String pageToken = tokens[0];
        final String rotationToken = tokens[1];

        final Rotation rotation = EnumUtils.valueOfSilently(Rotation.class, rotationToken);
        if (rotation == null) {
            throw new ArgumentValidationException("Unknown rotation: '" + rotationToken + "'");
        }

        RotationType rotationType = EnumUtils.valueOfSilently(RotationType.class, pageToken);
        Integer pageNumber = null;

        if (rotationType == null) {
            rotationType = RotationType.SINGLE_PAGE;
            pageNumber = AdapterUtils.parseIntSilently(pageToken);
        }

        if (rotationType.isSinglePage() && pageNumber == null) {
            throw new ArgumentValidationException("Unknown page definition: '" + pageToken + "'");
        }

        this.pageRotation = rotationType.isSinglePage() ? PageRotation.createSinglePageRotation(pageNumber, rotation)
                : PageRotation.createMultiplePagesRotation(rotation, rotationType);
    }

    /**
     * @return the pageRotation
     */
    public PageRotation getPageRotation() {
        return pageRotation;
    }
}
