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

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Model for a pdf input source
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class PdfSource implements Serializable {

    private static final long serialVersionUID = -6780088810823438389L;

    private String password;
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
    public abstract PdfSourceType getSourceType();

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(name).append(getSourceType()).toString();
    }

}
