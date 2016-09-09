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
package org.sejda.core.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.model.TopLeftRectangularBox;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.excel.PdfToExcelParameters;
import org.sejda.model.parameter.excel.Table;

@Ignore
public abstract class PdfToExcelTaskTest extends BaseTaskTest<PdfToExcelParameters> {
    @Test
    public void testConversion() throws IOException {
        PdfToExcelParameters params = getParams();
        execute(params);

        testContext.assertTaskCompleted();
        testContext.assertOutputSize(1).assertOutputContainsFilenames("tabular-data.xlsx").forEachRawOutput(p -> {
            try {
                InputStream in = new FileInputStream(p.toFile());
                Workbook wb = WorkbookFactory.create(in);

                assertThat(wb.getNumberOfSheets(), is(2));

                Sheet sheet = wb.getSheetAt(0);
                assertThat(sheet.getPhysicalNumberOfRows(), is(37));
                assertThat(sheet.getSheetName(), is("Table 1 (Page 1)"));

                assertThat(getDataRow(sheet, 0), is(Arrays.asList("OrderDate", "Region", "Rep", "Item", "Units", "Unit Cost", "Total")));
                assertThat(getDataRow(sheet, 10), is(Arrays.asList("6/8/15", "East", "Jones", "Binder", "60", "8.99", "539.40")));
                assertThat(getDataRow(sheet, 13), is(Arrays.asList("7/29/15", "East", "Parent", "Binder", "81", "19.99", "1,619.19")));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void testMergedTables() throws IOException {
        PdfToExcelParameters params = getParams();
        params.setMergeTablesSpanningMultiplePages(true);
        execute(params);

        testContext.assertTaskCompleted();
        testContext.assertOutputSize(1).assertOutputContainsFilenames("tabular-data.xlsx").forEachRawOutput(p -> {
            try {
                InputStream in = new FileInputStream(p.toFile());
                Workbook wb = WorkbookFactory.create(in);

                assertThat(wb.getNumberOfSheets(), is(1));

                Sheet sheet = wb.getSheetAt(0);
                assertThat(sheet.getPhysicalNumberOfRows(), is(44));
                assertThat(sheet.getSheetName(), is("Table 1 (Pages 1, 2)"));

                assertThat(getDataRow(sheet, 0), is(Arrays.asList("OrderDate", "Region", "Rep", "Item", "Units", "Unit Cost", "Total")));
                assertThat(getDataRow(sheet, 10), is(Arrays.asList("6/8/15", "East", "Jones", "Binder", "60", "8.99", "539.40")));
                assertThat(getDataRow(sheet, 13), is(Arrays.asList("7/29/15", "East", "Parent", "Binder", "81", "19.99", "1,619.19")));
                assertThat(getDataRow(sheet, 37), is(Arrays.asList("9/10/16", "Central", "Gill", "Pencil", "7", "1.29", "9.03")));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<Object> getDataRow(Sheet sheet, int i) {
        Row row = sheet.getRow(i);
        List<Object> dataRow = new ArrayList<>();

        for (Cell cell : row) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    dataRow.add(cell.getRichStringCellValue().getString());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        dataRow.add(cell.getDateCellValue());
                    } else {
                        dataRow.add(cell.getNumericCellValue());
                    }
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    dataRow.add(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    dataRow.add(cell.getCellFormula());
                    break;
                default:
                    throw new RuntimeException("Unknown cell type:" + cell.getCellType());
            }
        }

        return dataRow;
    }

    private PdfToExcelParameters getParams() throws IOException {
        PdfToExcelParameters parameters = new PdfToExcelParameters();
        parameters.addSource(customInput("pdf/tabular-data.pdf", "tabular-data.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);

        Table table = new Table();

        table.addColumns(new TopLeftRectangularBox(42, 39, 54, 524));
        table.addColumns(new TopLeftRectangularBox(99, 39, 36, 524));
        table.addColumns(new TopLeftRectangularBox(145, 39, 46, 524));
        table.addColumns(new TopLeftRectangularBox(195, 39, 39, 524));
        table.addColumns(new TopLeftRectangularBox(235, 39, 35, 524));
        table.addColumns(new TopLeftRectangularBox(274, 39, 45, 524));
        table.addColumns(new TopLeftRectangularBox(329, 39, 49, 524));
        table.addRows(new TopLeftRectangularBox(42, 39, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 53, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 67, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 82, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 96, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 110, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 124, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 139, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 153, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 167, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 182, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 196, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 210, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 224, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 238, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 253, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 267, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 281, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 295, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 310, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 324, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 338, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 352, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 367, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 381, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 395, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 409, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 424, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 438, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 452, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 466, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 481, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 495, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 509, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 523, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 538, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 552, 336, 11));

        parameters.addTable(1, table);
        
        table = new Table();

        table.addColumns(new TopLeftRectangularBox(42, 39, 54, 111));
        table.addColumns(new TopLeftRectangularBox(99, 39, 36, 111));
        table.addColumns(new TopLeftRectangularBox(145, 39, 46, 111));
        table.addColumns(new TopLeftRectangularBox(195, 39, 31, 111));
        table.addColumns(new TopLeftRectangularBox(235, 39, 35, 111));
//        table.addColumns(new TopLeftRectangularBox(264, 39, 6, 111));
        table.addColumns(new TopLeftRectangularBox(274, 39, 45, 111));
        table.addColumns(new TopLeftRectangularBox(329, 39, 49, 111));
        table.addRows(new TopLeftRectangularBox(42, 39, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 53, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 67, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 82, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 96, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 110, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 124, 336, 11));
        table.addRows(new TopLeftRectangularBox(42, 139, 336, 11));
        
        parameters.addTable(2, table);

        testContext.directoryOutputTo(parameters);

        return parameters;
    }
}
