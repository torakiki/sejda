/*
 * Copyright 2016 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.impl.sambox.component;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sejda.sambox.contentstream.PDFStreamEngine;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.contentstream.operator.OperatorProcessor;
import org.sejda.sambox.contentstream.operator.state.Concatenate;
import org.sejda.sambox.contentstream.operator.state.Restore;
import org.sejda.sambox.contentstream.operator.state.Save;
import org.sejda.sambox.contentstream.operator.state.SetGraphicsStateParameters;
import org.sejda.sambox.contentstream.operator.state.SetMatrix;
import org.sejda.sambox.cos.COSBase;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.graphics.PDXObject;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.graphics.image.PDImageXObject;
import org.sejda.sambox.util.Matrix;

public class ImageLocationsExtractor extends PDFStreamEngine {

    private Map<PDPage, List<Rectangle>> imageLocations = new HashMap<>();

    public ImageLocationsExtractor() {
        addOperator(new Concatenate());
        addOperator(new SetGraphicsStateParameters());
        addOperator(new Save());
        addOperator(new Restore());
        addOperator(new SetMatrix());
        addOperator(new XObjectOperator());
    }

    public void process(PDDocument document) throws IOException {
        imageLocations.clear();
        for (PDPage page : document.getPages()) {
            processPage(page);
        }
    }

    private class XObjectOperator extends OperatorProcessor {
        @Override
        public void process(Operator operator, List<COSBase> operands) throws IOException {
            COSName objectName = (COSName) operands.get(0);
            PDXObject xobject = getResources().getXObject(objectName);
            if (xobject instanceof PDImageXObject) {
                Matrix ctmNew = getGraphicsState().getCurrentTransformationMatrix();
                float imageXScale = ctmNew.getScalingFactorX();
                float imageYScale = ctmNew.getScalingFactorY();

                if (!imageLocations.containsKey(getCurrentPage())) {
                    imageLocations.put(getCurrentPage(), new ArrayList<>());
                }
                imageLocations.get(getCurrentPage()).add(new Rectangle((int) ctmNew.getTranslateX(),
                        (int) ctmNew.getTranslateY(), (int) imageXScale, (int) imageYScale));
            } else if (xobject instanceof PDFormXObject) {
                PDFormXObject form = (PDFormXObject) xobject;
                showForm(form);
            }
        }

        @Override
        public String getName() {
            return "Do";
        }
    }

    public Map<PDPage, List<Rectangle>> getImageLocations() {
        return imageLocations;
    }
}
