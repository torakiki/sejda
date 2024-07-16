/*
 * Created on 15/07/24
 * Copyright 2024 Sober Lemur S.r.l. and Sejda BV
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
package org.sejda.impl.sambox.component.pdfa;

import org.apache.commons.lang3.function.FailableConsumer;
import org.sejda.model.exception.TaskException;
import org.sejda.sambox.output.PreSaveCOSTransformer;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;

/**
 * @author Andrea Vacondio
 */
public final class Rules {

    private Rules() {
        //hide
    }

    public static FailableConsumer<PDDocument, TaskException> documentRules(ConversionContext context) {
        return switch (context.parameters().conformanceLevel()) {
            case PDFA_1B -> new NoOCPropertiesDocumentRule(context).andThen(new OutputIntentDocumentRule(context, true))
                    .andThen(new ActionsDocumentRule(context)).andThen(new NoAAorADocumentRule(context))
                    .andThen(new NoEmbeddedFilesDocumentRule(context))
                    .andThen(new NoNeedAppearancesDocumentRule(context)).andThen(new SpecVersionDocumentRule(context))
                    .andThen(new XMPMetadataDocumentRule(context));
        };
    }

    public static FailableConsumer<PDPage, TaskException> pageRules(ConversionContext context) {
        return switch (context.parameters().conformanceLevel()) {
            case PDFA_1B -> new ActionsPageRule(context).andThen(new AnnotationsPageRule(context))
                    .andThen(new AnnotationsColorPageRule(context));
        };
    }

    public static PreSaveCOSTransformer preSaveCOSTransformer(ConversionContext context) {
        return switch (context.parameters().conformanceLevel()) {
            case PDFA_1B -> {
                var transformer = new DefaultPreSaveCOSTransformer();
                transformer.addStreamConsumer(new NoDLStreamRule(context));
                transformer.addStreamConsumer(new NoFileSpecificationStreamRule(context));
                transformer.addStreamConsumer(new NoLZWCompressionStreamRule(context));
                transformer.addStreamConsumer(new XMPStreamRule(context));
                transformer.addDictionaryConsumer(new NoEFDictionaryRule(context));
                yield transformer;
            }
        };
    }
}
