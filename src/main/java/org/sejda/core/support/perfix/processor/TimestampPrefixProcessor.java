/*
 * Created on 01/lug/2010
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
package org.sejda.core.support.perfix.processor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.sejda.core.support.perfix.model.NameGenerationRequest;

/**
 * Process the input prefix replacing all the [TIMESTAMP] occurrences with the current timestamp.
 * 
 * @author Andrea Vacondio
 * 
 */
public class TimestampPrefixProcessor implements PrefixProcessor {

    private static final String TIMESTAMP_REPLACE_RGX = "\\[TIMESTAMP\\]";
    private static final String DATE_PATTERN = "yyyyMMdd_HHmmssSS";

    public String process(String inputPrefix, NameGenerationRequest request) {
        String retVal = inputPrefix;
        String timestamp = new SimpleDateFormat(DATE_PATTERN).format(new Date());
        return retVal.replaceAll(TIMESTAMP_REPLACE_RGX, timestamp);
    }

}
