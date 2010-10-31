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
package org.sejda.core.manipulation.model.input;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.sejda.core.validation.constraint.NotEmpty;

/**
 * Model for a pdf input source
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class PdfSource implements Serializable {

    private static final long serialVersionUID = -6780088810823438389L;

    private String password;
    @NotEmpty
    private String name;

    public PdfSource(String name) {
        this.name = name;
    }

    public PdfSource(String name, String password) {
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
        if (StringUtils.isNotEmpty(password)) {
            retVal = password.getBytes();
        }
        return retVal;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the name of this source
     */
    public String getName() {
        return name;
    }

    /**
     * @return the type of this source
     */
    @NotNull
    public abstract PdfSourceType getSourceType();

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(name).append(getSourceType()).toString();
    }

}
