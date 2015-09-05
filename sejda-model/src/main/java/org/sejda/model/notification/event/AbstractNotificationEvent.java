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

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.sejda.model.task.NotifiableTaskMetadata;

/**
 * Abstract event that carries the timestamp when it has been created.
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractNotificationEvent implements NotificationEvent, Serializable {

    private static final long serialVersionUID = 3392179202226082364L;

    private Long eventTimestamp;
    private NotifiableTaskMetadata notifiableTaskMetadata;

    AbstractNotificationEvent(NotifiableTaskMetadata notifiableTaskMetadata) {
        this.eventTimestamp = Long.valueOf(System.currentTimeMillis());
        this.notifiableTaskMetadata = notifiableTaskMetadata;
    }

    @Override
    public Long getEventTimestamp() {
        return eventTimestamp;
    }

    public NotifiableTaskMetadata getNotifiableTaskMetadata() {
        return notifiableTaskMetadata;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("eventTimestamp", eventTimestamp)
                .append("taskMetadata", notifiableTaskMetadata).toString();
    }

}
