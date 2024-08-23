/*
 * Created on 25/07/24
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

import org.sejda.impl.sambox.component.optimization.InUseDictionary;
import org.sejda.sambox.contentstream.operator.MissingOperandException;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.OperatorProcessor;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.cos.COSNumber;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.pdmodel.MissingResourceException;
import org.sejda.sambox.pdmodel.font.PDCIDFontType0;
import org.sejda.sambox.pdmodel.font.PDCIDFontType2;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.font.PDFontFactory;
import org.sejda.sambox.pdmodel.font.PDFontLike;
import org.sejda.sambox.pdmodel.font.PDTrueTypeFont;
import org.sejda.sambox.pdmodel.font.PDType0Font;
import org.sejda.sambox.pdmodel.font.PDType1Font;
import org.sejda.sambox.pdmodel.font.PDType3Font;
import org.sejda.sambox.pdmodel.graphics.state.RenderingMode;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.sejda.commons.util.RequireUtils.require;
import static org.sejda.commons.util.RequireUtils.requireIOCondition;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.sambox.contentstream.operator.OperatorName.SET_FONT_AND_SIZE;

/**
 * @author Andrea Vacondio
 */
public class SetFontOperator extends OperatorProcessor {

    private static final Set<COSName> VALID_SUBTYPES = Set.of(COSName.TYPE0, COSName.TYPE1, COSName.MM_TYPE1,
            COSName.TYPE3, COSName.TRUE_TYPE, COSName.CID_FONT_TYPE0, COSName.CID_FONT_TYPE2);
    private final ConversionContext conversionContext;

    public SetFontOperator(ConversionContext conversionContext) {
        this.conversionContext = conversionContext;
    }

    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        require(operands.size() > 1, () -> new MissingOperandException(operator, operands));

        if (operands.getFirst() instanceof COSName fontName) {
            COSDictionary fontDictionary = ofNullable(
                    fontResources().getDictionaryObject(fontName, COSDictionary.class)).orElseThrow(
                    () -> new MissingResourceException("Missing font dictionary: " + fontName.getName()));
            if (!(fontDictionary instanceof InUseDictionary)) {
                //required in the spec
                fontDictionary.setItem(COSName.TYPE, COSName.FONT);
                var subtype = fontDictionary.getCOSName(COSName.SUBTYPE);
                requireIOCondition(VALID_SUBTYPES.contains(subtype),
                        "Found a font dictionary with invalid subtype " + subtype);
                requireIOCondition(
                        COSName.TYPE3.equals(subtype) || nonNull(fontDictionary.getCOSName(COSName.BASE_FONT)),
                        "Found a font dictionary without the required BaseFont name");

                var font = PDFontFactory.createFont(fontDictionary, getContext().getResources().getResourceCache());
                requireIOCondition(
                        font.isEmbedded() || RenderingMode.NEITHER == getContext().getGraphicsState().getTextState()
                                .getRenderingMode(), "The font " + fontName.getName() + " is not embedded");
                validate(font, fontName.getName());

            }

        }
    }

    public void validate(PDFontLike font, String name) throws IOException {
        switch (font) {
        case PDType1Font type1 -> {
            requireDescriptor(type1.getCOSObject());
            validateWidthsArray(type1);
        }
        case PDType3Font type3 -> {
            validateWidthsArray(type3);
        }
        case PDType0Font type0 -> {
            requireCompatibleCIDFontAndCMap(type0, name);
            validate(type0.getDescendantFont(), name);
        }
        case PDTrueTypeFont ttf -> {
            requireDescriptor(ttf.getCOSObject());
            validateWidthsArray(ttf);
        }
        case PDCIDFontType0 cidType0 -> {
            requireDescriptor(cidType0.getCOSObject());
        }
        case PDCIDFontType2 cidType2 -> {
            validateCidToGidMap(cidType2, name);
            requireDescriptor(cidType2.getCOSObject());
        }
        default -> {
            //noop
        }
        }
    }

    //Rule 6.3.3.1 of ISO 19005-1
    private void requireCompatibleCIDFontAndCMap(PDType0Font font, String name) throws IOException {
        var encoding = font.getCOSObject().getDictionaryObject(COSName.ENCODING);
        //TECHNICAL CORRIGENDUM 2: “unless the value of the Encoding key in the font dictionary is Identity-H or Identity-V”
        if (!COSName.IDENTITY_H.equals(encoding) && !COSName.IDENTITY_V.equals(encoding)) {
            var fontCIDSystemInfo = font.getDescendantFont().getCOSObject()
                    .getDictionaryObject(COSName.CIDSYSTEMINFO, COSDictionary.class);
            requireIOCondition(nonNull(fontCIDSystemInfo), "CIDSystemInfo is required for type 0 fonts");
            if (encoding instanceof COSStream cmap) {
                var cmapCIDSystemInfo = cmap.getDictionaryObject(COSName.CIDSYSTEMINFO, COSDictionary.class);
                requireIOCondition(nonNull(cmapCIDSystemInfo), "CIDSystemInfo is required for type 0 font CMap");
                var cmapRegistry = cmapCIDSystemInfo.getString(COSName.REGISTRY);
                var cmapOrdering = cmapCIDSystemInfo.getString(COSName.ORDERING);
                requireIOCondition(
                        nonNull(cmapRegistry) && cmapRegistry.equals(fontCIDSystemInfo.getString(COSName.REGISTRY)),
                        "Registry string in CIDSystemInfo of the font and CMap are not equals");
                requireIOCondition(
                        nonNull(cmapOrdering) && cmapOrdering.equals(fontCIDSystemInfo.getString(COSName.ORDERING)),
                        "Ordering string in CIDSystemInfo of the font and CMap are not equals");
            } else {
                throw new IOException("Invalid encoding for type 0 font, expected Identity or Stream");
            }
        }
    }

    //Rule 6.3.3.2 of ISO 19005-1
    private void validateCidToGidMap(PDCIDFontType2 font, String name) throws IOException {
        //TECHNICAL CORRIGENDUM 2. This is required for embedded fonts used for rendering
        if (font.isEmbedded()) {
            COSBase map = font.getCOSObject().getDictionaryObject(COSName.CID_TO_GID_MAP);
            if (!(map instanceof COSStream) && !COSName.IDENTITY.equals(map)) {
                conversionContext.maybeFailOnInvalidElement(
                        () -> new IOException("Type 2 CIDFonts shall contain a CIDToGIDMap entry "));
                font.getCOSObject().setItem(COSName.CID_TO_GID_MAP, COSName.IDENTITY);
                notifyEvent(conversionContext.notifiableMetadata()).taskWarning(
                        "Type 2 CIDFonts '" + name + "' CIDToGIDMap set to Identity");
            }
        }
    }

    private void requireDescriptor(COSDictionary font) throws IOException {
        var descriptor = font.getDictionaryObject(COSName.FONT_DESC, COSDictionary.class);
        requireIOCondition(nonNull(descriptor), "Found a font dictionary with missing FontDescriptor dictionary");
        var streamSubtype = ofNullable(descriptor.getDictionaryObject(COSName.FONT_FILE3, COSStream.class)).map(
                s -> s.getCOSName(COSName.SUBTYPE)).orElse(null);
        requireIOCondition(
                isNull(streamSubtype) || COSName.getPDFName("Type1C").equals(streamSubtype) || COSName.getPDFName(
                        "CIDFontType0C").equals(streamSubtype),
                "Found a FontFile3 stream with invalid subtype " + streamSubtype);
    }

    private void validateWidthsArray(PDFont font) throws IOException {
        var fontDictionary = font.getCOSObject();
        requireIOCondition(
                font.isStandard14() || nonNull(fontDictionary.getDictionaryObject(COSName.FIRST_CHAR, COSNumber.class)),
                "Found a font dictionary with missing FirstChar");
        requireIOCondition(
                font.isStandard14() || nonNull(fontDictionary.getDictionaryObject(COSName.LAST_CHAR, COSNumber.class)),
                "Found a font dictionary with missing LastChar");
        var widths = fontDictionary.getDictionaryObject(COSName.WIDTHS, COSArray.class);
        requireIOCondition(font.isStandard14() || (nonNull(widths)),
                "Found a font dictionary with missing Widths array");
        requireIOCondition(font.isStandard14() || widths.size() == (
                        fontDictionary.getInt(COSName.LAST_CHAR) - fontDictionary.getInt(COSName.FIRST_CHAR) + 1),
                "Found a font dictionary with wrong size of the Widths array");
    }

    private COSDictionary fontResources() {
        return getContext().getResources().getCOSObject()
                .computeIfAbsent(COSName.FONT, k -> new COSDictionary(), COSDictionary.class);
    }

    @Override
    public String getName() {
        return SET_FONT_AND_SIZE;
    }
}
