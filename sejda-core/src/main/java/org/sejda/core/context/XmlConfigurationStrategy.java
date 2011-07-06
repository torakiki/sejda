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

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.sejda.core.exception.ConfigurationException;
import org.sejda.core.manipulation.model.parameter.TaskParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.notification.strategy.AsyncNotificationStrategy;
import org.sejda.core.notification.strategy.NotificationStrategy;
import org.sejda.core.notification.strategy.SyncNotificationStrategy;
import org.xml.sax.SAXException;

/**
 * Retrieves the configuration from the input xml stream
 * 
 * @author Andrea Vacondio
 * 
 */

class XmlConfigurationStrategy implements ConfigurationStrategy {

    private static final String ROOT_NODE = "/sejda";
    private static final String VALIDATION_XPATH = "/@validation";
    private static final String NOTIFICATION_XPATH = "/notification/@async";
    private static final String TASKS_XPATH = "/tasks/task";
    private static final String TASK_PARAM_XPATH = "@parameters";
    private static final String TASK_VALUE_XPATH = "@task";
    private static final String DEFAULT_SEJDA_CONFIG = "sejda.xsd";

    private Class<? extends NotificationStrategy> notificationStrategy;
    @SuppressWarnings("rawtypes")
    private Map<Class<? extends TaskParameters>, Class<? extends Task>> tasks;
    private boolean validation = false;

    /**
     * Creates an instance initialized with the given input stream. The stream is not closed.
     * 
     * @param input
     *            stream to the input xml configuration file
     * @throws ConfigurationException
     *             in case of error parsing the input stream
     */
    XmlConfigurationStrategy(InputStream input) throws ConfigurationException {
        initializeFromInputStream(input);
    }

    private void initializeFromInputStream(InputStream input) throws ConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

            factory.setSchema(schemaFactory.newSchema(new Source[] { new StreamSource(ClassLoader
                    .getSystemResourceAsStream(DEFAULT_SEJDA_CONFIG)) }));

            SAXReader reader = new SAXReader(factory.newSAXParser().getXMLReader());
            reader.setValidation(false);

            Document document = reader.read(input);

            notificationStrategy = getNotificationStrategy(document);
            tasks = getTasksMap(document);
            validation = getBooleanValueFromXPath(ROOT_NODE + VALIDATION_XPATH, document);
        } catch (DocumentException e) {
            throw new ConfigurationException("Error loading the xml input stream.", e);
        } catch (SAXException e) {
            throw new ConfigurationException("Error processing the xml schema input stream.", e);
        } catch (ParserConfigurationException e) {
            throw new ConfigurationException("Unable to create SAX parser.", e);
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

    @SuppressWarnings("rawtypes")
    private Map<Class<? extends TaskParameters>, Class<? extends Task>> getTasksMap(Document document)
            throws ConfigurationException {
        Map<Class<? extends TaskParameters>, Class<? extends Task>> retMap = new HashMap<Class<? extends TaskParameters>, Class<? extends Task>>();
        @SuppressWarnings("unchecked")
        List<Node> nodes = document.selectNodes(ROOT_NODE + TASKS_XPATH);
        for (Node node : nodes) {
            Class<? extends TaskParameters> paramClass = getClassFromNode(node, TASK_PARAM_XPATH, TaskParameters.class);
            Class<? extends Task> taksClass = getClassFromNode(node, TASK_VALUE_XPATH, Task.class);
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
     * @param xpath
     * @param assignableInterface
     * @return the retrieved class.
     * @throws ConfigurationException
     */
    private <T> Class<? extends T> getClassFromNode(Node node, String xpath, Class<T> assignableInterface)
            throws ConfigurationException {
        Node paramsClassNode = node.selectSingleNode(xpath);
        if (paramsClassNode != null) {
            String paramClass = paramsClassNode.getText().trim();
            Class<?> clazz;
            try {
                clazz = Class.forName(paramClass);
            } catch (ClassNotFoundException e) {
                throw new ConfigurationException(String.format("Unable to find the configured %s", paramClass), e);
            }
            if (assignableInterface.isAssignableFrom(clazz)) {
                return clazz.asSubclass(assignableInterface);
            }
            throw new ConfigurationException(String.format("The configured %s is not a subtype of %s", clazz,
                    assignableInterface));
        }
        throw new ConfigurationException(String.format("Missing %s configuration parameter.", xpath));
    }

    /**
     * Given a document, search for the notification strategy configuration and returns the configured strategy or the default one if nothing is configured.
     * 
     * @param document
     * @return the class extending {@link NotificationStrategy} configured.
     */
    private Class<? extends NotificationStrategy> getNotificationStrategy(Document document) {
        if (getBooleanValueFromXPath(ROOT_NODE + NOTIFICATION_XPATH, document)) {
            return AsyncNotificationStrategy.class;
        }
        return SyncNotificationStrategy.class;
    }

    /**
     * @param XPath
     * @param document
     * @return a boolean value from the given xpath that should point to a true/false attribute
     */
    private boolean getBooleanValueFromXPath(String xpath, Document document) {
        boolean retVal = false;
        Node node = document.selectSingleNode(xpath);
        if (node != null) {
            retVal = Boolean.parseBoolean(node.getText().trim());
        }
        return retVal;
    }
}
