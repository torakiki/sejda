/*
 * Created on 27 dic 2017
 * Copyright 2017 Sober Lemur S.r.l. and Sejda BV.
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
package org.sejda.impl.sambox.component;

import org.sejda.sambox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Component that parses the page content steam and the page annotations appearance streams
 *
 * @author Andrea Vacondio
 */
public class ContentStreamProcessor extends FailableContentStreamProcessor implements Consumer<PDPage> {

    private static final Logger LOG = LoggerFactory.getLogger(ContentStreamProcessor.class);

    @Override
    public void accept(PDPage page) {
        try {
            super.accept(page);
        } catch (IOException e) {
            LOG.warn("Failed parse page, skipping and continuing with next.", e);
        }
    }
}
