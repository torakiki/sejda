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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class DataTableUtilsTest {

    @Test
    public void mergeComplementaryColumns() {
        DataTable dt = new DataTable(1);
        dt.addRow("Vehicle type", "", "Brand");
        dt.addRow("Car", "Audi", "");
        dt.addRow("Car", "Mercedes", "");
        dt.addRow("Truck", "Volvo", "");
        dt.addRow("Truck", "DAF", "");

        DataTable merged = DataTableUtils.mergeComplementaryColumns(dt);
        assertThat(merged.getColumnsCount(), is(2));
        assertThat(merged.getRowsCount(), is(5));
        assertThat(merged.getColumn(0), is(Arrays.asList("Vehicle type", "Car", "Car", "Truck", "Truck")));
        assertThat(merged.getColumn(1), is(Arrays.asList("Brand", "Audi", "Mercedes", "Volvo", "DAF")));

        assertThat(dt.getColumnsCount(), is(3));
    }

    @Test
    public void areComplementary() {
        assertThat(DataTableUtils.areComplementary(
                Arrays.asList("Header", "", " "),
                Arrays.asList("", "Value1", "Value2")
        ), is(true));

        assertThat(DataTableUtils.areComplementary(
                Arrays.asList("Header1", "ValueA", ""),
                Arrays.asList("Header2", "", "Value2")
        ), is(false));
    }

    @Test
    public void mergeComplementaryColumnsThatNeedsMultiplePasses() {
        DataTable dt = new DataTable(1);
        dt.addRow("H1", "  ", "H2", "  ", "H4");
        dt.addRow("A1", "B1", "  ", "D1", "  ");
        dt.addRow("A2", "B2", "  ", "  ", "E2");
        dt.addRow("A3");

        DataTable merged = DataTableUtils.mergeComplementaryColumns(dt);
        assertThat(merged.getColumnsCount(), is(3));
        assertThat(merged.getColumn(0), is(Arrays.asList("H1", "A1", "A2", "A3")));
        assertThat(merged.getColumn(1), is(Arrays.asList("H2", "B1", "B2", "")));
        assertThat(merged.getColumn(2), is(Arrays.asList("H4", "D1", "E2", "")));

    }

    @Test
    public void mergeWithAccountingBlankHeaders_scenario1() {
        DataTable dt = new DataTable(1)
                .addRow("H1", "H2", "H3").addRow("A1", "A2", "A3");

        DataTable dt2 = new DataTable(3).addRow("H1", " ", "", "H2", "H3").addRow("C1", "CX", "CY", "C2", "C3");

        List<DataTable> mergedList = DataTableUtils.mergeTablesSpanningMultiplePages(Arrays.asList(dt, dt2));
        assertThat(mergedList.size(), is(1));
        DataTable merged = mergedList.get(0);

        assertThat(merged.toString(), is("""

                +--------------+
                |H1|  |  |H2|H3|
                +--------------+
                |A1|  |  |A2|A3|
                +--------------+
                |C1|CX|CY|C2|C3|
                +--------------+
                """));

    }

    @Test
    public void mergeWithAccountingBlankHeaders_scenario2() {
        DataTable dt = new DataTable(1)
                .addRow("H1", "", "H2", "H3").addRow("A1", "AX", "A2", "A3");

        DataTable dt2 = new DataTable(3).addRow("H1", "H2", "", "H3").addRow("C1", "C2", "CX", "C3");

        List<DataTable> mergedList = DataTableUtils.mergeTablesSpanningMultiplePages(Arrays.asList(dt, dt2));
        assertThat(mergedList.size(), is(1));
        DataTable merged = mergedList.get(0);

        assertThat(merged.toString(), is("""

                +--------------+
                |H1|  |H2|  |H3|
                +--------------+
                |A1|AX|A2|  |A3|
                +--------------+
                |C1|  |C2|CX|C3|
                +--------------+
                """));
    }
}
