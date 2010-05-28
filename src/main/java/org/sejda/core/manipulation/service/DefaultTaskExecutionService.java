/*
 * Created on 28/mag/2010
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
package org.sejda.core.manipulation.service;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.sejda.core.exception.TaskException;
import org.sejda.core.manipulation.DefaultTaskExecutionContext;
import org.sejda.core.manipulation.Task;
import org.sejda.core.manipulation.TaskExecutionContext;
import org.sejda.core.manipulation.TaskParameters;

/**
 * Default implementation of the {@link TaskExecutionService}
 * @author Andrea Vacondio
 * 
 */
public class DefaultTaskExecutionService implements TaskExecutionService {

    private static final Logger LOG = Logger.getLogger(DefaultTaskExecutionService.class.getPackage().getName());

    private StopWatch stopWatch = new StopWatch();
    private ThreadLocal<TaskExecutionContext> localContext = new ThreadLocal<TaskExecutionContext>(){
        protected TaskExecutionContext initialValue() {
            return new DefaultTaskExecutionContext();
        }
    };

    @SuppressWarnings("unchecked")
    public void execute(TaskParameters parameters) throws TaskException {
        stopWatch.reset();
        stopWatch.start();
        Task task = localContext.get().getTask(parameters);
        LOG.trace(String.format("Starting task %s execution.", task));
        try {
            task.before(parameters);
            task.execute(parameters);
        } finally {
            task.after();
        }
        stopWatch.stop();
        LOG.debug(String.format("Task %s executed in %s", task, DurationFormatUtils.formatDurationWords(stopWatch.getTime(), true, true)));
    }

    //Test purpose
    /**
     * set the thread local context
     */
    void setLocalContext(ThreadLocal<TaskExecutionContext> context) {
        localContext = context;
    }

    
}
