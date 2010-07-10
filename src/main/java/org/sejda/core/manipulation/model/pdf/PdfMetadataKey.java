/*
 * Created on 09/lug/2010
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
package org.sejda.core.manipulation.model.pdf;

/**
 * Possible metadata key used to set metadata values on a document.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PdfMetadataKey {
    TITLE("Title"), AUTHOR("Author"), SUBJECT("Subject"), KEYWORDS("Keywords"), CREATOR("Creator");

    private PdfMetadataKey(String key) {
        this.key = key;
    }

    private String key;

    /**
     * @return the metadata key
     */
    public String getKey() {
        return key;
    }

}
