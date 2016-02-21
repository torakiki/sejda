package org.sejda.conversion;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.exception.SejdaRuntimeException;

/**
 * Converts a string to a list of integers representing page numbers
 * 
 * @author Eduard Weissmann
 */
public class PageNumbersListAdapter {
    private static final String SEPARATOR = ",";

    private List<Integer> pageNumbers;

    public PageNumbersListAdapter(String rawString) {
        try {
            pageNumbers = doParsePageNumbers(rawString);
        } catch (SejdaRuntimeException e) {
            throw new ConversionException("Unparsable page numbers '" + rawString + "'. " + e.getMessage(), e);
        }
    }

    public List<Integer> getPageNumbers() {
        return pageNumbers;
    }

    /**
     * @param rawString
     *            string representation of the List<Integer> page numbers
     */
    private List<Integer> doParsePageNumbers(String rawString) {
        List<Integer> result = new ArrayList<>();
        for (String s : AdapterUtils.splitAndTrim(rawString, SEPARATOR)) {
            result.add(parsePageNumber(s));
        }
        return result;
    }

    private int parsePageNumber(String s) {
        return AdapterUtils.parseInt(StringUtils.trim(s), "page number");
    }
}
