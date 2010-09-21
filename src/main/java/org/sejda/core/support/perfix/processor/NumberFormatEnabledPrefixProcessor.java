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
package org.sejda.core.support.perfix.processor;

import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract prefix processor with number formatting capabilities. Provides help method to handle "####" like strings as pattern.
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class NumberFormatEnabledPrefixProcessor implements PrefixProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(NumberFormatEnabledPrefixProcessor.class);

    /**
     * @param numberPattern
     *            the input number pattern of the type "####"
     * @return the {@link DecimalFormat} with the applied pattern
     */
    DecimalFormat formatter(String numberPattern) {
        DecimalFormat retVal = new DecimalFormat();
        try {
            if (StringUtils.isNotBlank(numberPattern)) {
                retVal.applyPattern(numberPattern.replaceAll("#", "0"));
                return retVal;
            }
        } catch (IllegalArgumentException iae) {
            LOG.error(String.format("Error applying pattern %s", numberPattern), iae);
        }
        retVal.applyPattern("00000");
        return retVal;
    }

}
