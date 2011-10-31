/*
 * Created on 25/jun/2011
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
package org.sejda.model.notification.event;

import org.sejda.model.task.NotifiableTaskMetadata;

/**
 * Events notified when a task execution starts.
 * 
 * @author Andrea Vacondio
 * 
 */
public class TaskExecutionStartedEvent extends AbstractNotificationEvent {

    private static final long serialVersionUID = -8143994205216959322L;

    public TaskExecutionStartedEvent(NotifiableTaskMetadata taskMetadata) {
        super(taskMetadata);
    }

}
