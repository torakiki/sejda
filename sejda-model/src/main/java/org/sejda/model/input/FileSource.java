/*
 * Created on 27 feb 2016
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.model.input;

import org.sejda.io.SeekableSource;
import org.sejda.io.SeekableSources;
import org.sejda.model.encryption.NoEncryptionAtRest;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.validation.constraint.ExistingFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A {@link File} source
 * 
 * @author Andrea Vacondio
 */
public class FileSource extends AbstractSource<File> {

    @ExistingFile
    private final File file;

    public FileSource(File file) {
        super(file.getName());
        this.file = file;
    }

    @Override
    public File getSource() {
        return this.file;
    }

    @Override
    public String toString() {
        return file.getAbsolutePath();
    }

    @Override
    public <R> R dispatch(SourceDispatcher<R> dispatcher) throws TaskIOException {
        return dispatcher.dispatch(this);
    }

    @Override
    public SeekableSource initializeSeekableSource() throws IOException {
        // optimization: don't stream to a temp file if there's no encryption
        if(getEncryptionAtRestPolicy() instanceof NoEncryptionAtRest) {
            return SeekableSources.seekableSourceFrom(file);
        }

        return SeekableSources.onTempFileSeekableSourceFrom(
                getEncryptionAtRestPolicy().decrypt(new FileInputStream(file)), file.getName());
    }

    public static FileSource newInstance(File file) {
        if (file == null || !file.isFile()) {
            throw new IllegalArgumentException("A not null File instance that isFile is expected. Path: " + file);
        }
        return new FileSource(file);
    }

}
