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

import java.util.Map;

import org.sejda.core.manipulation.Task;
import org.sejda.core.manipulation.TaskParameters;
import org.sejda.core.notification.strategy.NotificationStrategy;

/**
 * Strategy used to load the configuration
 * 
 * @author Andrea Vacondio
 * 
 */
public interface ConfigurationStrategy {

    /**
     * @return the notification strategy class to use
     */
    Class<? extends NotificationStrategy> getNotificationStrategy();

    /**
     * Retrieves all the configured {@link Task} stored in a map. The map key is the subclass of {@link TaskParameters}
     * that the task can execute.
     * 
     * @return a map containing all the configured {@link Task}
     */
    @SuppressWarnings("unchecked")
    Map<Class<? extends TaskParameters>, Class<? extends Task>> getTasksMap();
}
