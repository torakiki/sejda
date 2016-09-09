/*
 * Copyright 2016 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.model.parameter.excel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;

import java.util.*;

public class PdfToExcelParameters extends MultiplePdfSourceMultipleOutputParameters {
    private Map<Integer, List<Table>> tables = new HashMap<>();
    private boolean mergeTablesSpanningMultiplePages = false;

    public void addTable(int pageNumber, Table table) {
        if(!this.tables.containsKey(pageNumber)) {
            this.tables.put(pageNumber, new ArrayList<>());
        }

        this.tables.get(pageNumber).add(table);
    }

    public void addTables(int pageNumber, List<Table> tables) {
        for(Table table: tables) {
            addTable(pageNumber, table);
        }
    }

    public List<Table> getTables(int pageNumber) {
        List<Table> result = tables.get(pageNumber);
        if(result == null) {
            result = new ArrayList<>();
        }

        return result;
    }

    public boolean isMergeTablesSpanningMultiplePages() {
        return mergeTablesSpanningMultiplePages;
    }

    public void setMergeTablesSpanningMultiplePages(boolean mergeTablesSpanningMultiplePages) {
        this.mergeTablesSpanningMultiplePages = mergeTablesSpanningMultiplePages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PdfToExcelParameters that = (PdfToExcelParameters) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(that))
                .append(tables, that.tables)
                .append(mergeTablesSpanningMultiplePages, that.mergeTablesSpanningMultiplePages)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(tables)
                .append(mergeTablesSpanningMultiplePages)
                .toHashCode();
    }
}
