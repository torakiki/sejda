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
//open because of reflective toString and validation
open module org.sejda.model {
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.sejda.commons;
    requires org.slf4j;

    requires transitive jakarta.validation;
    requires transitive java.desktop;
    requires transitive org.sejda.io;

    exports org.sejda.model;
    exports org.sejda.model.encryption;
    exports org.sejda.model.exception;
    exports org.sejda.model.image;
    exports org.sejda.model.input;
    exports org.sejda.model.notification;
    exports org.sejda.model.notification.event;
    exports org.sejda.model.optimization;
    exports org.sejda.model.outline;
    exports org.sejda.model.output;
    exports org.sejda.model.parameter;
    exports org.sejda.model.parameter.base;
    exports org.sejda.model.parameter.edit;
    exports org.sejda.model.parameter.image;
    exports org.sejda.model.pdf;
    exports org.sejda.model.pdfa;
    exports org.sejda.model.pdf.encryption;
    exports org.sejda.model.pdf.font;
    exports org.sejda.model.pdf.form;
    exports org.sejda.model.pdf.headerfooter;
    exports org.sejda.model.pdf.label;
    exports org.sejda.model.pdf.page;
    exports org.sejda.model.pdf.transition;
    exports org.sejda.model.pdf.viewerpreference;
    exports org.sejda.model.prefix;
    exports org.sejda.model.repaginate;
    exports org.sejda.model.rotation;
    exports org.sejda.model.scale;
    exports org.sejda.model.split;
    exports org.sejda.model.task;
    exports org.sejda.model.toc;
    exports org.sejda.model.validation.constraint;
    exports org.sejda.model.util;

}