/*
 * Created on 31/08/22
 * Copyright 2022 Sober Lemur S.r.l. and Sejda BV
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

import org.sejda.core.support.prefix.processor.AppendExtensionPrefixProcessor;
import org.sejda.core.support.prefix.processor.BasenamePrefixProcessor;
import org.sejda.core.support.prefix.processor.BookmarkPrefixProcessor;
import org.sejda.core.support.prefix.processor.CurrentPagePrefixProcessor;
import org.sejda.core.support.prefix.processor.FileNumberPrefixProcessor;
import org.sejda.core.support.prefix.processor.PrefixProcessor;
import org.sejda.core.support.prefix.processor.PrependPageNumberPrefixProcessor;
import org.sejda.core.support.prefix.processor.PrependPrefixProcessor;
import org.sejda.core.support.prefix.processor.StrictBookmarkPrefixProcessor;
import org.sejda.core.support.prefix.processor.TimestampPrefixProcessor;
import org.sejda.core.support.prefix.processor.ToSafeFilenamePrefixProcessor;

/**
 * @author Andrea Vacondio
 */
module org.sejda.core {
    uses PrefixProcessor;
    requires java.xml;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.sejda.commons;
    requires org.slf4j;
    requires com.ibm.icu;

    requires transitive jakarta.validation;
    requires transitive org.sejda.model;

    exports org.sejda.core;
    exports org.sejda.core.notification.context;
    exports org.sejda.core.service;
    exports org.sejda.core.support.io;
    exports org.sejda.core.support.io.model;
    exports org.sejda.core.support.prefix;
    exports org.sejda.core.support.prefix.model;
    exports org.sejda.core.support.prefix.processor;
    exports org.sejda.core.support.util;
    exports org.sejda.core.notification.dsl;
    exports org.sejda.core.context;

    provides PrefixProcessor with BasenamePrefixProcessor, CurrentPagePrefixProcessor, FileNumberPrefixProcessor, TimestampPrefixProcessor, BookmarkPrefixProcessor, StrictBookmarkPrefixProcessor, PrependPageNumberPrefixProcessor, PrependPrefixProcessor, AppendExtensionPrefixProcessor, ToSafeFilenamePrefixProcessor;
}
