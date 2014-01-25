/*
 * Created on 27/apr/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.context;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.sejda.common.XMLUtils.nullSafeGetBooleanAttribute;
import static org.sejda.common.XMLUtils.nullSafeGetStringAttribute;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.sejda.core.notification.strategy.AsyncNotificationStrategy;
import org.sejda.core.notification.strategy.NotificationStrategy;
import org.sejda.core.notification.strategy.SyncNotificationStrategy;
import org.sejda.model.exception.ConfigurationException;
import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.task.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Retrieves the configuration from the input xml stream
 * 
 * @author Andrea Vacondio
 * 
 */

final class XmlConfigurationStrategy implements ConfigurationStrategy {

    /**
     * system property as a switch to disable schema validation
     */
    private static final String PERFORM_SCHEMA_VALIDATION_PROPERTY = "sejda.perform.schema.validation";

    private static final String ROOT_NODE = "/sejda";
    private static final String VALIDATION_ATTRIBUTENAME = "validation";
    private static final String IGNORE_XML_CONFIG_VALIDATION_ATTRIBUTENAME = "ignore_xml_config";
    private static final String NOTIFICATION_XPATH = "/notification";
    private static final String NOTIFICATION_ASYNC_ATTRIBUTENAME = "async";
    private static final String TASKS_XPATH = "/tasks/task";
    private static final String TASK_PARAM_ATTRIBUTENAME = "parameters";
    private static final String TASK_VALUE_ATTRIBUTENAME = "task";
    private static final String DEFAULT_SEJDA_CONFIG = "sejda.xsd";

    private XPathFactory xpathFactory = XPathFactory.newInstance();
    private Class<? extends NotificationStrategy> notificationStrategy;
    @SuppressWarnings("rawtypes")
    private Map<Class<? extends TaskParameters>, Class<? extends Task>> tasks;
    private boolean validation = false;
    private boolean ignoreXmlConfig = true;

    /**
     * Creates an instance initialized with the given input stream. The stream is not closed.
     * 
     * @param input
     *            stream to the input xml configuration file
     * @throws ConfigurationException
     *             in case of error parsing the input stream
     */
    private XmlConfigurationStrategy(InputStream input) throws ConfigurationException {
        initializeFromInputStream(input);
    }

    private void initializeFromInputStream(InputStream input) throws ConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            initializeSchemaValidation(factory);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(input);

            notificationStrategy = getNotificationStrategy(document);
            tasks = getTasksMap(document);
            validation = getValidation(document);
            ignoreXmlConfig = getIgnoreXmlConfig(document);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        } catch (SAXException e) {
            throw new ConfigurationException(e);
        } catch (ParserConfigurationException e) {
            throw new ConfigurationException("Unable to create DocumentBuilder.", e);
        } catch (XPathExpressionException e) {
            throw new ConfigurationException("Unable evaluate xpath expression.", e);
        }
    }

    private void initializeSchemaValidation(DocumentBuilderFactory factory) throws SAXException {
        if (Boolean.valueOf(System.getProperty(PERFORM_SCHEMA_VALIDATION_PROPERTY, "true"))) {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            factory.setSchema(schemaFactory.newSchema(new Source[] { new StreamSource(Thread.currentThread()
                    .getContextClassLoader().getResourceAsStream(DEFAULT_SEJDA_CONFIG)) }));

            factory.setNamespaceAware(true);
        }
    }

    public Class<? extends NotificationStrategy> getNotificationStrategy() {
        return notificationStrategy;
    }

    @SuppressWarnings("rawtypes")
    public Map<Class<? extends TaskParameters>, Class<? extends Task>> getTasksMap() {
        return tasks;
    }

    public boolean isValidation() {
        return validation;
    }

    public boolean isIgnoreXmlConfiguration() {
        return ignoreXmlConfig;
    }

    @SuppressWarnings("rawtypes")
    private Map<Class<? extends TaskParameters>, Class<? extends Task>> getTasksMap(Document document)
            throws ConfigurationException, XPathExpressionException {
        Map<Class<? extends TaskParameters>, Class<? extends Task>> retMap = new HashMap<Class<? extends TaskParameters>, Class<? extends Task>>();
        NodeList nodes = (NodeList) xpathFactory.newXPath().evaluate(ROOT_NODE + TASKS_XPATH, document,
                XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Class<? extends TaskParameters> paramClass = getClassFromNode(node, TASK_PARAM_ATTRIBUTENAME,
                    TaskParameters.class);
            Class<? extends Task> taksClass = getClassFromNode(node, TASK_VALUE_ATTRIBUTENAME, Task.class);
            retMap.put(paramClass, taksClass);

        }
        return retMap;
    }

    /**
     * Retrieves the value of the input xpath in the given node, creates a Class object and performs a check to ensure that the input assignableInterface is assignable by the
     * created Class object.
     * 
     * @param <T>
     * 
     * @param node
     * @param attributeName
     * @param assignableInterface
     * @return the retrieved class.
     * @throws ConfigurationException
     */
    private <T> Class<? extends T> getClassFromNode(Node node, String attributeName, Class<T> assignableInterface)
            throws ConfigurationException {
        String attributeValue = nullSafeGetStringAttribute(node, attributeName);
        if (isNotBlank(attributeValue)) {
            Class<?> clazz;
            try {
                clazz = Class.forName(attributeValue.trim());
            } catch (ClassNotFoundException e) {
                throw new ConfigurationException(String.format("Unable to find the configured %s", attributeValue), e);
            }
            if (assignableInterface.isAssignableFrom(clazz)) {
                return clazz.asSubclass(assignableInterface);
            }
            throw new ConfigurationException(String.format("The configured %s is not a subtype of %s", clazz,
                    assignableInterface));
        }
        throw new ConfigurationException(String.format("Missing %s configuration parameter.", attributeName));
    }

    /**
     * Given a document, search for the notification strategy configuration and returns the configured strategy or the default one if nothing is configured.
     * 
     * @param document
     * @return the class extending {@link NotificationStrategy} configured.
     * @throws XPathExpressionException
     */
    private Class<? extends NotificationStrategy> getNotificationStrategy(Document document)
            throws XPathExpressionException {
        Node node = (Node) xpathFactory.newXPath().evaluate(ROOT_NODE + NOTIFICATION_XPATH, document,
                XPathConstants.NODE);
        if (nullSafeGetBooleanAttribute(node, NOTIFICATION_ASYNC_ATTRIBUTENAME)) {
            return AsyncNotificationStrategy.class;
        }
        return SyncNotificationStrategy.class;
    }

    private boolean getValidation(Document document) throws XPathExpressionException {
        Node node = (Node) xpathFactory.newXPath().evaluate(ROOT_NODE, document, XPathConstants.NODE);
        return nullSafeGetBooleanAttribute(node, VALIDATION_ATTRIBUTENAME);
    }

    private boolean getIgnoreXmlConfig(Document document) throws XPathExpressionException {
        Node node = (Node) xpathFactory.newXPath().evaluate(ROOT_NODE, document, XPathConstants.NODE);
        return nullSafeGetBooleanAttribute(node, IGNORE_XML_CONFIG_VALIDATION_ATTRIBUTENAME, true);
    }

    /**
     * static factory method.
     * 
     * @param provider
     *            provider for the configuration stream.
     * @return the new instance.
     * @throws ConfigurationException
     */
    static XmlConfigurationStrategy newInstance(ConfigurationStreamProvider provider) throws ConfigurationException {
        InputStream stream = null;
        try {
            stream = provider.getConfigurationStream();
            return new XmlConfigurationStrategy(stream);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }
}
