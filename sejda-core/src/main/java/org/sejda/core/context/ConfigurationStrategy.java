/*
 * Created on 27/apr/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.context;

import java.util.Map;

import org.sejda.core.notification.strategy.NotificationStrategy;
import org.sejda.model.parameter.base.TaskParameters;
import org.sejda.model.task.Task;

/**
 * Strategy used to load the configuration
 * 
 * @author Andrea Vacondio
 * 
 */
interface ConfigurationStrategy {

    /**
     * @return the notification strategy class to use
     */
    Class<? extends NotificationStrategy> getNotificationStrategy();

    /**
     * Retrieves all the configured {@link Task} stored in a map. The map key is the subclass of {@link TaskParameters} that the task can execute.
     * 
     * @return a map containing all the configured {@link Task}
     */
    @SuppressWarnings("rawtypes")
    Map<Class<? extends TaskParameters>, Class<? extends Task>> getTasksMap();

    /**
     * @return true if the validation has to be performed by the framework, false otherwise (incoming requests are already validated externally)
     */
    boolean isValidation();

    /**
     * @return true if the validator should set {@link javax.validation.Configuration#ignoreXmlConfiguration()} to ignore <i>META-INF/validation.xml</i>. This is true by default to
     *         allow a typical configuration (Hibernate-validator and JDK5) to run smooth, it would require JAXB otherwise.
     */
    boolean isIgnoreXmlConfiguration();
}
