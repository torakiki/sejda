/*
 * Created on 29/mag/2010
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
package org.sejda.model.input;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sejda.model.validation.constraint.NotEmpty;

/**
 * Skeletal implementation for a pdf input source.
 * 
 * @author Andrea Vacondio
 * 
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

    public String getPassword() {
        return password;
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
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(name).toString();
    }

}
