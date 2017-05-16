/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

import org.sejda.core.support.io.MultipleOutputWriter;
import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.util.StringUtils;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.PdfScaler;
import org.sejda.impl.sambox.component.SetHeaderFooterWriter;
import org.sejda.impl.sambox.util.FontUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SetHeaderFooterParameters;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.scale.ScaleType;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAMBox implementation of a task adding header or footer labels to pages
 * 
 * @author Eduard Weissmann
 * 
 */
public class SetHeaderFooterTask extends BaseTask<SetHeaderFooterParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetHeaderFooterTask.class);

    private int totalSteps;
    private PDDocumentHandler documentHandler = null;
    private MultipleOutputWriter outputWriter;

    private PdfSourceOpener<PDDocumentHandler> documentLoader;

    @Override
    public void before(SetHeaderFooterParameters parameters, TaskExecutionContext executionContext)
            throws TaskException {
        super.before(parameters, executionContext);
        totalSteps = parameters.getSourceList().size();
        documentLoader = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newMultipleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(SetHeaderFooterParameters parameters) throws TaskException {
        int currentStep = 0;

        for (PdfSource<?> source : parameters.getSourceList()) {
            executionContext().assertTaskNotCancelled();

            currentStep++;

            LOG.debug("Opening {}", source);
            documentHandler = source.open(documentLoader);
            documentHandler.getPermissions().ensurePermission(PdfAccessPermission.MODIFY);
            documentHandler.setCreatorOnPDDocument();

            File tmpFile = createTemporaryBuffer(parameters.getOutput());
            LOG.debug("Created output on temporary buffer {}", tmpFile);

            documentHandler.setVersionOnPDDocument(parameters.getVersion());
            documentHandler.setCompress(parameters.isCompress());

            if(parameters.isAddMargins()){
                new PdfScaler(ScaleType.CONTENT).scale(documentHandler.getUnderlyingPDDocument(), 0.9);
            }

            // ensure the text can be displayed
            // remove any unsupported characters and warn about it
            String originalValue = parameters.getPattern();
            String value = FontUtils.removeUnsupportedCharacters(parameters.getPattern(), documentHandler.getUnderlyingPDDocument());
            if(!value.equals(originalValue)) {
                // some characters are not supported
                Set<Character> unsupportedChars = StringUtils.difference(originalValue, value);
                String displayUnsupportedChars = org.apache.commons.lang3.StringUtils.join(
                        unsupportedChars.stream()
                                .map(c -> StringUtils.asUnicodes(c.toString()))
                                .collect(Collectors.toList()),
                        ","
                );
                notifyEvent(executionContext().notifiableTaskMetadata()).taskWarning(
                        String.format("Unsupported characters (%s) were removed: '%s'",
                                displayUnsupportedChars, org.apache.commons.lang3.StringUtils.abbreviate(originalValue, 20))
                );
            }

            try (SetHeaderFooterWriter footerWriter = new SetHeaderFooterWriter(documentHandler)) {
                int currentFileCounter = currentStep + parameters.getFileCountStartFrom() - 1;
                String outName = nameGenerator(parameters.getOutputPrefix()).generate(
                        nameRequest().originalName(source.getName()).fileNumber(currentFileCounter));

                footerWriter.write(value, parameters, currentFileCounter, outName, executionContext());
                documentHandler.savePDDocument(tmpFile);
                outputWriter.addOutput(file(tmpFile).name(outName));
            }

            notifyEvent(executionContext().notifiableTaskMetadata()).stepsCompleted(currentStep).outOf(totalSteps);
        }

        parameters.getOutput().accept(outputWriter);
    }

    @Override
    public void after() {
        nullSafeCloseQuietly(documentHandler);
    }

}
