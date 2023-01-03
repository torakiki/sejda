package org.sejda.conversion;
/*
 * Created on 03/01/23
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

import org.sejda.model.scale.PageNormalizationPolicy;

/**
 * @author Andrea Vacondio
 */
public class PageNormalizationPolicyAdapter extends EnumAdapter<PageNormalizationPolicy> {

    public PageNormalizationPolicyAdapter(String userFriendlyName) {
        super(userFriendlyName, PageNormalizationPolicy.class, "Page normalization policy");
    }

}
