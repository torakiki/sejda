/*
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

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.PageImageWriter;
import org.sejda.impl.sambox.component.PageTextWriter;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.EditParameters;
import org.sejda.model.parameter.edit.AddImageOperation;
import org.sejda.model.parameter.edit.InsertPageOperation;
import org.sejda.model.parameter.edit.AddTextOperation;
import org.sejda.model.parameter.edit.DeletePageOperation;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.task.BaseTask;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.font.PDType1Font;
import org.sejda.sambox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.SortedSet;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;
import static org.sejda.impl.sambox.util.FontUtils.getStandardType1Font;

public class EditTask extends BaseTask<EditParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(EditTask.class);

    private int totalSteps;
    private PDDocumentHandler documentHandler = null;
    private MultipleOutputWriter outputWriter;

    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    @Override
    public void before(EditParameters parameters) {
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy());
    }

    @Override
    public void execute(EditParameters parameters) throws TaskException {
        int currentStep = 0;

        for (PdfSource<?> source : parameters.getSourceList()) {
            stopTaskIfCancelled();

            currentStep++;

            LOG.debug("Opening {}", source);
            documentHandler = source.open(documentLoader);
            documentHandler.getPermissions().ensurePermission(PdfAccessPermission.MODIFY);
            documentHandler.setCreatorOnPDDocument();

            File tmpFile = createTemporaryPdfBuffer();
            LOG.debug("Created output on temporary buffer {}", tmpFile);

            documentHandler.setVersionOnPDDocument(parameters.getVersion());
            documentHandler.setCompress(parameters.isCompress());

            // to be able to delete multiple pages without having issues due to index shift
            // remove them in descending order, one by one
            int[] pagesToDeleteSorted = parameters.getDeletePageOperations().stream().mapToInt(DeletePageOperation::getPageNumber).sorted().toArray();
            for(int i = pagesToDeleteSorted.length - 1; i >= 0; i--) {
                int pageNumber = pagesToDeleteSorted[i];
                LOG.debug("Deleting page {}", pageNumber);
                documentHandler.removePage(pageNumber);
            }

            for(InsertPageOperation insertPageOperation : parameters.getInsertPageOperations()) {
                int pageNumber = insertPageOperation.getPageNumber();
                if(pageNumber > 1) {
                    LOG.debug("Adding new page after page {}", pageNumber - 1);
                    documentHandler.addBlankPageAfter(pageNumber - 1);
                } else {
                    LOG.debug("Adding new page before page {}", pageNumber);
                    documentHandler.addBlankPageBefore(pageNumber);
                }
            }

            int totalPages = documentHandler.getNumberOfPages();

            for (AddTextOperation textOperation : parameters.getTextOperations()) {
                PageTextWriter textWriter = new PageTextWriter(documentHandler.getUnderlyingPDDocument());

                SortedSet<Integer> pageNumbers = textOperation.getPageRange().getPages(totalPages);

                for (int pageNumber : pageNumbers) {
                    PDPage page = documentHandler.getPage(pageNumber);
                    PDFont font = defaultIfNull(getStandardType1Font(textOperation.getFont()), PDType1Font.HELVETICA);
                    textWriter.write(page, textOperation.getPosition(), textOperation.getText(), font, textOperation.getFontSize(), textOperation.getColor());
                }
            }

            for(AddImageOperation imageOperation: parameters.getImageOperations()) {
                PageImageWriter imageWriter = new PageImageWriter(documentHandler.getUnderlyingPDDocument());
                PDImageXObject image = PageImageWriter.toPDXImageObject(imageOperation.getImageSource());

                SortedSet<Integer> pageNumbers = imageOperation.getPageRange().getPages(totalPages);

                for (int pageNumber : pageNumbers) {
                    PDPage page = documentHandler.getPage(pageNumber);
                    imageWriter.write(page, image, imageOperation.getPosition(), imageOperation.getWidth(), imageOperation.getHeight());
                }
            }

            documentHandler.savePDDocument(tmpFile);
            String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                    nameRequest().originalName(source.getName()).fileNumber(currentStep));
            outputWriter.addOutput(file(tmpFile).name(outName));

            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        parameters.getOutput().accept(outputWriter);
    }

    @Override
    public void after() {
        nullSafeCloseQuietly(documentHandler);
    }

}
