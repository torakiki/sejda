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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.input.PdfURLSource;

import com.lowagie.text.pdf.PdfReader;

/**
 * iText component able to open a PdfSource and return the corresponding {@link PdfReader}. The opened input source is fully loaded into memory.
 * 
 * @author Andrea Vacondio
 * 
 */
// AV: this loader is used in the RotateTask because when using the partial read loader the rotation is not applied (I don't know why). I tried to dig into the iText code but it
// looks like a treasure map to me, I can't follow it and I'm not able to find why rotation is not working. As a workaround we use this loader in the RotateTask that seems
// working.
class FullReadPdfSourceOpener extends AbstractPdfSourceOpener {

    @Override
    PdfReader openSource(PdfURLSource source) throws IOException {
        return new PdfReader(new BufferedInputStream(source.getSource().openStream()), source.getPasswordBytes());

    }

    @Override
    PdfReader openSource(PdfFileSource source) throws IOException {
        return new PdfReader(new BufferedInputStream(new FileInputStream(source.getSource())),
                source.getPasswordBytes());

    }

    @Override
    PdfReader openSource(PdfStreamSource source) throws IOException {
        return new PdfReader(new BufferedInputStream(source.getSource()), source.getPasswordBytes());
    }

}
