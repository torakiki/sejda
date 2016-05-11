/*
 * Copyright 2015 by Eduard Weissmann (edi.weissmann@gmail.com).
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.model.pdf;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.sejda.model.pdf.headerfooter.NumberingStyle;

public class TextStampPattern {

    private int currentPage;
    private int totalPages;

    private String batesSeq;
    private String fileSeq;
    private String filename;

    public TextStampPattern withPage(int currentPage, int totalPages) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;

        return this;
    }

    public TextStampPattern withBatesSequence(String batesSeq) {
        this.batesSeq = batesSeq;

        return this;
    }

    public TextStampPattern withFileSequence(String fileSeq) {
        this.fileSeq = fileSeq;

        return this;
    }

    public TextStampPattern withFilename(String filename) {
        this.filename = filename;

        return this;
    }

    public String build(String pattern) {
        String result = pattern;

        if(currentPage > 0) {
            result = StringUtils.replace(result, "[PAGE_ROMAN]", NumberingStyle.ROMAN.toStyledString(currentPage));
            result = StringUtils.replace(result, "[PAGE_ARABIC]", NumberingStyle.ARABIC.toStyledString(currentPage));
            result = StringUtils.replace(result, "[PAGE_NUMBER]", NumberingStyle.ARABIC.toStyledString(currentPage));
            result = StringUtils.replace(result, "[TOTAL_PAGES_ROMAN]", NumberingStyle.ROMAN.toStyledString(totalPages));
            result = StringUtils.replace(result, "[TOTAL_PAGES_ARABIC]", NumberingStyle.ARABIC.toStyledString(totalPages));
            result = StringUtils.replace(result, "[TOTAL_PAGES]", NumberingStyle.ARABIC.toStyledString(totalPages));
            result = StringUtils.replace(result, "[PAGE_OF_TOTAL]", String.format("%d of %d", currentPage, totalPages));
        }

        result = StringUtils.replace(result, "[DATE]", dateNow());

        if(filename != null) {
            result = StringUtils.replace(result, "[BASE_NAME]", FilenameUtils.getBaseName(filename));
        }

        if(batesSeq != null) {
            result = StringUtils.replace(result, "[BATES_NUMBER]", batesSeq);
        }

        if(fileSeq != null) {
            result = StringUtils.replace(result, "[FILE_NUMBER]", fileSeq);
        }

        return result;
    }

    public static String dateNow() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }
}
