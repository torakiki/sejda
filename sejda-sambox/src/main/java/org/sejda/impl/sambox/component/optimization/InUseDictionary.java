/*
 * Created on 29 mag 2016
 * Copyright 2015 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.impl.sambox.component.optimization;

import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.IndirectCOSObjectIdentifier;

/**
 * Simple {@link COSDictionary} wrapper used to identify a dictionary that is used in the page content stream
 *
 * @author Andrea Vacondio
 */
public class InUseDictionary extends COSDictionary {

    private final COSDictionary wrapped;

    public InUseDictionary(COSDictionary wrapped) {
        super(wrapped);
        this.wrapped = wrapped;
    }

    @Override
    public IndirectCOSObjectIdentifier id() {
        return wrapped.id();
    }

    @Override
    public void idIfAbsent(IndirectCOSObjectIdentifier id) {
        wrapped.idIfAbsent(id);
    }
}
