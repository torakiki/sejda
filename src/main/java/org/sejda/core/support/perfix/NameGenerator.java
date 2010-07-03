/*
 * Created on 03/lug/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.support.perfix;

import static org.sejda.core.support.perfix.NameGenerationRequest.nameRequest;

import org.apache.commons.lang.StringUtils;
import org.sejda.core.Sejda;

/**
 * Component used to generate the output name for a manipulation given the original name and the input prefix (if any);
 * 
 * @author Andrea Vacondio
 * @see PrefixType
 */
public final class NameGenerator {

    private String prefix;
    private String originalName;
    private PrefixTypesChain prefixTypesChain;

    private NameGenerator(String prefix, String originalName) {
        this.prefix = prefix;
        this.prefixTypesChain = new PrefixTypesChain(prefix);
        originalName(originalName);

    }

    /**
     * @param prefix
     * @param originalName
     * @return a new instance of a NameGenrator
     */
    public static NameGenerator nameGenerator(String prefix, String originalName) {
        return new NameGenerator(prefix, originalName);
    }

    /**
     * Sets the original name
     * 
     * @param originalName
     */
    private void originalName(String originalName) {
        if (StringUtils.isNotBlank(originalName)) {
            // check if the filename contains '.' and it's at least in second position (Ex. a.pdf)
            if (originalName.lastIndexOf('.') > 1) {
                this.originalName = originalName.substring(0, originalName.lastIndexOf('.'));
            } else {
                this.originalName = originalName;
            }
        }
    }

    /**
     * @param request
     *            parameters used to generate the name. It can be null.
     * @return generates a new name from the given request
     */
    public String generate(NameGenerationRequest request) {
        String generatedName = prefixTypesChain.process(prefix, preProcessRequest(request));
        return ensurePdfExtension(generatedName);
    }

    /**
     * pre process the request ensuring a not null request is returned
     * 
     * @param request
     * @return a not null request with the originalName parameter populated
     */
    private NameGenerationRequest preProcessRequest(NameGenerationRequest request) {
        NameGenerationRequest retVal = request;
        if (request == null) {
            retVal = nameRequest();
        }
        return retVal.originalName(originalName);
    }

    private String ensurePdfExtension(String name) {
        if (!name.endsWith(Sejda.PDF_EXTENSION)) {
            return String.format("%s.%s", name, Sejda.PDF_EXTENSION);
        }
        return name;
    }
}
