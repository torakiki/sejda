/*
 * Created on 30/mag/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.manipulation.model.output;

import java.io.File;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.sejda.core.validation.constraint.PdfFile;

/**
 * {@link File} output destination
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfFileOutput extends AbstractPdfOutput {

    private static final long serialVersionUID = 8455837002827550634L;

    @PdfFile
    private File file;

    public PdfFileOutput(File file) {
        super();
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public OutputType getOutputType() {
        return OutputType.FILE_OUTPUT;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append(file).toString();
    }

}
