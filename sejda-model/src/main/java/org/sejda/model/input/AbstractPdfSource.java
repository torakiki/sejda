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

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.validation.constraint.NotEmpty;

/**
 * Skeletal implementation for a pdf input source.
 * 
 * @author Andrea Vacondio
 * @param <T>
 *            the generic type of the source
 */
public abstract class AbstractPdfSource<T> implements PdfSource<T> {

    private String password;
    @NotEmpty
    private final String name;

    /**
     * Creates a source with the given name.
     * 
     * @param name
     * @throws IllegalArgumentException
     *             if the name is blank
     */
    public AbstractPdfSource(String name) {
        if (isBlank(name)) {
            throw new IllegalArgumentException("A not blank name are expected.");
        }
        this.name = name;
    }

    /**
     * Creates a source with the given name and password.
     * 
     * @param name
     * @param password
     * @throws IllegalArgumentException
     *             if the name is blank
     */
    public AbstractPdfSource(String name, String password) {
        this(name);
        this.password = password;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the password in bytes or null if no password has been set
     */
    public byte[] getPasswordBytes() {
        byte[] retVal = null;
        if (isNotEmpty(password)) {
            retVal = password.getBytes();
        }
        return retVal;
    }

    /**
     * @return the name of this source
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(name).toString();
    }

}
