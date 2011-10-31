/*
 * Created on 28/ott/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.model.task;

import java.io.Serializable;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * An immutable set of metadata related to the task the event is notifying about.
 * 
 * @author Andrea Vacondio
 * 
 */
public class NotifiableTaskMetadata implements Serializable {

    private static final long serialVersionUID = -6423865557633949211L;
    /**
     * Null object pattern
     */
    public static final NotifiableTaskMetadata NULL = new NullNotifiableTaskMetadata();
    private UUID taskIdentifier;
    private String qualifiedName;

    private NotifiableTaskMetadata() {
        // empty constructor
    }

    public NotifiableTaskMetadata(Task<?> task) {
        if (task == null) {
            throw new IllegalArgumentException("No task given, unable to create notifiable metadata.");
        }
        this.taskIdentifier = UUID.randomUUID();
        this.qualifiedName = task.getClass().getName();
    }

    /**
     * @return the identifier of the task the event is notifying about.
     */
    public UUID getTaskIdentifier() {
        return taskIdentifier;
    }

    /**
     * @return the qualified name of the task the event is notifying about.
     */
    public String getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(taskIdentifier).append(qualifiedName).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NotifiableTaskMetadata)) {
            return false;
        }
        NotifiableTaskMetadata meta = (NotifiableTaskMetadata) other;
        return new EqualsBuilder().append(taskIdentifier, meta.taskIdentifier)
                .append(qualifiedName, meta.qualifiedName).isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("taskIdentifier", taskIdentifier)
                .append("qualifiedName", qualifiedName).toString();
    }

    /**
     * Null object pattern providing empty behavior.
     * 
     * @author Andrea Vacondio
     * 
     */
    private static class NullNotifiableTaskMetadata extends NotifiableTaskMetadata {

        private static final long serialVersionUID = 6788562820506828221L;

        @Override
        public UUID getTaskIdentifier() {
            return null;
        }

        @Override
        public String getQualifiedName() {
            return StringUtils.EMPTY;
        }
    }
}
