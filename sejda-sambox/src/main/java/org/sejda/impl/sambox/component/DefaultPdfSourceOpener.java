/* 
 * This file is part of the Sejda source code
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.sambox.component;

import java.io.IOException;

import org.sejda.core.notification.dsl.ApplicationEventsNotifier;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.exception.TaskWrongPasswordException;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.input.PdfURLSource;
import org.sejda.model.task.TaskExecutionContext;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.encryption.InvalidPasswordException;

/**
 * SAMBox component able to open a PdfSource and return the corresponding {@link PDDocumentHandler}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class DefaultPdfSourceOpener implements PdfSourceOpener<PDDocumentHandler> {

    private static final String WRONG_PWD_MESSAGE = "Unable to open '%s' due to a wrong password.";
    private static final String ERROR_MESSAGE = "An error occurred opening the source: %s.";
    private static final String WARNING_PARSE_ERRORS_MESSAGE = "Errors were detected when reading document: %s. Please verify the results carefully.";
    
    private final TaskExecutionContext taskExecutionContext;

    @Deprecated
    //use the constructor with the TaskExecutionContext instead
    public DefaultPdfSourceOpener() {
        this.taskExecutionContext = null;
    }

    public DefaultPdfSourceOpener(TaskExecutionContext taskExecutionContext) {
        this.taskExecutionContext = taskExecutionContext;
    }

    @Override
    public PDDocumentHandler open(PdfURLSource source) throws TaskIOException {
        return openGeneric(source);
    }

    @Override
    public PDDocumentHandler open(PdfFileSource source) throws TaskIOException {
        return openGeneric(source);
    }

    @Override
    public PDDocumentHandler open(PdfStreamSource source) throws TaskIOException {
        return openGeneric(source);
    }

    private PDDocumentHandler openGeneric(PdfSource<?> source) throws TaskIOException {
        try {
            PDDocument document = PDFParser.parse(source.getSeekableSource(), source.getPassword());
            if(document.hasParseErrors()){
                if(taskExecutionContext != null){
                    ApplicationEventsNotifier.notifyEvent(taskExecutionContext.notifiableTaskMetadata())
                            .taskWarningOnce(String.format(WARNING_PARSE_ERRORS_MESSAGE, source.getName()));
                }
            }
            
            return new PDDocumentHandler(document);
        } catch (InvalidPasswordException ipe) {
            throw new TaskWrongPasswordException(String.format(WRONG_PWD_MESSAGE, source.getName()), ipe);
        } catch (IOException e) {
            throw new TaskIOException(String.format(ERROR_MESSAGE, source), e);
        }
    }
}
