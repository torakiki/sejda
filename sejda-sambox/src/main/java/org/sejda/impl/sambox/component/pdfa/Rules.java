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
import org.sejda.impl.sambox.component.ReadOnlyFilteredCOSStream;
import org.sejda.impl.sambox.component.optimization.ResourcesHitter;
import org.sejda.model.exception.TaskException;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.IndirectCOSObjectIdentifier;
import org.sejda.sambox.output.PreSaveCOSTransformer;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.sejda.impl.sambox.component.optimization.TilingPatternHitterOperator.tilingPatternSetNonStrokingColor;
import static org.sejda.impl.sambox.component.optimization.TilingPatternHitterOperator.tilingPatternSetStrokingColor;
import static org.sejda.impl.sambox.component.pdfa.BaseSetColorSpace.nonStrokingColorspace;
import static org.sejda.impl.sambox.component.pdfa.BaseSetColorSpace.strokingColorspace;
import static org.sejda.impl.sambox.component.pdfa.BaseSetDeviceColorSpace.nonStrokingCMYK;
import static org.sejda.impl.sambox.component.pdfa.BaseSetDeviceColorSpace.nonStrokingRGB;
import static org.sejda.impl.sambox.component.pdfa.BaseSetDeviceColorSpace.strokingCMYK;
import static org.sejda.impl.sambox.component.pdfa.BaseSetDeviceColorSpace.strokingRGB;
import static org.sejda.impl.sambox.component.pdfa.ShadingPatternSetColor.shadingPatternSetNonStrokingColor;
import static org.sejda.impl.sambox.component.pdfa.ShadingPatternSetColor.shadingPatternSetStrokingColor;

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
            case PDFA_1B -> new NoTransparencyGroupPageRule(context).andThen(new AnnotationsPageRule(context))
                    .andThen(new AnnotationsColorPageRule(context)).andThen(new ActionsPageRule(context));
        };
    }

    public static PreSaveCOSTransformer preSaveCOSTransformer(ConversionContext context) {
        return switch (context.parameters().conformanceLevel()) {
            case PDFA_1B -> new DefaultPreSaveCOSTransformer().withStreamRule(new NoDLStreamRule(context))
                    .withStreamRule(new NoFileSpecificationStreamRule(context))
                    .withStreamRule(new NoLZWCompressionStreamRule(context)).withStreamRule(new XMPStreamRule(context))
                    .withDictionaryRule(new NoEFDictionaryRule(context)).withDictionaryRule((d) -> {
                        if (COSName.FONT.equals(d.getCOSName(COSName.TYPE))) {
                            System.out.println("Font");
                        }
                    });
        };
    }

    public static FailableConsumer<PDPage, IOException> contentStreamRules(ConversionContext context) {
        return switch (context.parameters().conformanceLevel()) {
            case PDFA_1B -> {
                var csProcessor = new PdfAContentStreamProcessor(context);
                csProcessor.addOperator(
                        new XObjectOperator(context).andThen(new ResourcesHitter.XObjectHitterOperator()));
                csProcessor.addConversionOperator(strokingRGB());
                csProcessor.addConversionOperator(nonStrokingRGB());
                csProcessor.addConversionOperator(strokingCMYK());
                csProcessor.addConversionOperator(nonStrokingCMYK());
                csProcessor.addConversionOperator(strokingColorspace());
                csProcessor.addConversionOperator(nonStrokingColorspace());
                csProcessor.addConversionOperator(new SetRenderingIntent());
                csProcessor.addConversionOperator(new InlineImageOperator());
                csProcessor.addConversionOperator(new ShowText());
                csProcessor.addConversionOperator(new ShowTextAdjusted());
                csProcessor.addOperator(new SetFontOperator(context));
                csProcessor.addOperator(new SetGraphicState(context).andThen(new ResourcesHitter.SetGraphicState()));
                //use the same map for stroking and not stroking
                Map<IndirectCOSObjectIdentifier, ReadOnlyFilteredCOSStream> hitPatternsById = new HashMap<>();
                csProcessor.addOperator(tilingPatternSetStrokingColor(hitPatternsById).andThen(
                        shadingPatternSetStrokingColor(context)));
                csProcessor.addOperator(tilingPatternSetNonStrokingColor(hitPatternsById).andThen(
                        shadingPatternSetNonStrokingColor(context)));
                yield csProcessor;
            }
        };
    }
}
