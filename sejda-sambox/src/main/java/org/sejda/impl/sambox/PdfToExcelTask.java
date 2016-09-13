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
package org.sejda.impl.sambox;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.PdfTextExtractorByArea;
import org.sejda.impl.sambox.component.excel.DataTable;
import org.sejda.model.TopLeftRectangularBox;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.excel.PdfToExcelParameters;
import org.sejda.model.parameter.excel.Table;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

public class PdfToExcelTask extends BaseTask<PdfToExcelParameters> {
    private static final Logger LOG = LoggerFactory.getLogger(PdfToExcelTask.class);

    private PDDocumentHandler sourceDocumentHandler = null;
    private PDDocumentHandler destinationDocument = null;
    private MultipleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    @Override
    public void before(PdfToExcelParameters parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(PdfToExcelParameters parameters) throws TaskException {
        int currentStep = 0;
        int totalSteps = parameters.getSourceList().size();
        for (PdfSource<?> source : parameters.getSourceList()) {
            executionContext().assertTaskNotCancelled();

            currentStep++;

            LOG.debug("Opening {}", source);
            sourceDocumentHandler = source.open(documentLoader);

            File tmpFile = createTemporaryBuffer(".xlsx");
            LOG.debug("Created output temporary buffer {}", tmpFile);

            this.destinationDocument = new PDDocumentHandler();
            destinationDocument.setVersionOnPDDocument(parameters.getVersion());
            destinationDocument.initialiseBasedOn(sourceDocumentHandler.getUnderlyingPDDocument());
            destinationDocument.setCompress(parameters.isCompress());

            int numberOfPages = sourceDocumentHandler.getNumberOfPages();

            List<DataTable> all = new ArrayList<>();

            for (int pageNumber = 1; pageNumber <= numberOfPages; pageNumber++) {
                DataTable dataTable = new DataTable(pageNumber);
                LOG.debug("Extracting tables from page {}", pageNumber);
                long start = System.currentTimeMillis();
                PDPage page = sourceDocumentHandler.getPage(pageNumber);

                for (Table table : parameters.getTables(pageNumber)) {
                    List<Rectangle> cellAreas = new ArrayList<>();
                    for (TopLeftRectangularBox row : table.getRows()) {
                        for (TopLeftRectangularBox column : table.getColumns()) {
                            TopLeftRectangularBox cell = row.intersection(column);
                            if (!cell.asRectangle().isEmpty()) {
                                cellAreas.add(cell.withPadding(1).asRectangle());
                            } else {
                                LOG.warn("Column and row do not intersect: row: " + row.toString() + ", column: "
                                        + column.toString());
                                cellAreas.add(new Rectangle(0, 0, 0, 0));
                            }
                        }
                    }

                    LOG.debug("Extracting text for {} table cells", cellAreas.size());
                    long startTimingCells = System.currentTimeMillis();
                    List<String> cellValues = new PdfTextExtractorByArea().extractTextFromAreas(page, cellAreas);
                    LOG.debug("Text extraction took {} seconds",
                            (System.currentTimeMillis() - startTimingCells) / 1000);

                    int i = 0;
                    ArrayList<String> rowData = new ArrayList<>();
                    for (TopLeftRectangularBox row : table.getRows()) {
                        for (TopLeftRectangularBox column : table.getColumns()) {
                            rowData.add(cellValues.get(i));
                            i++;
                        }
                        dataTable.addRow(rowData);
                        rowData = new ArrayList<>();
                    }

                    all.add(dataTable);
                }

                LOG.debug("Done extracting tables from page {}, took {} seconds", pageNumber,
                        (System.currentTimeMillis() - start) / 1000);
            }

            if (parameters.isMergeTablesSpanningMultiplePages()) {
                all = mergeTablesSpanningMultiplePages(all);
            }
            writeExcelFile(all, tmpFile);

            String outName = nameGenerator(parameters.getOutputPrefix())
                    .generate(nameRequest("xlsx").originalName(source.getName()).fileNumber(currentStep));
            outputWriter.addOutput(file(tmpFile).name(outName));

            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);

            nullSafeCloseQuietly(sourceDocumentHandler);
        }

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Input documents cropped and written to {}", parameters.getOutput());
    }

    private List<DataTable> mergeTablesSpanningMultiplePages(List<DataTable> dataTables) {
        List<DataTable> results = new ArrayList<>();
        DataTable current = null;

        for (DataTable dt : dataTables) {
            if (current != null) {
                if (current.hasSameColumnsAs(dt)) {
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

    private void writeExcelFile(List<DataTable> dataTables, File tmpFile) throws TaskException {
        LOG.debug("Writing data to excel file");
        long start = System.currentTimeMillis();

        Workbook wb = new XSSFWorkbook();
        try (FileOutputStream fileOut = new FileOutputStream(tmpFile)) {
            for (int t = 0; t < dataTables.size(); t++) {
                LOG.debug("Writing data table " + t);
                DataTable dataTable = dataTables.get(t);
                List<List<String>> data = dataTable.getData();
                Sheet sheet = wb.createSheet(String.format("Table %d (%s)", t + 1, dataTable.getPagesAsString()));

                for (int r = 0; r < data.size(); r++) {
                    List<String> dataRow = data.get(r);
                    LOG.debug("Writing row " + r + " of " + dataRow.size() + " values");

                    Row row = sheet.createRow(r);

                    for (int i = 0; i < dataRow.size(); i++) {
                        String stringValue = dataRow.get(i);
                        row.createCell(i).setCellValue(stringValue);
                    }
                }

                for (int c = 0; c < sheet.getRow(0).getPhysicalNumberOfCells(); c++) {
                    sheet.autoSizeColumn(c);
                }
            }
            wb.write(fileOut);
            LOG.debug("Done writing data to excel file, took {} seconds", (System.currentTimeMillis() - start) / 1000);
        } catch (IOException ioe) {
            throw new TaskException("Could not save .xlsx file", ioe);
        }
    }

    @Override
    public void after() {
        nullSafeCloseQuietly(sourceDocumentHandler);
        nullSafeCloseQuietly(destinationDocument);
    }
}
