/*
 * Created on 27 gen 2016
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.sambox.component.optimizaton;

import static java.util.Optional.ofNullable;
import static org.sejda.impl.sambox.component.optimizaton.Optimizers.pageOptimizer;

import java.util.function.Consumer;

import org.sejda.model.parameter.OptimizeParameters;
import org.sejda.sambox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Vacondio
 *
 */
public class PagesOptimizer implements Consumer<PDPage> {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentOptimizer.class);

    private Consumer<PDPage> optimizer = (p) -> LOG.trace("Optimizing page");

    public PagesOptimizer(OptimizeParameters parameters) {
        parameters.getOptimizations().forEach(o -> {
            ofNullable(pageOptimizer(o, parameters)).ifPresent(toAdd -> optimizer = optimizer.andThen(toAdd));
        });
    }

    @Override
    public void accept(PDPage d) {
        optimizer.accept(d);
    }

}
