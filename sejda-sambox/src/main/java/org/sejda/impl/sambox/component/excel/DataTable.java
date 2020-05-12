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
package org.sejda.impl.sambox.component.excel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.sejda.core.support.util.StringUtils.isolateRTLIfRequired;

public class DataTable {

    private static final Logger LOG = LoggerFactory.getLogger(DataTable.class);

    private final List<List<String>> data = new ArrayList<>();
    private final TreeSet<Integer> pageNumbers = new TreeSet<>();

    public DataTable(int pageNumber) {
        this.pageNumbers.add(pageNumber);
    }

    public DataTable(Collection<Integer> pageNumbers) {
        this.pageNumbers.addAll(pageNumbers);
    }

    public DataTable addRow(String... dataRow) {
        List<String> row = new ArrayList<>();
        for (String item: dataRow) {
            row.add(item);
        }
        addRow(row);
        
        return this;
    }

    public void addRow(List<String> dataRow) {
        data.add(dataRow);
    }

    public void addRows(List<List<String>> dataRows) {
        dataRows.forEach(this::addRow);
    }

    public List<String> headerRow() {
        return data.get(0);
    }
    
    public List<String> headerRowIgnoreBlanks() {
        return data.get(0).stream().filter(s -> !s.trim().isEmpty()).collect(Collectors.toList());
    }

    public boolean hasSameHeaderAs(DataTable other) {
        String thisHeader = String.join("", this.headerRowIgnoreBlanks()).trim();
        String otherHeader = String.join("", other.headerRowIgnoreBlanks()).trim();
        LOG.debug("Comparing header columns: '{}' and '{}'", thisHeader, otherHeader);

        return thisHeader.equalsIgnoreCase(otherHeader);
    }
    
    public boolean hasSameHeaderBlanksIgnoredAs(DataTable other) {
        return this.headerRowIgnoreBlanks().equals(other.headerRowIgnoreBlanks());
    }

    public boolean hasSameColumnCountAs(DataTable other) {
        LOG.debug("Comparing header columns size: {} and {}", this.headerRow().size(), other.headerRow().size());
        return other.headerRow().size() == this.headerRow().size();
    }

    public List<List<String>> getData() {
        return data;
    }

    public TreeSet<Integer> getPageNumbers() {
        return pageNumbers;
    }
    
    public DataTable mergeWith(DataTable other) {
        TreeSet<Integer> resultPageNumbers = new TreeSet<>();
        resultPageNumbers.addAll(this.pageNumbers);
        resultPageNumbers.addAll(other.pageNumbers);

        DataTable result = new DataTable(resultPageNumbers);
        result.addRows(this.data);

        List<List<String>> otherData = other.data;
        if (this.hasSameHeaderAs(other)) {
            otherData.remove(0);
        }

        result.addRows(otherData);

        return result;
    }
    
    private boolean hasConsecutivePages() {
        Integer prev = null;
        for(Integer current: pageNumbers) {
            if(prev != null) {
                if(prev != current - 1) {
                    return false;
                }
            }
            
            prev = current;
        }
        
        return true;
    }

    public String getPagesAsString() {
        StringBuilder sb = new StringBuilder();
        if (this.pageNumbers.size() > 1) {
            sb.append("Pages ");
            
            if(pageNumbers.size() > 2 && hasConsecutivePages()) {
                sb.append(pageNumbers.first()).append("-").append(pageNumbers.last());
                return sb.toString();
            }
            
            int i = 0;
            for (Integer pageNumber : pageNumbers) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(pageNumber);
                i++;
            }
        } else {
            sb.append("Page ").append(pageNumbers.iterator().next());
        }

        return sb.toString();
    }

    public boolean hasData() {
        return this.data.size() > 0;
    }

    public int getColumnsCount() {
        int result = 0;
        for (List<String> row : data) {
            result = Math.max(row.size(), result);
        }

        return result;
    }

    public int getRowsCount() {
        return data.size();
    }

    public List<String> getColumn(int c) {
        List<String> result = new ArrayList<>(getRowsCount());
        for (List<String> row : data) {
            result.add(getOrEmpty(row, c));
        }
        return result;
    }
    
    public List<String> getRow(int r) {
        return data.get(r);
    }

    public DataTable mergeColumns(int c1, int c2) {
        DataTable result = new DataTable(getPageNumbers());
        for (List<String> row : data) {
            List<String> newRow = new ArrayList<>(row.size() - 1);
            for (int c = 0; c < row.size(); c++) {
                if (c == c2) {
                    // noop, continue
                } else if (c == c1) {
                    String newValue = new StringBuilder().append(getOrEmpty(row, c1)).append(" ")
                            .append(getOrEmpty(row, c2)).toString().trim();
                    newRow.add(newValue);
                } else {
                    newRow.add(getOrEmpty(row, c));

                }
            }
            result.addRow(newRow);
        }
        return result;
    }
    
    public void addBlankColumn(int index) {
        for(List<String> row : this.data) {
            row.add(index, "");
        }
    }

    private static String getOrEmpty(List<String> list, int index) {
        if (list.size() <= index) {
            return "";
        }
        return list.get(index);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        int totalWidth = 0;
        int colCount = getColumnsCount();
        List<Integer> colWidths = new ArrayList<>();
        
        for (int i = 0; i < colCount; i++) {
            List<String> col = getColumn(i);
            int colWidth = 0;
            for (int j = 0; j < col.size(); j++) {
                colWidth = Math.max(colWidth, col.get(j).length());
            }
            colWidths.add(colWidth);
            totalWidth += colWidth;
        }
        
        String line = "+" + StringUtils.repeat("-", totalWidth + colCount - 1) + "+";
        
        for(int i = 0; i < getRowsCount(); i++) {
            List<String> row = getRow(i);

            sb.append("\n").append(line).append("\n");
            for(int j = 0; j < colWidths.size(); j++) {
                // TODO: fix padding for arabic when unprintable chars are present
                // TODO: ensure unprintable characters are not counted when padding (all columns have same width)
                String cellPadded = rightPad("", colWidths.get(j));
                
                if(j < row.size()) {
                    cellPadded = isolateRTLIfRequired(rightPad(row.get(j), colWidths.get(j)));
                }

                sb.append("|").append(cellPadded);

            }
            
            sb.append("|");
        }

        sb.append("\n").append(line).append("\n");
        
        return sb.toString();
    }
}
