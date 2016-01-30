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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.core.support.prefix.NameGenerator.nameGenerator;
import static org.sejda.core.support.prefix.model.NameGenerationRequest.nameRequest;

import java.io.File;

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.optimizaton.DocumentOptimizer;
import org.sejda.impl.sambox.component.optimizaton.PagesOptimizer;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.OptimizeParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.sambox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAMbox implementation of the Optimize task
 */
public class OptimizeTask extends BaseTask<OptimizeParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(OptimizeTask.class);

    private int totalSteps;
    private PDDocumentHandler documentHandler = null;
    private MultipleOutputWriter outputWriter;
    private DocumentOptimizer documentOptimizer;
    private PagesOptimizer pagesOptimizer;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    @Override
    public void before(OptimizeParameters parameters) {
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy());
        documentOptimizer = new DocumentOptimizer(parameters.getOptimizations());
        pagesOptimizer = new PagesOptimizer(parameters);
    }

    @Override
    public void execute(OptimizeParameters parameters) throws TaskException {

        int currentStep = 0;
        for (PdfSource<?> source : parameters.getSourceList()) {
            stopTaskIfCancelled();
            currentStep++;
            LOG.debug("Opening {}", source);
            documentHandler = source.open(documentLoader);
            documentHandler.setCreatorOnPDDocument();

            File tmpFile = createTemporaryPdfBuffer();
            LOG.debug("Created output on temporary buffer {}", tmpFile);

            for (PDPage p : documentHandler.getPages()) {
                stopTaskIfCancelled();
                pagesOptimizer.accept(p);
            }

            documentOptimizer.accept(documentHandler.getUnderlyingPDDocument());

            documentHandler.setVersionOnPDDocument(parameters.getVersion());
            documentHandler.setCompress(parameters.isCompress());
            documentHandler.savePDDocument(tmpFile);

            String outName = nameGenerator(parameters.getOutputPrefix())
                    .generate(nameRequest().originalName(source.getName()).fileNumber(currentStep));
            outputWriter.addOutput(file(tmpFile).name(outName));

            nullSafeCloseQuietly(documentHandler);

            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Input documents optimized and written to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        nullSafeCloseQuietly(documentHandler);
    }

}
