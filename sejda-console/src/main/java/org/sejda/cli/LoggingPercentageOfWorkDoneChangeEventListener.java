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
import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.PercentageOfWorkDoneChangedEvent;
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

    @Override
    public void onEvent(PercentageOfWorkDoneChangedEvent event) {
        if (event.isUndetermined()) {
            LOG.info("Task in progress");
        } else {
            LOG.info("Task progress: " + event.getPercentage().toPlainString() + "% done");
        }
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