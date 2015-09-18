/*
 * Created on 12 set 2015
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.common;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Component that allows to set up a lookup relation between items of the same type.
 * 
 * @author Andrea Vacondio
 * @param <I>
 *            type of the items
 */
public class LookupTable<I> {
    private Map<I, I> oldToNew = new LinkedHashMap<>();

    /**
     * Adds a lookup entry.
     * 
     * @param keyItem
     * @param valueItem
     */
    public void addLookupEntry(I keyItem, I valueItem) {
        requireNonNull(keyItem, "Cannot map a null item");
        requireNonNull(valueItem, "Cannot map a null item");
        oldToNew.put(keyItem, valueItem);

    }

    public void clear() {
        oldToNew.clear();
    }

    /**
     * @return true if the table is empty
     */
    public boolean isEmpty() {
        return oldToNew.isEmpty();
    }

    /**
     * Looks up the item that correspond to the given one
     * 
     * @param item
     * @return the item associated to the given input one or null if no mapping is present
     */
    public I lookup(I item) {
        return oldToNew.get(item);
    }

    /**
     * @param item
     * @return true if the table contains a lookup for the given item
     */
    public boolean hasLookupFor(I item) {
        return oldToNew.containsKey(item);
    }

    /**
     * @return a collection containing values of the table
     */
    public Collection<I> values() {
        return oldToNew.values();
    }

    /**
     * @return the first item or null if the table is empty.
     */
    public I first() {
        if (!isEmpty()) {
            return oldToNew.values().iterator().next();
        }
        return null;
    }

    /**
     * @return the keys of the table
     */
    public Set<I> keys() {
        return oldToNew.keySet();
    }
}
