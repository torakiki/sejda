/*
 * Created on 22/08/22
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
/**
 * @author Andrea Vacondio
 */
module org.sejda.conversion {
    requires java.xml;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.sejda.commons;
    requires org.slf4j;

    requires transitive java.desktop;
    requires transitive org.sejda.model;

    exports org.sejda.conversion;
    exports org.sejda.conversion.exception;
}