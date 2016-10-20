/*
 * Created on 29/mag/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.input;

import java.io.File;

import org.sejda.model.exception.TaskIOException;
import org.sejda.model.validation.constraint.PdfFile;

/**
 * {@link AbstractPdfSource} from a {@link File}
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfFileSource extends AbstractPdfSource<File> {

    @PdfFile
    private final File file;

    private PdfFileSource(File file, String password) {
        super(file.getName(), password);
        this.file = file;
    }

    @Override
    public File getSource() {
        return file;
    }

    @Override
    public <T> T open(PdfSourceOpener<T> opener) throws TaskIOException {
        return opener.open(this);
    }

    /**
     * Creates a new instance of the pdf source where a password is NOT required to open the source.
     * 
     * @param file
     *            input pdf file
     * @return a newly created instance
     */
    public static PdfFileSource newInstanceNoPassword(File file) {
        return PdfFileSource.newInstanceWithPassword(file, null);
    }

    /**
     * Creates a new instance of the pdf source where a password is required to open the source.
     * 
     * @param file
     *            input pdf file
     * @param password
     * @return a newly created instance
     */
    public static PdfFileSource newInstanceWithPassword(File file, String password) {
        if (file == null || !file.isFile()) {
            throw new IllegalArgumentException("A not null File instance that isFile is expected. Path: " + file);
        }
        return new PdfFileSource(file, password);
    }

    @Override
    public String toString() {
        return file.getAbsolutePath();
    }

}
