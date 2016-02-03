/*
 * Created on 03 feb 2016
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
package org.sejda.cli.model;

import org.sejda.conversion.OptimizationPolicyAdapter;

import com.lexicalscope.jewel.cli.Option;

/**
 * For tasks with optimizable output
 * 
 * @author Andrea Vacondio
 *
 */
public interface CliArgumentsWithOptimizableOutput {

    @Option(shortName = "z", description = "optimize generated documents removing unused resources {yes, no, auto}. If omitted it uses auto and perform optimization only if necessary (optional)", defaultValue = "auto")
    OptimizationPolicyAdapter getOptimize();
}
