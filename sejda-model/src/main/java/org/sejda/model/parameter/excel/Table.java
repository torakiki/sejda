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
import org.sejda.model.TopLeftRectangularBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Table {
    private List<TopLeftRectangularBox> rows = new ArrayList<>();
    private List<TopLeftRectangularBox> columns = new ArrayList<>();

    public  Table() {
    }

    public Table(List<TopLeftRectangularBox> rows, List<TopLeftRectangularBox> columns) {
        this.rows.addAll(rows);
        this.columns.addAll(columns);
    }

    public void addRows(TopLeftRectangularBox... rows) {
        this.rows.addAll(Arrays.asList(rows));
    }

    public void addColumns(TopLeftRectangularBox... columns) {
        this.columns.addAll(Arrays.asList(columns));
    }

    public List<TopLeftRectangularBox> getRows() {
        return rows;
    }

    public List<TopLeftRectangularBox> getColumns() {
        return columns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Table table = (Table) o;

        return new EqualsBuilder()
                .append(rows, table.rows)
                .append(columns, table.columns)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(rows)
                .append(columns)
                .toHashCode();
    }
}
