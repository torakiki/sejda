/*
 * Created on Oct 11, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
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
package org.sejda.cli;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.notification.EventListener;
import org.sejda.core.notification.event.TaskExecutionFailedEvent;

/**
 * Default listener for the {@link TaskExecutionFailedEvent}
 * 
 * @author Eduard Weissmann
 * 
 */
public class DefaultTaskExecutionFailedEventListener implements EventListener<TaskExecutionFailedEvent> {

    public void onEvent(TaskExecutionFailedEvent event) {
        String failingCauseMessage = extractFailingCauseMessage(event);
        throw new SejdaRuntimeException("Task failed. Reason was: " + failingCauseMessage, event.getFailingCause());
    }

    /**
     * @param event
     * @return string containing the message in the failing cause exception of the event
     */
    private String extractFailingCauseMessage(TaskExecutionFailedEvent event) {
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
