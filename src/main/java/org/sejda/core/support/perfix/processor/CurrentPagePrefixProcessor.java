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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.sejda.core.support.perfix.model.NameGenerationRequest;

/**
 * Process the input prefix replacing all the [CURRENTPAGE] or [CURRENTPAGE##] occurrences with the input current page number (formatted with the given pattern identified by the
 * number of #). Ex:
 * <p>
 * <b>[CURRENTPAGE]_BLA_[CURRENTPAGE####]_LAB</b> and given page number <b>2</b> will produce <b>2_BLA_0002_LAB</b>
 * </p>
 * 
 * @author Andrea Vacondio
 * 
 */
public class CurrentPagePrefixProcessor extends NumberFormatEnabledPrefixProcessor {

    private static final String FIND_REGEXP = "\\[CURRENTPAGE(#*)\\]";

    public String process(String inputPrefix, NameGenerationRequest request) {
        String retVal = "";
        if (request != null && request.getPage() != null) {
            retVal = findAndReplace(inputPrefix, request.getPage());
        }
        return (StringUtils.isBlank(retVal)) ? inputPrefix : retVal;
    }

    /**
     * Try to find matches and replace them with the formatted page number. An empty string is returned if no match is found.
     * 
     * @param inputString
     * @param pageNumber
     * @return
     */
    private String findAndReplace(String inputString, Integer pageNumber) {
        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile(FIND_REGEXP).matcher(inputString);
        while (m.find()) {
            String replacement = getReplacement(m.group(1), pageNumber);
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * @param numberPatter
     * @param pageNumber
     *            the page number
     * @return the string used by the processor to replace
     */
    private String getReplacement(String numberPatter, Integer pageNumber) {
        String replacement = "";
        if (StringUtils.isNotBlank(numberPatter)) {
            replacement = formatter(numberPatter).format(pageNumber);
        } else {
            replacement = pageNumber.toString();
        }
        return replacement;
    }

}
