/* 
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.optimization.OptimizationRuler;
import org.sejda.impl.sambox.component.split.PagesPdfSplitter;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.AbstractSplitByPageParameters;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task splitting an input pdf document on a set of pages defined in the input parameter object.
 * 
 * @author Andrea Vacondio
 * @param <T>
 *            the type of the parameters.
 */
public class SplitByPageNumbersTask<T extends AbstractSplitByPageParameters> extends BaseTask<T> {

    private static final Logger LOG = LoggerFactory.getLogger(SplitByPageNumbersTask.class);

    private int totalSteps;
    private PDDocument document = null;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;
    private PagesPdfSplitter<T> splitter;

    @Override
    public void before(T parameters, TaskExecutionContext executionContext) throws TaskException {
        super.before(parameters, executionContext);
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
    }

    @Override
    public void execute(T parameters) throws TaskException {
        int currentStep = 0;

        for (PdfSource<?> source : parameters.getSourceList()) {
            executionContext().assertTaskNotCancelled();
            currentStep++;

            LOG.debug("Opening {}", source);
            document = source.open(documentLoader).getUnderlyingPDDocument();

            splitter = new PagesPdfSplitter<>(document, parameters,
                    new OptimizationRuler(parameters.getOptimizationPolicy()).apply(document));

            LOG.debug("Starting split by page numbers for {} ", parameters);
            splitter.split(executionContext(), parameters.getOutputPrefix(), source);

            nullSafeCloseQuietly(document);

            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        LOG.debug("Input documents split and written to {}", parameters.getOutput());
    }

    @Override
    public void after() {
        nullSafeCloseQuietly(document);
        splitter = null;
    }

}
