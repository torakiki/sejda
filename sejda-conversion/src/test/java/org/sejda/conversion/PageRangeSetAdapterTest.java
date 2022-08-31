/*
 * Created on 15/ott/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.conversion;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.sejda.model.pdf.page.PageRange;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andrea Vacondio
 * 
 */
public class PageRangeSetAdapterTest {

    @Test
    public void insertionOrder() {
        PageRange[] inputRanges = { new PageRange(4), new PageRange(6, 9), new PageRange(1, 2) };
        doTestInsertionOrder(inputRanges);
        ArrayUtils.reverse(inputRanges);
        doTestInsertionOrder(inputRanges);
    }

    private void doTestInsertionOrder(PageRange... pageRanges) {
        String inputString = StringUtils.join(pageRanges, ',');
        var ranges = new ArrayList<>(new PageRangeSetAdapter(inputString).getPageRangeSet());
        for (int i = 0; i < pageRanges.length; i++) {
            assertEquals(pageRanges[i], ranges.get(i));
        }
    }
}
