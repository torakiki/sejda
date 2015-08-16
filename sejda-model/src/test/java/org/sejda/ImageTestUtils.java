/*
 * Created on 25/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda;

import static org.junit.Assert.fail;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.xml.transform.Source;

import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageManager;
import org.apache.xmlgraphics.image.loader.ImageSessionContext;
import org.apache.xmlgraphics.image.loader.ImageSource;
import org.apache.xmlgraphics.image.loader.impl.DefaultImageContext;
import org.apache.xmlgraphics.image.loader.impl.DefaultImageSessionContext;
import org.apache.xmlgraphics.image.loader.impl.ImageRendered;

/**
 * @author Andrea Vacondio
 * 
 */
public final class ImageTestUtils {

    private ImageTestUtils() {
        // hide
    }

    /**
     * Loads an image file.
     * 
     * @param image
     * @return
     * @throws ImageException
     * @throws IOException
     */
    public static RenderedImage loadImage(File image) throws IOException {
        ImageManager imageManager = new ImageManager(new DefaultImageContext());
        ImageSessionContext sessionContext = new DefaultImageSessionContext(imageManager.getImageContext(), null);

        ImageInfo info = null;
        Image img = null;
        try {
            info = imageManager.preloadImage(image.toURI().toString(), sessionContext);
            img = imageManager.getImage(info, ImageFlavor.RENDERED_IMAGE, sessionContext);
        } catch (ImageException e) {
            fail(e.getMessage());
            throw new RuntimeException(e);
        }
        ImageRendered imageRend = (ImageRendered) img;
        return imageRend.getRenderedImage();
    }

    /**
     * Loads an image file from an input stream.
     * 
     * @param image
     * @return
     * @throws ImageException
     * @throws IOException
     */
    public static RenderedImage loadImage(InputStream image, String name) throws IOException {
        ImageManager imageManager = new ImageManager(new DefaultImageContext());
        ImageSessionContext sessionContext = new DefaultImageSessionContext(imageManager.getImageContext(), null);
        ImageInputStream imageInputStream = new MemoryCacheImageInputStream(image);
        Source source = new ImageSource(imageInputStream, name, true);
        sessionContext.returnSource(name, source);
        ImageInfo info = null;
        Image img = null;
        try {
            info = imageManager.preloadImage(name, source);
            img = imageManager.getImage(info, ImageFlavor.RENDERED_IMAGE, sessionContext);
        } catch (ImageException e) {
            fail(e.getMessage());
            throw new RuntimeException(e);
        }
        ImageRendered imageRend = (ImageRendered) img;
        return imageRend.getRenderedImage();
    }
}
