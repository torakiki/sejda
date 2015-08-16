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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.sejda.common.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author Andrea Vacondio
 * 
 */
public class XMLUtilsTest {

    private Document doc;

    @Before
    public void setUp() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        doc = builder.parse(getClass().getClassLoader().getResourceAsStream("test.xml"));
    }

    @Test
    public void nullSafeGetStringAttribute() throws XPathExpressionException {
        Node node = (Node) XPathFactory.newInstance().newXPath().evaluate("/test", doc, XPathConstants.NODE);
        assertEquals("stringValue", XMLUtils.nullSafeGetStringAttribute(node, "stringAttr"));
        assertNull(XMLUtils.nullSafeGetStringAttribute(node, "chuckNorris"));
        Node node2 = (Node) XPathFactory.newInstance().newXPath().evaluate("/chuckNorris", doc, XPathConstants.NODE);
        assertNull(node2);
        assertNull(XMLUtils.nullSafeGetStringAttribute(node2, "kick"));
    }

    @Test
    public void nullSafeGetBooleanAttribute() throws XPathExpressionException {
        Node node = (Node) XPathFactory.newInstance().newXPath().evaluate("/test", doc, XPathConstants.NODE);
        assertTrue(XMLUtils.nullSafeGetBooleanAttribute(node, "boolVal"));
        assertFalse(XMLUtils.nullSafeGetBooleanAttribute(node, "chuckNorris"));
    }

    @Test
    public void nullSafeGetBooleanAttributeWithDefault() throws XPathExpressionException {
        Node node = (Node) XPathFactory.newInstance().newXPath().evaluate("/test", doc, XPathConstants.NODE);
        assertTrue(XMLUtils.nullSafeGetBooleanAttribute(node, "chuckNorris", true));
    }
}
