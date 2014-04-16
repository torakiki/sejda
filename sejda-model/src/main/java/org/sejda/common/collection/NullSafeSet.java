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
package org.sejda.common.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * {@link LinkedHashSet} wrapper disallowing null elements.
 * 
 * @author Andrea Vacondio
 * @param <E>
 *            type of elements of the set.
 */
public class NullSafeSet<E> implements Set<E> {

    private Set<E> delegate;

    public NullSafeSet() {
        delegate = new LinkedHashSet<E>();
    }

    public int size() {
        return delegate.size();
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    public Iterator<E> iterator() {
        return delegate.iterator();
    }

    public Object[] toArray() {
        return delegate.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    /**
     * Adds the input element if it's not null.
     */
    public boolean add(E e) {
        if (e != null) {
            return delegate.add(e);
        }
        return false;
    }

    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
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
        return delegate.retainAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
