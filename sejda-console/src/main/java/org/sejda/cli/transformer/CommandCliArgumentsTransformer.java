/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli.transformer;

import org.sejda.cli.model.TaskCliArguments;
import org.sejda.model.parameter.base.TaskParameters;

/**
 * Transformation component that knows how to transform command line arguments {@link TaskCliArguments} into task parameters {@link TaskParameters}
 * 
 * @author Eduard Weissmann
 * @param <A>
 *            the {@link TaskCliArguments} type transformed.
 * @param <P>
 *            the {@link TaskParameters} type the arguments are transformed to.
 */
public interface CommandCliArgumentsTransformer <A extends TaskCliArguments, P extends TaskParameters> {

    /**
     * Transforms the specified command line arguments into task parameters
     * 
     * @param taskCliArguments
     *            command line arguments
     * @return translated task parameters
     */
    P toTaskParameters(A taskCliArguments);
}
