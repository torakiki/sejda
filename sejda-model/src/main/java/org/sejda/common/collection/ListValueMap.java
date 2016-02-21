/*
 * Created on 18/apr/2010
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
package org.sejda.common.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Map where the elements added to the same key are enqueued in a List.
 * 
 * @author Andrea Vacondio
 * 
 * @param <K>
 *            key generic type
 * @param <V>
 *            value generic type
 */
public final class ListValueMap<K, V> {

    private Map<K, List<V>> map;

    public ListValueMap() {
        map = new HashMap<>();
    }

    /**
     * Removes all the elements form the map
     */
    public void clear() {
        map.clear();
    }

    /**
     * Adds the input value to the {@link List} associated to the input key
     * 
     * @param key
     * @param value
     * @return the List with the input value added
     */
    public List<V> put(K key, V value) {
        List<V> list = map.get(key);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(value);
        return map.put(key, list);
    }

    /**
     * Removes the input value from the {@link List} associated to the input key.
     * 
     * @param key
     * @param value
     * @return true if the value was found and removed.
     */
    public boolean remove(K key, V value) {
        List<V> list = map.get(key);
        if (list != null && !list.isEmpty()) {
            return list.remove(value);
        }
        return false;
    }

    /**
     * A null safe getter for the given key.
     * 
     * @param key
     * @return the list associated to the input key or an empty list of nothing is associated.
     */
    public List<V> get(K key) {
        List<V> list = map.get(key);
        if (list != null) {
            return list;
        }
        return Collections.emptyList();
    }

    /**
     * @return number of values stored by this map
     */
    public int size() {
        int retVal = 0;
        for (Entry<K, List<V>> entry : map.entrySet()) {
            retVal += entry.getValue().size();
        }
        return retVal;
    }

}
