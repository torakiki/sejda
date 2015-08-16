/*
 * Created on 09/mar/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.itext.component.input;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;
import org.sejda.core.Sejda;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.PdfStreamSource;

import com.lowagie.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 * 
 */
public class AbstractPdfSourceOpenerTest {

    private PdfStreamSource getSource() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_own_pwd.pdf");
        return PdfStreamSource.newInstanceNoPassword(stream, "enc_own_pwd.pdf");
    }

    @Test
    public void testUnethicalFull() throws TaskIOException {
        System.setProperty(Sejda.UNETHICAL_READ_PROPERTY_NAME, "false");
        PdfReader encReader = PdfSourceOpeners.newFullReadOpener().open(getSource());
        assertTrue(encReader.isEncrypted());
        System.setProperty(Sejda.UNETHICAL_READ_PROPERTY_NAME, "true");
        PdfReader reader = PdfSourceOpeners.newFullReadOpener().open(getSource());
        assertFalse(reader.isEncrypted());
    }

    @Test
    public void testUnethicalPartial() throws TaskIOException {
        System.setProperty(Sejda.UNETHICAL_READ_PROPERTY_NAME, "false");
        PdfReader encReader = PdfSourceOpeners.newPartialReadOpener().open(getSource());
        assertTrue(encReader.isEncrypted());
        System.setProperty(Sejda.UNETHICAL_READ_PROPERTY_NAME, "true");
        PdfReader reader = PdfSourceOpeners.newPartialReadOpener().open(getSource());
        assertFalse(reader.isEncrypted());
    }

    @Test(expected = TaskIOException.class)
    public void wrongPdfFull() throws TaskIOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/not_a_pdf.pdf");
        PdfSourceOpeners.newFullReadOpener().open(PdfStreamSource.newInstanceNoPassword(stream, "not_a_pdf.pdf"));
    }

    @Test(expected = TaskIOException.class)
    public void wrongPdfPartial() throws TaskIOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/not_a_pdf.pdf");
        PdfSourceOpeners.newPartialReadOpener().open(PdfStreamSource.newInstanceNoPassword(stream, "not_a_pdf.pdf"));
    }
}
