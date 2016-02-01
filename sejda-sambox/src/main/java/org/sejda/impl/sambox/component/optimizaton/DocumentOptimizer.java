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

import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static org.sejda.impl.sambox.component.optimizaton.Optimizers.documentOptimizer;

import java.util.Set;
import java.util.function.Consumer;

import org.sejda.model.optimization.Optimization;
import org.sejda.model.parameter.OptimizeParameters;
import org.sejda.sambox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Consumer that optimize the given document based on the given {@link OptimizeParameters}
 * 
 * @author Andrea Vacondio
 *
 */
public class DocumentOptimizer implements Consumer<PDDocument> {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentOptimizer.class);

    private Consumer<PDDocument> optimizer = (d) -> LOG.debug("Optimizing document");

    public DocumentOptimizer(Set<Optimization> optimizations) {
        ofNullable(optimizations).orElse(emptySet()).forEach(o -> {
            ofNullable(documentOptimizer(o)).ifPresent(toAdd -> optimizer = optimizer.andThen(toAdd));
        });
    }

    @Override
    public void accept(PDDocument d) {
        optimizer.accept(d);
    }
}
