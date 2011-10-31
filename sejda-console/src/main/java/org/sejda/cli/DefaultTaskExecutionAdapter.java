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

import org.sejda.core.notification.context.GlobalNotificationContext;
import org.sejda.core.service.TaskExecutionService;
import org.sejda.model.exception.NotificationContextException;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.notification.EventListener;
import org.sejda.model.parameter.base.TaskParameters;

/**
 * Default implementation of {@link TaskExecutionAdapter}
 * 
 * @author Eduard Weissmann
 * 
 */
public class DefaultTaskExecutionAdapter implements TaskExecutionAdapter {

    private final TaskExecutionService taskExecutionService;

    public DefaultTaskExecutionAdapter(TaskExecutionService taskExecutionService) {
        this.taskExecutionService = taskExecutionService;
        registerListeners();
    }

    private void registerListeners() {
        try {
            doRegisterProcessListener();
            doRegisterTaskFailureListener();
        } catch (NotificationContextException e) {
            throw new SejdaRuntimeException("Could not register listeners. Reason: " + e.getMessage(), e);
        }
    }

    private void doRegisterProcessListener() throws NotificationContextException {
        LoggingPercentageOfWorkDoneChangeEventListener listener = new LoggingPercentageOfWorkDoneChangeEventListener();
        addEnsuringOnlyOne(listener);
    }

    private void doRegisterTaskFailureListener() throws NotificationContextException {
        DefaultTaskExecutionFailedEventListener listener = new DefaultTaskExecutionFailedEventListener();
        addEnsuringOnlyOne(listener);
    }

    /**
     * @param listener
     * @throws NotificationContextException
     */
    private void addEnsuringOnlyOne(EventListener<?> listener) throws NotificationContextException {
        GlobalNotificationContext.getContext().removeListener(listener);
        GlobalNotificationContext.getContext().addListener(listener);
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
