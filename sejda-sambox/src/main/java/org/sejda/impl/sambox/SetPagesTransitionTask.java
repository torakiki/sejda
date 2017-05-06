/*
 * Created on 18 dic 2015
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

import static java.util.Optional.ofNullable;
import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;
import static org.sejda.impl.sambox.util.TransitionUtils.getTransition;
import static org.sejda.impl.sambox.util.TransitionUtils.initTransitionDimension;
import static org.sejda.impl.sambox.util.TransitionUtils.initTransitionDirection;
import static org.sejda.impl.sambox.util.TransitionUtils.initTransitionMotion;

import java.io.File;

import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SetPagesTransitionParameters;
import org.sejda.model.pdf.viewerpreference.PdfPageMode;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.pagenavigation.PDTransition;
import org.sejda.sambox.pdmodel.interactive.pagenavigation.PDTransitionStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAMBox implementation of a task that applies pages transitions to an input document.
 * 
 * @author Andrea Vacondio
 *
 */
public class SetPagesTransitionTask extends BaseTask<SetPagesTransitionParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetPagesTransitionTask.class);

    private PDDocumentHandler documentHandler = null;
    private SingleOutputWriter outputWriter;
    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    @Override
    public void before(SetPagesTransitionParameters parameters, TaskExecutionContext executionContext)
            throws TaskException {
        super.before(parameters, executionContext);
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(SetPagesTransitionParameters parameters) throws TaskException {
        executionContext().assertTaskNotCancelled();
        notifyEvent(executionContext().notifiableTaskMetadata()).progressUndetermined();

        PdfSource<?> source = parameters.getSource();
        LOG.debug("Opening {}", source);
        documentHandler = source.open(documentLoader);

        File tmpFile = createTemporaryBuffer(parameters.getOutput());
        outputWriter.taskOutput(tmpFile);
        LOG.debug("Temporary output set to {}", tmpFile);

        LOG.debug("Applying transitions");
        int current = 0;
        for (PDPage page : documentHandler.getPages()) {
            executionContext().assertTaskNotCancelled();
            current++;
            ofNullable(parameters.getOrDefault(current)).ifPresent(t -> {
                LOG.trace("Applying transition {}", t);
                PDTransition transition = new PDTransition(
                        ofNullable(getTransition(t.getStyle())).orElse(PDTransitionStyle.R));
                initTransitionDimension(t, transition);
                initTransitionMotion(t, transition);
                initTransitionDirection(t, transition);
                transition.setDuration(t.getTransitionDuration());
                page.setTransition(transition, t.getDisplayDuration());
            });
        }

        if (parameters.isFullScreen()) {
            documentHandler.setPageModeOnDocument(PdfPageMode.FULLSCREEN);
        }

        documentHandler.setCreatorOnPDDocument();
        documentHandler.setVersionOnPDDocument(parameters.getVersion());
        documentHandler.setCompress(parameters.isCompress());
        documentHandler.savePDDocument(tmpFile);
        nullSafeCloseQuietly(documentHandler);

        parameters.getOutput().accept(outputWriter);
        LOG.debug("Transitions set on {}", parameters.getOutput());
    }

    @Override
    public void after() {
        nullSafeCloseQuietly(documentHandler);
    }


}
