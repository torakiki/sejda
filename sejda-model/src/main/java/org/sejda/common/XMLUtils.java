/*
 * Created on 25/ott/2011
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
package org.sejda.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Provides some utility methods to deal with xml.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class XMLUtils {

    private static final Logger LOG = LoggerFactory.getLogger(XMLUtils.class);

    private XMLUtils() {
        // hide
    }

    /**
     * @param node
     * @param attributeName
     * @return the String value of the given attributeName for the given node. null if the attributeName is not found or the input node is null.
     */
    public static String nullSafeGetStringAttribute(Node node, String attributeName) {
        if (node != null) {
            Node namedItem = node.getAttributes().getNamedItem(attributeName);
            if (namedItem != null) {
                return namedItem.getNodeValue();
            }
        }
        return null;
    }

    /**
     * @param node
     * @param attributeName
     * @return the boolean value of the given attribute. false if the attribute is not found.
     */
    public static boolean nullSafeGetBooleanAttribute(Node node, String attributeName) {
        return nullSafeGetBooleanAttribute(node, attributeName, false);
    }

    /**
     * @param node
     * @param attributeName
     * @param defaultValue
     * @return the boolean value of the given attribute. the defaultValue if the attribute is not found.
     */
    public static boolean nullSafeGetBooleanAttribute(Node node, String attributeName, boolean defaultValue) {
        String value = nullSafeGetStringAttribute(node, attributeName);
        if (StringUtils.isNotBlank(value)) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    public static final DocumentBuilderFactory getDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setValidating(true);
        factory.setFeature("http://xml.org/sax/features/validation", true);

        return factory;
    }

    public static final ErrorHandler ERROR_HANDLER = new ErrorHandler() {

        @Override
        public void warning(SAXParseException e) throws SAXException {
            // noop
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            LOG.debug(e.getMessage());
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            throw e;
        }
    };
}
