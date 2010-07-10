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
 */
package org.sejda.core.context;

import org.sejda.core.configuration.GlobalConfiguration;
import org.sejda.core.manipulation.registry.TasksRegistry;
import org.sejda.core.notification.strategy.NotificationStrategy;

/**
 * Abstract implementation of the ApplicationContext. Other contexts can extend this abstract class to access the configuration.
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractApplicationContext implements ApplicationContext {

    public final Class<? extends NotificationStrategy> getNotificationStrategy() {
        return GlobalConfiguration.getInstance().getNotificationStrategy();
    }

    public final TasksRegistry getTasksRegistry() {
        return GlobalConfiguration.getInstance().getTaskRegistry();
    }
}
