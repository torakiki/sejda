/*
 * Created on 31/ago/2015
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;

import org.sejda.core.support.util.HumanReadableSize;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.optimization.OptimizationRuler;
import org.sejda.impl.sambox.component.split.AbstractPdfSplitter;
import org.sejda.impl.sambox.component.split.SizePdfSplitter;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SplitBySizeParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task splitting an input pdf document when the generated document reaches a given size. This implementation doesn't allow to store the pdf document objects i Objects Stream.
 * 
 * @author Andrea Vacondio
 */
public class SplitBySizeTask extends BaseTask<SplitBySizeParameters> {
    private static final Logger LOG = LoggerFactory.getLogger(SplitBySizeTask.class);

    private int totalSteps;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;
    private PDDocument document = null;
    private AbstractPdfSplitter<SplitBySizeParameters> splitter;

    @Override
    public void before(SplitBySizeParameters parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
    }

    @Override
    public void execute(SplitBySizeParameters parameters) throws TaskException {
        int currentStep = 0;

        for (PdfSource<?> source : parameters.getSourceList()) {
            executionContext().assertTaskNotCancelled();
            currentStep++;
            LOG.debug("Opening {}", source);
            document = source.open(documentLoader).getUnderlyingPDDocument();

            splitter = new SizePdfSplitter(document, parameters,
                    new OptimizationRuler(parameters.getOptimizationPolicy()).apply(document));
            LOG.debug("Starting split by size {}", HumanReadableSize.toString(parameters.getSizeToSplitAt()));
            splitter.split(executionContext(), parameters.getOutputPrefix(), source);

            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        LOG.debug("Input documents rotated and written to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        closeResource();
    }

    private void closeResource() {
        nullSafeCloseQuietly(document);
        splitter = null;
    }
}
