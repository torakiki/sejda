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

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class DataTableTest {

    private static final DataTable dt = new DataTable(1);
    static {
        dt.addRow("Vehicle type", "", "Brand");
        dt.addRow("Car", "Audi", "");
        dt.addRow("Car", "Mercedes", "");
        dt.addRow("Truck", "Volvo", "");
        dt.addRow("Truck", "DAF", "");
    }

    @Test
    public void getColumnCount() {
        assertThat(dt.getColumnsCount(), is(3));
    }

    @Test
    public void getColumn() {
        assertThat(dt.getColumn(1), is(Arrays.asList("", "Audi", "Mercedes", "Volvo", "DAF")));
    }

    @Test
    public void getRowsCount() {
        assertThat(dt.getRowsCount(), is(5));
    }

    @Test
    public void mergeColumns() {
        DataTable merged = dt.mergeColumns(1, 2);
        assertThat(merged.getRowsCount(), is(5));
        assertThat(merged.getColumnsCount(), is(2));
        assertThat(merged.getColumn(0), is(Arrays.asList("Vehicle type", "Car", "Car", "Truck", "Truck")));
        assertThat(merged.getColumn(1), is(Arrays.asList("Brand", "Audi", "Mercedes", "Volvo", "DAF")));
    }

    @Test
    public void getColumnWhenUnevenRowLength() {
        DataTable dt = new DataTable(1);
        dt.addRow("HeadA", "HeadB", "HeadC");
        dt.addRow("A1", "B1", "C1");
        dt.addRow("A2", "B2");
        dt.addRow("A3");
        dt.addRow("A4", "B4", "C4");
        assertThat(dt.getColumn(1), is(Arrays.asList("HeadB", "B1", "B2", "", "B4")));
        assertThat(dt.getColumn(2), is(Arrays.asList("HeadC", "C1", "", "", "C4")));
    }

    @Test
    public void mergeColumnsWhenUnevenRowLength() {
        DataTable dt = new DataTable(1);
        dt.addRow("HeadA", "HeadB", "HeadC");
        dt.addRow("A1", "B1", "C1");
        dt.addRow("A2", "B2");
        dt.addRow("A3");
        dt.addRow("A4", "B4", "C4");

        DataTable merged = dt.mergeColumns(1, 2);
        assertThat(merged.getColumn(1), is(Arrays.asList("HeadB HeadC", "B1 C1", "B2", "", "B4 C4")));
    }
}