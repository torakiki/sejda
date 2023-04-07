/*
 * Created on 01/lug/2010
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
package org.sejda.core.support.prefix.processor;

import org.sejda.core.support.prefix.model.PrefixTransformationContext;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A {@link PrefixProcessor} that updates the context current prefix replacing all the [TIMESTAMP] occurrences with the current timestamp.
 *
 * @author Andrea Vacondio
 */
public class TimestampPrefixProcessor implements PrefixProcessor {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSS");

    @Override
    public void accept(PrefixTransformationContext context) {
        if (context.currentPrefix().contains("[TIMESTAMP]")) {
            context.uniqueNames(true);
            context.currentPrefix(
                    context.currentPrefix().replace("[TIMESTAMP]", FORMATTER.format(ZonedDateTime.now())));
        }
    }

}
