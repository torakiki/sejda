/*
 * Created on 18/apr/2010
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
 * 
 */
package org.sejda.core.context;

import org.sejda.core.manipulation.registry.TasksRegistry;
import org.sejda.core.notification.strategy.NotificationStrategy;

/**
 * Parent Context interface. Provides a configuration for the application.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface ApplicationContext {

    /**
     * @return the configured strategy to use during listeners notification.
     */
    NotificationStrategy getNotificationStrategy();
    
    /**
     * @return the registry of the configured tasks
     */
    TasksRegistry getTasksRegistry();
}
