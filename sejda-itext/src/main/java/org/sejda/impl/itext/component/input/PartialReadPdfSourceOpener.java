/*
 * Created on 16/jul/2011
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.impl.itext.component.input;

import java.io.IOException;

import org.sejda.core.manipulation.model.input.PdfFileSource;
import org.sejda.core.manipulation.model.input.PdfStreamSource;
import org.sejda.core.manipulation.model.input.PdfURLSource;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;

/**
 * iText component able to open a PdfSource and return the corresponding {@link PdfReader} opened in "partial" mode.
 * 
 * @author Andrea Vacondio
 * 
 */
class PartialReadPdfSourceOpener extends AbstractPdfSourceOpener {

    @Override
    PdfReader openSource(PdfURLSource source) throws IOException {
        return new PdfReader(new RandomAccessFileOrArray(source.getUrl()), source.getPasswordBytes());
    }

    @Override
    PdfReader openSource(PdfFileSource source) throws IOException {
        return new PdfReader(new RandomAccessFileOrArray(source.getFile().getAbsolutePath()), source.getPasswordBytes());
    }

    @Override
    PdfReader openSource(PdfStreamSource source) throws IOException {
        return new PdfReader(new RandomAccessFileOrArray(source.getStream()), source.getPasswordBytes());
    }

}
