/*
 * Copyright 2018 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.impl.sambox.component.excel;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataTableUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DataTableUtils.class);

    private DataTableUtils() {
    }

    public static List<DataTable> mergeTablesSpanningMultiplePages(List<DataTable> dataTables) {
        List<DataTable> results = new ArrayList<>();
        DataTable current = null;

        for (DataTable dt : dataTables) {
            if (current != null) {
                if (current.hasSameHeaderBlanksIgnoredAs(dt)) {
                    addBlankColumnsToMatchHeaders(current, dt);
                }
                
                if (current.hasSameColumnCountAs(dt)) {
                    current = current.mergeWith(dt);
                } else {
                    results.add(current);
                    current = dt;
                }
            } else {
                current = dt;
            }
        }

        if (current != null) {
            results.add(current);
        }

        return results;
    }

    public static List<DataTable> mergeComplementaryColumns(List<DataTable> dataTables) {
        List<DataTable> results = new ArrayList<>();
        for(DataTable dt: dataTables) {
            results.add(mergeComplementaryColumns(dt));
        }
        return results;
    }
    
    public static void addBlankColumnsToMatchHeaders(DataTable a, DataTable b) {
        if (!a.hasSameHeaderBlanksIgnoredAs(b)) {
            throw new RuntimeException("Only works when tables have same headers (blanks ignored)");
        }

        List<String> aHeaderRow = a.headerRow();
        List<String> bHeaderRow = b.headerRow();
        int aa = 0, bb = 0;
        while(aa < aHeaderRow.size() && bb < bHeaderRow.size()) {
            String aCol = aHeaderRow.get(aa).trim();
            String bCol = bHeaderRow.get(bb).trim();
            
            if (aCol.equals(bCol)) {
                aa++;
                bb++;
            } else if(aCol.isEmpty()) {
                b.addBlankColumn(bb);
            } else if(bCol.isEmpty()) {
                a.addBlankColumn(aa);
            } else {
                throw new RuntimeException("Should not happen");
            }
        }
    }

    static DataTable mergeComplementaryColumns(DataTable dataTable) {
        DataTable result = dataTable;
        boolean again = true;
        while(again) {
            again = false;
            for (int c = 0; c < result.getColumnsCount() - 1; c++) {
                List<String> column1 = result.getColumn(c);
                List<String> column2 = result.getColumn(c + 1);
                if (areComplementary(column1, column2)) {
                    LOG.debug("Merging complementary columns {} and {}", c, c + 1);
                    result = result.mergeColumns(c, c + 1);
                    again = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Complementary columns are columns which can be merged like a zipper: left column is missing values where right
     * column has them, and vice versa.
     *
     * Eg:
     *
     *   , Header
     * A1,
     *   , B2
     *   , B3
     * A4,
     */
    public static boolean areComplementary(List<String> column1, List<String> column2) {
        if(column1.size() != column2.size()) {
            return false;
        }

        for(int i = 0; i < column1.size(); i++) {
            String v1 = column1.get(i);
            String v2 = column2.get(i);

            if(!v1.trim().isEmpty() && !v2.trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
