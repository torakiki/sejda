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

import java.net.URL;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.exception.TaskIOException;

/**
 * {@link AbstractPdfSource} from a {@link URL}
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PdfURLSource extends AbstractPdfSource<URL> {

    @NotNull
    private final URL url;

    private PdfURLSource(URL url, String name, String password) {
        super(name, password);
        this.url = url;
    }

    @Override
    public URL getSource() {
        return url;
    }

    @Override
    public <T> T open(PdfSourceOpener<T> opener) throws TaskIOException {
        return opener.open(this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append(url).toString();
    }

    /**
     * Creates a new instance of the pdf source where a password is NOT required to open the source.
     * 
     * @param url
     *            input URL to a pdf document
     * @param name
     * @return a newly created instance
     */
    public static PdfURLSource newInstanceNoPassword(URL url, String name) {
        return PdfURLSource.newInstanceWithPassword(url, name, null);
    }

    /**
     * Creates a new instance of the pdf source where a password is required to open the source.
     * 
     * @param url
     *            input URL to a pdf document
     * @param name
     * @param password
     * @return a newly created instance
     * @throws IllegalArgumentException
     *             if the input stream or the input name are blank.
     */
    public static PdfURLSource newInstanceWithPassword(URL url, String name, String password) {
        if (url == null) {
            throw new IllegalArgumentException("A not null url instance and a not blank name are expected.");
        }
        return new PdfURLSource(url, name, password);
    }
}
