/*
 * Created on 09/lug/2010
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
package org.sejda.model.parameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.parameter.base.SinglePdfSourceSingleOutputParameters;
import org.sejda.model.pdf.PdfMetadataKey;
import org.sejda.model.validation.constraint.NotEmpty;
import org.sejda.model.validation.constraint.SingleOutputAllowedExtensions;

/**
 * Parameter class for the set metadata manipulation.
 * 
 * @author Andrea Vacondio
 * 
 */
@SingleOutputAllowedExtensions
public final class SetMetadataParameters extends SinglePdfSourceSingleOutputParameters {

    @NotEmpty
    private final Map<PdfMetadataKey, String> metadata = new HashMap<PdfMetadataKey, String>();

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

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(metadata).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SetMetadataParameters)) {
            return false;
        }
        SetMetadataParameters parameter = (SetMetadataParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(metadata.entrySet(), parameter.entrySet())
                .isEquals();
    }
}
