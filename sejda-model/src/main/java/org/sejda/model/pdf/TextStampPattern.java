/*
 * Copyright 2015 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.model.pdf;

import org.apache.commons.lang3.StringUtils;
import org.sejda.model.pdf.headerfooter.NumberingStyle;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TextStampPattern {

    private int currentPage;
    private int totalPages;

    private String batesSeq;
    private String fileSeq;

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

    public String build(String pattern) {
        String result = pattern;

        if(currentPage > 0) {
            result = StringUtils.replace(result, "[PAGE_ROMAN]", NumberingStyle.ROMAN.toStyledString(currentPage));
            result = StringUtils.replace(result, "[PAGE_ARABIC]", NumberingStyle.ARABIC.toStyledString(currentPage));
            result = StringUtils.replace(result, "[PAGE_OF_TOTAL]", String.format("%d of %d", currentPage, totalPages));
        }

        result = StringUtils.replace(result, "[DATE]", dateNow());

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
