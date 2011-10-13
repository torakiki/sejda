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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.core.notification.EventListener;
import org.sejda.core.notification.event.PercentageOfWorkDoneChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener for the {@link PercentageOfWorkDoneChangedEvent} that logs a message containing the percentage done
 * 
 * @author Eduard Weissmann
 * 
 */
final class LoggingPercentageOfWorkDoneChangeEventListener implements EventListener<PercentageOfWorkDoneChangedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingPercentageOfWorkDoneChangeEventListener.class);

    public void onEvent(PercentageOfWorkDoneChangedEvent event) {
        LOG.info("Task progress: " + event.getPercentage().toPlainString() + "% done");
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

        if (!(other instanceof LoggingPercentageOfWorkDoneChangeEventListener)) {
            return false;
        }

        return new EqualsBuilder().append(getClass(), other.getClass()).isEquals();
    }
}