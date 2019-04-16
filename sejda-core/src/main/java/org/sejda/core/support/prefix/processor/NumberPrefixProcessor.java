/*
 * Created on 03/lug/2010
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
 * <b>[FILENUMBER-3]_BLA_LAB_[FILENUMBER####100]</b> and given file number <b>2</b> will produce <b>-1_BLA_002_LAB_0102</b>
 * </p>
 * 
 * @author Andrea Vacondio
 * 
 */
abstract class NumberPrefixProcessor implements PrefixProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(NumberPrefixProcessor.class);
    private final Pattern pattern;

    NumberPrefixProcessor(String prefix) {
        if (StringUtils.isBlank(prefix)) {
            throw new IllegalArgumentException("Prefix cannot be blank");
        }
        pattern = Pattern.compile(String.format("\\[%s(#*)(-?[0-9]*)\\]", prefix));
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
        Matcher m = pattern.matcher(inputString);
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
        Integer number = getReplacementNumber(startingNumber, num);
        if (StringUtils.isNotBlank(numberPatter)) {
            return formatter(numberPatter).format(number);
        }
        return number.toString();
    }

    /**
     * @param startingNumber
     * @param num
     * @return the number calculated as num + startingNumber
     */
    private Integer getReplacementNumber(String startingNumber, Integer num) {
        if (StringUtils.isNotBlank(startingNumber)) {
            return num + Integer.valueOf(startingNumber);
        }
        return num;
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
