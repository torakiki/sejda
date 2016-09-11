/*
 * Created on Oct 11, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.sejda.model.exception.InvalidTaskParametersException;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.TaskExecutionFailedEvent;

/**
 * Default listener for the {@link TaskExecutionFailedEvent}
 * 
 * @author Eduard Weissmann
 * 
 */
public class DefaultTaskExecutionFailedEventListener implements EventListener<TaskExecutionFailedEvent> {

    @Override
    public void onEvent(TaskExecutionFailedEvent event) {
        String failingCauseMessage = extractFailingCauseMessage(event);
        throw new SejdaRuntimeException("Task failed. Reason was: " + failingCauseMessage, event.getFailingCause());
    }

    /**
     * @param event
     * @return string containing the message in the failing cause exception of the event
     */
    private String extractFailingCauseMessage(TaskExecutionFailedEvent event) {
        if(event.getFailingCause() instanceof InvalidTaskParametersException) {
            return String.join(". ", ((InvalidTaskParametersException)event.getFailingCause()).getReasons());
        }
        return ExceptionUtils.getMessage(event.getFailingCause());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getClass()).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof DefaultTaskExecutionFailedEventListener)) {
            return false;
        }

        return new EqualsBuilder().append(getClass(), other.getClass()).isEquals();
    }
}
