/*
 * Created on 22/ago/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * {@link HashSet} wrapper disallowing null elements.
 * 
 * @author Andrea Vacondio
 * @param <E>
 *            the set type.
 */
public class NullSafeSet<E> implements Set<E> {

    private Set<E> wrappedSet;

    public NullSafeSet() {
        wrappedSet = new HashSet<E>();
    }

    public int size() {
        return wrappedSet.size();
    }

    public boolean isEmpty() {
        return wrappedSet.isEmpty();
    }

    public boolean contains(Object o) {
        return wrappedSet.contains(o);
    }

    public Iterator<E> iterator() {
        return wrappedSet.iterator();
    }

    public Object[] toArray() {
        return wrappedSet.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return wrappedSet.toArray(a);
    }

    /**
     * Adds the input element if it's not null.
     */
    public boolean add(E e) {
        if (e != null) {
            return wrappedSet.add(e);
        }
        return false;
    }

    public boolean remove(Object o) {
        return wrappedSet.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return wrappedSet.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        boolean retVal = false;
        for (E e : c) {
            if (add(e)) {
                retVal = true;
            }
        }
        return retVal;
    }

    public boolean retainAll(Collection<?> c) {
        return wrappedSet.retainAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return wrappedSet.removeAll(c);
    }

    public void clear() {
        wrappedSet.clear();
    }

}
