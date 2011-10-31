/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
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
