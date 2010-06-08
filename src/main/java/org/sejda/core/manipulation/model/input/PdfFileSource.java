/*
 * Created on 29/mag/2010
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
package org.sejda.core.manipulation.model.input;

import java.io.File;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * {@link PdfSource} from a {@link File}
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfFileSource extends PdfSource {

    private static final long serialVersionUID = 9153473654119405497L;

    private File file;

    public PdfFileSource(File file) {
        this.file = file;
    }

    public PdfFileSource(File file, String password) {
        super(password);
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public PdfSourceType getSourceType() {
        return PdfSourceType.FILE_SOURCE;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(file).toString();
    }
}
