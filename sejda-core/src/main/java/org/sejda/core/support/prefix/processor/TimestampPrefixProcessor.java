/*
 * Created on 01/lug/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.support.prefix.processor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.sejda.core.support.prefix.model.NameGenerationRequest;

/**
 * Process the input prefix replacing all the [TIMESTAMP] occurrences with the current timestamp.
 * 
 * @author Andrea Vacondio
 * 
 */
class TimestampPrefixProcessor implements PrefixProcessor {

    private static final String TIMESTAMP_REPLACE_RGX = "\\[TIMESTAMP\\]";
    private static final String DATE_PATTERN = "yyyyMMdd_HHmmssSS";

    @Override
    public String process(String inputPrefix, NameGenerationRequest request) {
        String retVal = inputPrefix;
        String timestamp = new SimpleDateFormat(DATE_PATTERN).format(new Date());
        return retVal.replaceAll(TIMESTAMP_REPLACE_RGX, timestamp);
    }

}
