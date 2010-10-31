/*
 * Created on 03/lug/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
