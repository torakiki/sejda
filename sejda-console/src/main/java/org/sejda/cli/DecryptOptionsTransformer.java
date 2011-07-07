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
package org.sejda.cli;

import org.sejda.cli.adapters.PdfFileSourceAdapter;
import org.sejda.core.manipulation.model.parameter.DecryptParameters;

/**
 * {@link CommandOptionsTransformer} for the {@link org.sejda.core.manipulation.model.task.itext.DecryptTask} command line interface
 * 
 * @author Eduard Weissmann
 * 
 */
public class DecryptOptionsTransformer implements CommandOptionsTransformer<DecryptCommandOptions, DecryptParameters> {

    /**
     * Transforms {@link DecryptCommandOptions} to {@link DecryptParameters}
     * 
     * @param options
     * @return
     */
    @Override
    public DecryptParameters toTaskParameters(DecryptCommandOptions options) {
        DecryptParameters parameters = new DecryptParameters();
        parameters.setCompress(options.getCompressed());
        parameters.setVersion(options.getPdfVersion());
        parameters.setOutput(options.getOutput().getPdfDirectoryOutput());
        parameters.setOutputPrefix(options.getOutputPrefix());
        for (PdfFileSourceAdapter eachAdapter : options.getFiles()) {
            parameters.addSource(eachAdapter.getPdfFileSource());
        }
        parameters.setOverwrite(options.getOverwrite());
        return parameters;
    }
}
