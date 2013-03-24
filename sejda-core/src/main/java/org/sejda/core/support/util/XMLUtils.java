/*
 * Created on 25/ott/2011
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
package org.sejda.core.support.util;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

/**
 * Provides some utility methods to deal with xml.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class XMLUtils {
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
}
