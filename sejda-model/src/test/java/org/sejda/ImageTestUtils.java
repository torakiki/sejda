/*
 * Created on 25/set/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
