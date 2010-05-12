/*
 * Created on 27/apr/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.configuration;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.sejda.core.exception.ConfigurationException;
import org.sejda.core.manipulation.Task;
import org.sejda.core.manipulation.TaskParameters;
import org.sejda.core.notification.strategy.AsyncNotificationStrategy;
import org.sejda.core.notification.strategy.NotificationStrategy;
import org.sejda.core.notification.strategy.SyncNotificationStrategy;

/**
 * Retrieves the configuration from the input xml stream
 * 
 * @author Andrea Vacondio
 * 
 */
@SuppressWarnings("unchecked")
public class XmlConfigurationStrategy implements ConfigurationStrategy {

    private static final String ROOT_NODE = "/sejda";
    private static final String NOTIFICATION_XPATH = "/notification/@async";
    private static final String TASKS_XPATH = "/tasks/task";
    private static final String TASK_PARAM_XPATH = "@parameters";
    private static final String TASK_VALUE_XPATH = "@task";

    private Class<? extends NotificationStrategy> notificationStrategy;
    private Map<Class<? extends TaskParameters>, Class<? extends Task>> tasks;

    /**
     * Creates an instance initialized with the given input stream. The stream is not closed.
     * 
     * @param input
     *            stream to the input xml configuration file
     * @throws ConfigurationException
     *             in case of error parsing the input stream
     */
    public XmlConfigurationStrategy(InputStream input) throws ConfigurationException {
        initializeFromInputStream(input);
    }

    private void initializeFromInputStream(InputStream input) throws ConfigurationException {
        SAXReader reader = new SAXReader();
        Document document;
        try {
            document = reader.read(input);
            notificationStrategy = getNotificationStrategy(document);
            tasks = getTasksMap(document);
        } catch (DocumentException e) {
            throw new ConfigurationException("Error loading the xml input stream", e);
        }

    }

    public Class<? extends NotificationStrategy> getNotificationStrategy() {
        return notificationStrategy;
    }

    public Map<Class<? extends TaskParameters>, Class<? extends Task>> getTasksMap() {
        return tasks;
    }

    private Map<Class<? extends TaskParameters>, Class<? extends Task>> getTasksMap(Document document)
            throws ConfigurationException {
        Map<Class<? extends TaskParameters>, Class<? extends Task>> retMap = new HashMap<Class<? extends TaskParameters>, Class<? extends Task>>();
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
    private <T> Class<? extends T> getClassFromNode(Node node, String xpath, Class<? extends T> assignableInterface)
            throws ConfigurationException {
        Node paramsClassNode = node.selectSingleNode(xpath);
        if (paramsClassNode != null) {
            String paramClass = paramsClassNode.getText().trim();
            Class<?> clazz;
            try {
                clazz = Class.forName(paramClass);
            } catch (ClassNotFoundException e) {
                throw new ConfigurationException(String.format("Unable to find the configured class %s", paramClass), e);
            }
            if (assignableInterface.isAssignableFrom(clazz)) {
                return (Class<? extends T>) clazz;
            } else {
                throw new ConfigurationException(String.format("The configured class %s is not a subtype of %s", clazz,
                        assignableInterface));
            }
        } else {
            throw new ConfigurationException(String.format("Missing %s configuration parameter.", xpath));
        }
    }

    /**
     * Given a document, search for the notification strategy configuration and returns the configured strategy or the default one if nothing is configured.
     * 
     * @param document
     * @return the class extending {@link NotificationStrategy} configured.
     */
    private Class<? extends NotificationStrategy> getNotificationStrategy(Document document) {
        Node node = document.selectSingleNode(ROOT_NODE + NOTIFICATION_XPATH);
        if (node != null) {
            if (Boolean.parseBoolean(node.getText().trim())) {
                return AsyncNotificationStrategy.class;
            }
        }
        return SyncNotificationStrategy.class;
    }
}
