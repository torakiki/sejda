/*
 * Created on 18/apr/2010
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
package org.sejda.core.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        map = new HashMap<K, List<V>>();
    }

    /**
     * Removes all the elements form the map
     */
    public void clear() {
        map.clear();
    }

    /**
     * Adds the input value to the List associated to the input key
     * 
     * @param key
     * @param value
     * @return the List with the input value added
     */
    public List<V> put(K key, V value) {
        List<V> list = map.get(key);
        if (list == null) {
            list = new ArrayList<V>();
        }
        list.add(value);
        return map.put(key, list);
    }

    /**
     * A null safe getter for the given key.
     * 
     * @param key
     * @return the list associated to the input key or an empty list of nothing is associated.
     */
    @SuppressWarnings("unchecked")
    public List<V> get(K key) {
        List<V> list = map.get(key);
        if (list != null) {
            return list;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * @return number of values stored by this map
     */
    public int size() {
        int retVal = 0;
        for (K key : map.keySet()) {
            retVal += map.get(key).size();
        }
        return retVal;
    }

}
