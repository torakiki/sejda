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

import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.commons.collection.NullSafeSet;
import org.sejda.model.parameter.base.MultiplePdfSourceMultipleOutputParameters;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Parameter class for the set metadata manipulation.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class SetMetadataParameters extends MultiplePdfSourceMultipleOutputParameters {

    @NotEmpty
    private final Map<String, String> metadata = new HashMap<>();
    private final Set<String> toRemove = new NullSafeSet<>();
    private boolean updateCreatorProducerModifiedDate = true;
    private boolean removeAllMetadata = false;

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void putAll(Map<String, String> m) {
        metadata.putAll(m);
    }

    public void put(String key, String metadata) {
        this.metadata.put(key, metadata);
    }
    
    public void addFieldToRemove(String fieldName) {
        toRemove.add(fieldName);
    }

    public void addFieldsToRemove(Collection<String> fieldNames) {
        toRemove.addAll(fieldNames);
    }

    public Set<String> getToRemove() {
        return toRemove;
    }

    public boolean isUpdateProducerModifiedDate() {
        return updateCreatorProducerModifiedDate;
    }

    public void setUpdateCreatorProducerModifiedDate(Boolean updateCreatorProducerModifiedDate) {
        this.updateCreatorProducerModifiedDate = updateCreatorProducerModifiedDate;
    }

    public boolean isRemoveAllMetadata() {
        return removeAllMetadata;
    }

    public void setRemoveAllMetadata(Boolean removeAllMetadata) {
        this.removeAllMetadata = removeAllMetadata;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(metadata).append(toRemove)
                .append(updateCreatorProducerModifiedDate).append(removeAllMetadata)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SetMetadataParameters parameter)) {
            return false;
        }
        return new EqualsBuilder().appendSuper(super.equals(other)).append(metadata, parameter.metadata)
                .append(toRemove, parameter.toRemove)
                .append(updateCreatorProducerModifiedDate, parameter.updateCreatorProducerModifiedDate)
                .append(removeAllMetadata, parameter.removeAllMetadata)
                .isEquals();
    }
}
