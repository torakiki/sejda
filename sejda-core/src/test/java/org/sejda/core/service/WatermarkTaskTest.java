/*
 * Created on 21 ott 2016
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
package org.sejda.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.sejda.io.SeekableSources;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.parameter.WatermarkParameters;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.watermark.Location;
import org.sejda.sambox.contentstream.operator.Operator;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.input.ContentStreamParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.common.PDStream;

/**
 * @author Andrea Vacondio
 *
 */
@Ignore
public abstract class WatermarkTaskTest extends BaseTaskTest<WatermarkParameters> {

    private WatermarkParameters pngParams() throws IOException {
        WatermarkParameters parameters = new WatermarkParameters(customNonPdfInput("image/draft.png"));
        setUpParams(parameters);
        return parameters;
    }

    private WatermarkParameters tiffParams() throws IOException {
        WatermarkParameters parameters = new WatermarkParameters(customNonPdfInput("image/draft.tiff"));
        setUpParams(parameters);
        return parameters;
    }

    private void setUpParams(WatermarkParameters parameters) throws IOException {
        parameters.setCompress(true);
        parameters.setPosition(new Point(10, 50));
        parameters.setOpacity(40);
        testContext.directoryOutputTo(parameters);
        parameters.setOutputPrefix("test_file[FILENUMBER]");
        parameters.addSource(customInput("pdf/test_file.pdf"));
        parameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
    }

    @Test
    public void testAddingPngImage() throws Exception {
        WatermarkParameters parameters = pngParams();
        parameters.addPageRange(new PageRange(2, 3));
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput(d -> {
            assertNotNull(d.getPage(1).getResources().getCOSObject().getDictionaryObject(COSName.XOBJECT,
                    COSDictionary.class));
            assertNotNull(d.getPage(2).getResources().getCOSObject().getDictionaryObject(COSName.XOBJECT,
                    COSDictionary.class));
            assertNoImageAtLocation(d, d.getPage(0), new Rectangle(10, 50, 248, 103));
            assertImageAtLocation(d, d.getPage(1), new Rectangle(10, 50, 248, 103));
            assertImageAtLocation(d, d.getPage(2), new Rectangle(10, 50, 248, 103));
            assertNoImageAtLocation(d, d.getPage(3), new Rectangle(10, 50, 248, 103));
        });
    }

    @Test
    public void testAddingTiffImage() throws Exception {
        WatermarkParameters parameters = tiffParams();
        parameters.addPageRange(new PageRange(2, 3));
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput(d -> {
            assertNotNull(d.getPage(1).getResources().getCOSObject().getDictionaryObject(COSName.XOBJECT,
                    COSDictionary.class));
            assertNotNull(d.getPage(2).getResources().getCOSObject().getDictionaryObject(COSName.XOBJECT,
                    COSDictionary.class));
            assertNoImageAtLocation(d, d.getPage(0), new Rectangle(10, 50, 248, 103));
            assertImageAtLocation(d, d.getPage(1), new Rectangle(10, 50, 248, 103));
            assertImageAtLocation(d, d.getPage(2), new Rectangle(10, 50, 248, 103));
            assertNoImageAtLocation(d, d.getPage(3), new Rectangle(10, 50, 248, 103));
        });
    }

    @Test
    public void testAddingPngImageScaled() throws Exception {
        WatermarkParameters parameters = new WatermarkParameters(customNonPdfInput("image/draft.png"));
        setUpParams(parameters);
        parameters.setDimension(new Dimension(200, 83));
        parameters.addPageRange(new PageRange(1, 1));
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput(d -> {
            assertNotNull(d.getPage(0).getResources().getCOSObject().getDictionaryObject(COSName.XOBJECT,
                    COSDictionary.class));
            assertImageAtLocation(d, d.getPage(0), new Rectangle(10, 50, 200, 83));
            assertNoImageAtLocation(d, d.getPage(1), new Rectangle(10, 50, 200, 83));
            assertNoImageAtLocation(d, d.getPage(2), new Rectangle(10, 50, 200, 83));
            assertNoImageAtLocation(d, d.getPage(3), new Rectangle(10, 50, 200, 83));
        });
    }

    @Test
    public void testRotation() throws Exception {
        WatermarkParameters parameters = new WatermarkParameters(customNonPdfInput("image/draft.png"));
        setUpParams(parameters);
        parameters.setDimension(new Dimension(200, 83));
        parameters.setRotationDegrees(45);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput(d -> {
            assertImageAtLocation(d, d.getPage(0), new Rectangle(68, -8, 200, 83));
        });
    }

    @Test
    public void testLocation() throws IOException {
        WatermarkParameters parameters = pngParams();
        parameters.addPageRange(new PageRange(1, 1));
        parameters.setLocation(Location.OVER);
        parameters.setOpacity(100);
        execute(parameters);
        testContext.assertTaskCompleted();

        testContext.forPdfOutput(d -> {
            Iterator<PDStream> iter = d.getPage(0).getContentStreams();
            PDStream formPrintStream = null;
            while (iter.hasNext()) {
                formPrintStream = iter.next();
            }
            try (ContentStreamParser parser = new ContentStreamParser(
                    SeekableSources.inMemorySeekableSourceFrom(formPrintStream.createInputStream()))) {
                List<Object> tokens = parser.tokens();
                assertEquals(12, tokens.size());
                assertEquals("Form1", ((COSName) tokens.get(9)).getName());
                assertEquals("Do", ((Operator) tokens.get(10)).getName());
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void testOpacity() throws IOException {
        WatermarkParameters parameters = pngParams();
        parameters.addPageRange(new PageRange(1, 1));
        parameters.setLocation(Location.BEHIND);
        parameters.setOpacity(30);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput(d -> {
            PDStream formPrintStream = d.getPage(0).getContentStreams().next();
            try (ContentStreamParser parser = new ContentStreamParser(
                    SeekableSources.inMemorySeekableSourceFrom(formPrintStream.createInputStream()))) {
                List<Object> tokens = parser.tokens();
                assertEquals(13, tokens.size());
                assertEquals("gs1", ((COSName) tokens.get(8)).getName());
                assertEquals("gs", ((Operator) tokens.get(9)).getName());
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void testNoOpacity() throws IOException {
        WatermarkParameters parameters = pngParams();
        parameters.addPageRange(new PageRange(1, 1));
        parameters.setLocation(Location.BEHIND);
        execute(parameters);
        testContext.assertTaskCompleted();
        testContext.forPdfOutput(d -> {
            PDStream formPrintStream = d.getPage(0).getContentStreams().next();
            try (ContentStreamParser parser = new ContentStreamParser(
                    SeekableSources.inMemorySeekableSourceFrom(formPrintStream.createInputStream()))) {
                parser.tokens().stream().filter(t -> t instanceof Operator).map(t -> (Operator) t)
                        .noneMatch(o -> "gs".equals(o.getName()));

            } catch (IOException e) {
                fail(e.getMessage());
            }
        });
    }

    protected abstract void assertImageAtLocation(PDDocument document, PDPage page, Rectangle rectangle);

    protected abstract void assertNoImageAtLocation(PDDocument document, PDPage page, Rectangle rectangle);
}
