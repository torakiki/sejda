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
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

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
    public void testToString() {
        DataTable data = new DataTable(1);
        data.addRow("Name", "Surname", "Age", "Sex");
        data.addRow("John", "Doe", "14", "M");
        data.addRow("Alexander", "Appleseed", "99", "M");
        data.addRow("Alex", "Jones", "", "F");
        data.addRow("Felix", "Fog");
        
        String expected = "\n" +
                "+---------------------------+\n" +
                "|Name     |Surname  |Age|Sex|\n" +
                "+---------------------------+\n" +
                "|John     |Doe      |14 |M  |\n" +
                "+---------------------------+\n" +
                "|Alexander|Appleseed|99 |M  |\n" +
                "+---------------------------+\n" +
                "|Alex     |Jones    |   |F  |\n" +
                "+---------------------------+\n" +
                "|Felix    |Fog      |   |   |\n" +
                "+---------------------------+\n";
        
        assertThat(data.toString(), is(expected));
    }
    
    @Test
    public void testToStringWithArabic() {
        DataTable data = new DataTable(1);
        data.addRow("Word one longer header", "Word two");
        data.addRow("Hello", "Goodbye", "1");
        data.addRow("مرحبا", "مرحبًا ABC 123", "وداعا");
        data.addRow("مرحبا", "مرحبًا", "وداعا");
        data.addRow("مرحبا", "", "وداعا");
        
//        System.out.println(data.toString());
        
        String expected = 
                "+-------------------------------------------+\n" +
                "|Word one longer header|Word two      |     |\n" +
                "+-------------------------------------------+\n" +
                "|Hello                 |Goodbye       |1    |\n" +
                "+-------------------------------------------+\n" +
                "|\u2068مرحبا                 \u2069|\u2068مرحبًا ABC 123\u2069|\u2068وداعا\u2069|\n" +
                "+-------------------------------------------+\n" +
                "|\u2068مرحبا                 \u2069|\u2068مرحبًا        \u2069|\u2068وداعا\u2069|\n" +
                "+-------------------------------------------+\n" +
                "|\u2068مرحبا                 \u2069|              |\u2068وداعا\u2069|\n" +
                "+-------------------------------------------+";

        assertThat(data.toString().trim(), is(expected.trim()));
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

    @Test
    public void addBlankColumn() {
        DataTable dt = new DataTable(1);
        dt.addRow("H1", "H2", "H3");
        dt.addRow("A1", "A2", "A3");
        
        dt.addBlankColumn(1);

        assertThat(dt.getRow(0), is(Arrays.asList("H1", "", "H2", "H3")));
        assertThat(dt.getRow(1), is(Arrays.asList("A1", "", "A2", "A3")));
    }

    @Test
    public void testPagesAsString() {
        assertThat(new DataTable(1).getPagesAsString(), is("Page 1"));
        assertThat(new DataTable(new TreeSet<>(Arrays.asList(2, 3))).getPagesAsString(), is("Pages 2, 3"));
        assertThat(new DataTable(new TreeSet<>(Arrays.asList(1, 3, 5))).getPagesAsString(), is("Pages 1, 3, 5"));
        assertThat(new DataTable(new TreeSet<>(Arrays.asList(2, 3, 4, 5))).getPagesAsString(), is("Pages 2-5"));
    }
}
