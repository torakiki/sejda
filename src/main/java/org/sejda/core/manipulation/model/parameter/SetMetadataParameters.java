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
package org.sejda.core.manipulation.model.parameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.sejda.core.manipulation.model.pdf.PdfMetadataKey;
import org.sejda.core.validation.constraint.NotEmpty;

/**
 * Parameter class for the set metadata manipulation
 * 
 * @author Andrea Vacondio
 * 
 */
public final class SetMetadataParameters extends SinglePdfSourceParameters {

    private static final long serialVersionUID = -9113822216737314063L;

    @NotEmpty
    private Map<PdfMetadataKey, String> metadata = new HashMap<PdfMetadataKey, String>();

    /**
     * @see Map#entrySet()
     * @return an unmodifiable set view of map
     */
    public Set<Entry<PdfMetadataKey, String>> entrySet() {
        return Collections.unmodifiableSet(metadata.entrySet());
    }

    /**
     * @see Map#keySet()
     * @return a unmodifiable set containing keys of the map
     */
    public Set<PdfMetadataKey> keySet() {
        return Collections.unmodifiableSet(metadata.keySet());
    }

    /**
     * @see Map#putAll(Map)
     * @param m
     */
    public void putAll(Map<PdfMetadataKey, String> m) {
        metadata.putAll(m);
    }

    /**
     * adds the key,value
     * 
     * @see Map#put(Object, Object)
     * @param key
     * @param metadata
     */
    public void put(PdfMetadataKey key, String metadata) {
        this.metadata.put(key, metadata);
    }

    /**
     * clear the metadata map
     * 
     * @see Map#clear()
     */
    public void clear() {
        metadata.clear();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(metadata).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SetMetadataParameters)) {
            return false;
        }
        SetMetadataParameters parameter = (SetMetadataParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(metadata.entrySet(), parameter.entrySet())
                .isEquals();
    }
}
