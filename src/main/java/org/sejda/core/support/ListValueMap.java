/*
 * Created on 18/apr/2010
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
package org.sejda.core.support;

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
