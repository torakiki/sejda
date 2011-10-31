/*
 * Created on 17/apr/2010
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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.sejda.model.task.NotifiableTaskMetadata;

/**
 * Event thrown when an execution completes without errors.
 * 
 * @author Andrea Vacondio
 * 
 */
public class TaskExecutionCompletedEvent extends AbstractNotificationEvent {

    private static final long serialVersionUID = -2839444329684682481L;

    private long executionTime = -1;

    /**
     * Creates an instance specifying the execution time, the number of millis from the task to complete.
     * 
     * @param executionTime
     * @param taskMetadata
     */
    public TaskExecutionCompletedEvent(long executionTime, NotifiableTaskMetadata taskMetadata) {
        super(taskMetadata);
        this.executionTime = executionTime;
    }

    /**
     * 
     * @return the number of millis from the task to complete or -1 if not specified.
     */
    public long getExecutionTime() {
        return executionTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString())
                .append("executionTime", executionTime).toString();
    }

}
