/*
 * Created on Jul 1, 2011
 * Copyright 2011 by Eduard Weissmann (edi.weissmann@gmail.com).
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

import org.sejda.core.exception.NotificationContextException;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.model.parameter.base.TaskParameters;
import org.sejda.core.manipulation.service.TaskExecutionService;
import org.sejda.core.notification.EventListener;
import org.sejda.core.notification.context.GlobalNotificationContext;
import org.sejda.core.notification.event.PercentageOfWorkDoneChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link TaskExecutionAdapter}
 * 
 * @author Eduard Weissmann
 * 
 */
public class DefaultTaskExecutionAdapter implements TaskExecutionAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTaskExecutionAdapter.class);

    private final TaskExecutionService taskExecutionService;

    public DefaultTaskExecutionAdapter(TaskExecutionService taskExecutionService) {
        this.taskExecutionService = taskExecutionService;
        registerProcessListener();
    }

    private void registerProcessListener() {
        try {
            doRegisterProcessListener();
        } catch (NotificationContextException e) {
            throw new SejdaRuntimeException("Could not register progress listener. Reason: " + e.getMessage(), e);
        }
    }

    private void doRegisterProcessListener() throws NotificationContextException {
        GlobalNotificationContext.getContext().addListener(new EventListener<PercentageOfWorkDoneChangedEvent>() {

            public void onEvent(PercentageOfWorkDoneChangedEvent event) {
                LOG.info("Task progress: " + event.getPercentage().toPlainString() + "% done");
            }
        });
    }

    TaskExecutionService getTaskExecutionService() {
        return taskExecutionService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.sejda.cli.TaskExecutionAdapter#executeCommand(org.sejda.core.manipulation.model.parameter.TaskParameters)
     */
    public void execute(TaskParameters taskParameters) {
        getTaskExecutionService().execute(taskParameters);
    }
}
