/*
 * Created on 31/08/22
 * Copyright 2022 Sober Lemur S.a.s. di Vacondio Andrea and Sejda BV
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
/**
 * @author Andrea Vacondio
 */
module org.sejda.impl.sambox {

    requires java.xml;
    requires metadata.extractor;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.apache.fontbox;
    requires org.sejda.core.writer;
    requires org.slf4j;
    requires thumbnailator;

    requires transitive java.desktop;
    requires transitive org.sejda.commons;
    requires transitive org.sejda.core;
    requires transitive org.sejda.io;
    requires transitive org.sejda.model;
    requires transitive org.sejda.sambox;

    exports org.sejda.impl.sambox;
    exports org.sejda.impl.sambox.component;
    exports org.sejda.impl.sambox.component.excel;
    exports org.sejda.impl.sambox.component.image;
    exports org.sejda.impl.sambox.component.optimization;
    exports org.sejda.impl.sambox.component.split;
    exports org.sejda.impl.sambox.util;

}