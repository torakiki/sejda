/*
 * Created on 17 gen 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.conversion;

import org.sejda.model.pdf.PdfVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter providing a smooth transition between single digit pdf versions (7 for pdf version 1.7) used in sejda-console v1.x to double digit pdf version (1.7 for pdf version 1.7)
 * used in sejda-console v2.x
 * 
 * @author Andrea Vacondio
 *
 */
public class PdfVersionAdapterWithFallback {
    private static final Logger LOG = LoggerFactory.getLogger(PdfVersionAdapterWithFallback.class);

    private PdfVersion version;

    public PdfVersionAdapterWithFallback(String userFriendlyName) {
        if (userFriendlyName.matches("[01234567]{1}")) {
            LOG.warn(
                    "Single digit PDF version '{}' is deprecated and will be removed in future releases, please use the two digits '1.{}' format",
                    userFriendlyName, userFriendlyName);
            this.version = new PdfVersionAdapter("1." + userFriendlyName).getEnumValue();
        } else {
            this.version = new PdfVersionAdapter(userFriendlyName).getEnumValue();
        }
    }

    public PdfVersion getVersion() {
        return version;
    }

}
