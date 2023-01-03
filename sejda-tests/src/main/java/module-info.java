/*
 * Created on 22/08/22
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
module org.sejda.tests {

    requires jakarta.validation;
    requires transitive org.sejda.model;
    requires org.junit.jupiter.api;
    requires org.slf4j;
    requires org.hibernate.validator;
    requires org.sejda.commons;
    requires org.sejda.sambox;
    requires org.sejda.core;
    requires org.mockito;
    requires org.apache.commons.lang3;
    requires org.apache.commons.io;
    requires org.hamcrest;
    requires net.coobird.thumbnailator;
    requires com.github.romankh3.image.comparison;

    exports org.sejda.tests;
    exports org.sejda.tests.tasks;
    opens pdf;
    opens pdf.forms;
    opens image;
}
