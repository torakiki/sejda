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

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.PdfTextExtractorByArea;
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

            List<List<List<String>>> all = new ArrayList<>();
            List<List<String>> dataTable = new ArrayList<>();

            for (int pageNumber = 1; pageNumber <= numberOfPages; pageNumber++) {
                LOG.debug("Extracting tables from page {}", pageNumber);
                long start = System.currentTimeMillis();
                PDPage page = sourceDocumentHandler.getPage(pageNumber);

                for (Table table : parameters.getTables(pageNumber)) {
                    for (TopLeftRectangularBox row : table.getRows()) {
                        List<Rectangle> cellAreas = new ArrayList<>();
                        for (TopLeftRectangularBox column : table.getColumns()) {
                            TopLeftRectangularBox cell = row.intersection(column);
                            if (!cell.asRectangle().isEmpty()) {
                                cellAreas.add(cell.withPadding(1).asRectangle());
                            } else {
                                LOG.warn("Column and row do not intersect: row: " + row.toString() + ", column: "
                                        + column.toString());
                            }
                        }

                        List<String> cellValues = new PdfTextExtractorByArea().extractTextFromAreas(page, cellAreas);
                        LOG.trace("Cell values: " + StringUtils.join(cellValues, ","));
                        dataTable.add(cellValues);
                    }

                    all.add(dataTable);
                    dataTable = new ArrayList<>();
                }

                LOG.debug("Done extracting tables from page {}, took {} seconds", pageNumber,
                        (System.currentTimeMillis() - start) / 1000);
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

    private void writeExcelFile(List<List<List<String>>> dataTables, File tmpFile) throws TaskException {
        Workbook wb = new XSSFWorkbook();
        try (FileOutputStream fileOut = new FileOutputStream(tmpFile)) {
            for (int t = 0; t < dataTables.size(); t++) {
                LOG.trace("Writing data table " + t);
                List<List<String>> dataTable = dataTables.get(t);
                Sheet sheet = wb.createSheet(String.format("Table %d", t));

                for (int r = 0; r < dataTable.size(); r++) {
                    LOG.trace("Writing row " + r);
                    List<String> dataRow = dataTable.get(r);
                    Row row = sheet.createRow(r);

                    for (int i = 0; i < dataRow.size(); i++) {
                        String stringValue = dataRow.get(i);
                        row.createCell(i).setCellValue(stringValue);

                        sheet.autoSizeColumn(i);
                    }
                }
            }
            wb.write(fileOut);
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
