package org.sejda.core.support.prefix.model;
/*
 * Created on 04/04/23
 * Copyright 2023 Sober Lemur S.r.l. and Sejda BV
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

import static java.util.Optional.ofNullable;

/**
 * Holds the state of a prefix transformation
 *
 * @author Andrea Vacondio
 */
public class PrefixTransformationContext {

    private String currentPrefix;
    private final String originalPrefix;
    private final NameGenerationRequest request;

    private boolean uniqueNames = false;

    public PrefixTransformationContext(String prefix, NameGenerationRequest request) {
        this.currentPrefix = prefix;
        this.originalPrefix = prefix;
        this.request = ofNullable(request).orElseGet(NameGenerationRequest::nameRequest);
    }

    public String currentPrefix() {
        return currentPrefix;
    }

    public void currentPrefix(String prefix) {
        this.currentPrefix = prefix;
    }

    public NameGenerationRequest request() {
        return request;
    }

    /**
     * @return true if the prefix has been transformed by a transformer that generates unique names
     */
    public boolean uniqueNames() {
        return uniqueNames;
    }

    public void uniqueNames(boolean uniqueNames) {
        this.uniqueNames = uniqueNames;
    }

    /**
     * @return true if no transformation has been applied to the prefix
     */
    public boolean noTransformationApplied() {
        return originalPrefix.equals(currentPrefix);
    }
}
