/*
 * Copyright 2015 by Edi Weissmann (edi.weissmann@gmail.com)
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
package org.sejda.impl.sambox.component.split;

import org.sejda.impl.sambox.component.SplitByTextChangesOutputStrategy;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.SplitByTextContentParameters;
import org.sejda.model.split.NextOutputStrategy;

public class ByTextChangesPdfSplitter extends AbstractPdfSplitter<SplitByTextContentParameters> {
    private SplitByTextChangesOutputStrategy outputStrategy;

    public ByTextChangesPdfSplitter(PDDocument document, SplitByTextContentParameters parameters) throws TaskIOException {
        super(document, parameters);
        this.outputStrategy = new SplitByTextChangesOutputStrategy(document, parameters.getTextArea());
    }

    @Override
    NameGenerationRequest enrichNameGenerationRequest(NameGenerationRequest request) {
        return request.text(outputStrategy.getTextByPage(request.getPage()));
    }

    @Override
    NextOutputStrategy nextOutputStrategy() {
        return outputStrategy;
    }
}
