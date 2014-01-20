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
package org.sejda.core.support.prefix.processor;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract prefix processor with number formatting capabilities and skeletal implementation for number based {@link PrefixProcessor}. Provides help method to handle "####" like
 * strings as pattern. Process the input prefix replacing all the [PREFIX] or [PREFIX##] or [PREFIX##11] or [PREFIX11] occurrences with the input number (formatted with the given
 * pattern identified by the number of # and incremented by the starting number if found where starting number can be negative).
 * <p>
 * Ex: <b>[FILENUMBER]_BLA_[FILENUMBERE###]_LAB_[FILENUMBER####100]</b> and given file number <b>2</b> will produce <b>2_BLA_002_LAB_0102</b>
 * <p>
 * </p>
 * <b>[FILENUMBER-3]_BLA_LAB_[FILENUMBER####100]</b> and given file number <b>2</b> will produce <b>-1_BLA_002_LAB_0102</b> </p>
 * 
 * @author Andrea Vacondio
 * 
 */
abstract class NumberPrefixProcessor implements PrefixProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(NumberPrefixProcessor.class);
    private final String findRegexp;

    NumberPrefixProcessor(String prefix) {
        if (StringUtils.isBlank(prefix)) {
            throw new IllegalArgumentException("Prefix cannot be blank");
        }
        findRegexp = String.format("\\[%s(#*)(-?[0-9]*)\\]", prefix);
    }

    /**
     * Try to find matches and replace them with the formatted number. An empty string is returned if no match is found.
     * 
     * @param inputString
     * @param num
     * @return the processed string if a match is found. An empty string if no match is found.
     */
    protected String findAndReplace(String inputString, Integer num) {
        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile(findRegexp).matcher(inputString);
        while (m.find()) {
            String replacement = getReplacement(m.group(1), m.group(2), num);
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * @param numberPatter
     * @param startingNumber
     * @param num
     * @return the string the processor will use to perform replacement
     */
    private String getReplacement(String numberPatter, String startingNumber, Integer num) {
        String replacement = "";
        Integer number = getReplacementNumber(startingNumber, num);
        if (StringUtils.isNotBlank(numberPatter)) {
            replacement = formatter(numberPatter).format(number);
        } else {
            replacement = number.toString();
        }
        return replacement;
    }

    /**
     * @param startingNumber
     * @param num
     * @return the number calculated as fileNumber + startingNumber
     */
    private Integer getReplacementNumber(String startingNumber, Integer num) {
        Integer retVal = num;
        if (StringUtils.isNotBlank(startingNumber)) {
            retVal += Integer.valueOf(startingNumber);
        }
        return retVal;
    }

    /**
     * @param numberPattern
     *            the input number pattern of the type "####"
     * @return the {@link DecimalFormat} with the applied pattern
     */
    private DecimalFormat formatter(String numberPattern) {
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
