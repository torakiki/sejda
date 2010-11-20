/*
 * Created on 09/lug/2010
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
