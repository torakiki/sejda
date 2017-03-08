/*
 * Created on 20 ott 2016
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
package org.sejda.impl.sambox.component;

import static java.util.Optional.ofNullable;

import java.awt.geom.AffineTransform;
import java.io.IOException;

import org.sejda.model.exception.TaskIOException;
import org.sejda.model.parameter.WatermarkParameters;
import org.sejda.model.watermark.Location;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDPageContentStream;
import org.sejda.sambox.pdmodel.PDResources;
import org.sejda.sambox.pdmodel.common.PDRectangle;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;
import org.sejda.sambox.pdmodel.graphics.form.PDTransparencyGroupAttributes;
import org.sejda.sambox.pdmodel.graphics.image.PDImageXObject;
import org.sejda.sambox.pdmodel.graphics.state.PDExtendedGraphicsState;

/**
 * Component responsible for adding a watermark to given pages
 * 
 * @author Andrea Vacondio
 *
 */
public class PdfWatermarker {

    private PageImageWriter imageWriter;
    private PDFormXObject form;
    private WatermarkParameters parameters;

    public PdfWatermarker(WatermarkParameters parameters, PDDocument document) throws TaskIOException {
        this.imageWriter = new PageImageWriter(document);
        this.parameters = parameters;
        PDImageXObject watermark = PageImageWriter.toPDXImageObject(parameters.getWatermark());
        form = new PDFormXObject();
        form.setResources(new PDResources());

        PDTransparencyGroupAttributes group = new PDTransparencyGroupAttributes();
        group.setKnockout();
        this.form.setGroup(group);

        PDRectangle bbox = ofNullable(parameters.getDimension())
                .map(d -> new PDRectangle((float) d.getWidth(), (float) d.getHeight()))
                .orElseGet(() -> new PDRectangle(watermark.getWidth(), watermark.getHeight()));
        form.setBBox(bbox);

        int degrees = parameters.getRotationDegrees();
        while(degrees > 360) {
            degrees -= 360;
        }
        while(degrees < 0) {
            degrees += 360;
        }

        if(degrees != 0) {
            AffineTransform at = form.getMatrix().createAffineTransform();
            double radians = degrees * Math.PI / 180;
            at.rotate(radians, bbox.getWidth() / 2, bbox.getHeight() / 2);
            form.setMatrix(at);
        }

        try (PDPageContentStream contentStream = new PDPageContentStream(document, form)) {
            contentStream.drawImage(watermark, 0, 0, form.getBBox().getWidth(), form.getBBox().getHeight());
        } catch (IOException e) {
            throw new TaskIOException("An error occurred writing form xobject stream.", e);
        }
    }

    public void mark(PDPage page) throws TaskIOException {
        PDExtendedGraphicsState gs = null;
        if (parameters.getOpacity() != 100) {
            gs = new PDExtendedGraphicsState();
            float alpha = (float) parameters.getOpacity() / 100;
            gs.setStrokingAlphaConstant(alpha);
            gs.setNonStrokingAlphaConstant(alpha);
        }
        if (parameters.getLocation() == Location.BEHIND) {
            imageWriter.prepend(page, form, parameters.getPosition(), 1, 1, gs, 0);
        } else {
            imageWriter.append(page, form, parameters.getPosition(), 1, 1, gs, 0);
        }
    }

}
