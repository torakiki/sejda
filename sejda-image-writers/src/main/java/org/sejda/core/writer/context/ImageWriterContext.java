/*
 * Created on 20/set/2011
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
package org.sejda.core.writer.context;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.sejda.core.writer.model.ImageWriterAbstractFactory;
import org.sejda.core.writer.xmlgraphics.ImageWriterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Image Writer Context used to get the proper {@link ImageWriterAbstractFactory}. A custom factory class can be supplied using the system property
 * "sejda.image.writer.factory.class". If a custom factory class is provided and an error occur during the creation, the default factory is used.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class ImageWriterContext {

    private static final Logger LOG = LoggerFactory.getLogger(ImageWriterContext.class);

    private static final String IMAGE_WRITER_FACTORY_CLASS = "sejda.image.writer.factory.class";
    /**
     * @deprecated use IMAGE_WRITER_FACTORY_CLASS
     */
    private static final String OLD_IMAGE_WRITER_FACTORY_CLASS = "org.sejda.image.writer.factory.class";

    private final ImageWriterAbstractFactory factory;
    private final ImageWriterAbstractFactory defaultFactory;

    public static ImageWriterContext getContext() {
        return DefaultImageWriterFactoryContextHolder.IMAGE_WRITER_CONTEXT;
    }

    private ImageWriterContext() {
        factory = newImageWriterFactory();
        defaultFactory = new ImageWriterFactory();
    }

    /**
     * @return the shared instance of the {@link ImageWriterAbstractFactory}.
     */
    public ImageWriterAbstractFactory getImageWriterFactory() {
        return factory;
    }

    /**
     * @return the shared instance of the default {@link ImageWriterAbstractFactory}.
     */
    public ImageWriterAbstractFactory getDefaultImageWriterFactory() {
        return defaultFactory;
    }

    private static ImageWriterAbstractFactory newImageWriterFactory() {
        ImageWriterAbstractFactory retVal = newNonDefaultFactory();
        if (retVal != null) {
            return retVal;
        }
        LOG.trace("Creating default ImageWriterAbstractFactory.");
        return new ImageWriterFactory();
    }

    private static ImageWriterAbstractFactory newNonDefaultFactory() {
        ImageWriterAbstractFactory retVal = null;
        String factoryClassString = defaultString(System.getProperty(IMAGE_WRITER_FACTORY_CLASS),
                System.getProperty(OLD_IMAGE_WRITER_FACTORY_CLASS));

        if (isNotBlank(factoryClassString)) {
            LOG.trace("Instantiating custom ImageWriterAbstractFactory: {}", factoryClassString);
            try {
                Constructor<?> constructor = findConstructor(factoryClassString);
                if (constructor != null) {
                    retVal = ImageWriterAbstractFactory.class.cast(constructor.newInstance());
                }
            } catch (InvocationTargetException e) {
                LOG.warn("An exception occured instantiating custom ImageWriterAbstractFactory.", e);
            } catch (InstantiationException e) {
                LOG.warn("Unable to instantiate custom ImageWriterAbstractFactory.", e);
            } catch (IllegalAccessException e) {
                LOG.warn("Unable to access the constructor for custom ImageWriterAbstractFactory.", e);
            }
        }
        return retVal;
    }

    private static Constructor<?> findConstructor(String factoryClassString) {
        try {
            Class<?> factoryClass = Class.forName(factoryClassString);
            return factoryClass.getConstructor();
        } catch (ClassNotFoundException e) {
            LOG.warn("Custom ImageWriterAbstractFactory class not found.", e);
        } catch (SecurityException e) {
            LOG.warn("Error finding the constructor for custom ImageWriterAbstractFactory.", e);
        } catch (NoSuchMethodException e) {
            LOG.warn("Unable to find constructor for custom ImageWriterAbstractFactory.", e);
        }

        return null;
    }

    /**
     * Lazy initialization holder class
     * 
     * @author Andrea Vacondio
     * 
     */
    private static final class DefaultImageWriterFactoryContextHolder {

        private DefaultImageWriterFactoryContextHolder() {
            // hide constructor
        }

        static final ImageWriterContext IMAGE_WRITER_CONTEXT = new ImageWriterContext();
    }
}
