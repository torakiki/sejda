/*
 * Created on 17/ott/2011
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
package org.sejda.example;

import java.io.File;

import org.sejda.core.exception.NotificationContextException;
import org.sejda.core.exception.SejdaRuntimeException;
import org.sejda.core.manipulation.model.input.PdfFileSource;
import org.sejda.core.manipulation.model.output.DirectoryOutput;
import org.sejda.core.manipulation.model.parameter.SplitByPagesParameters;
import org.sejda.core.manipulation.service.DefaultTaskExecutionService;
import org.sejda.core.manipulation.service.TaskExecutionService;
import org.sejda.core.notification.EventListener;
import org.sejda.core.notification.context.GlobalNotificationContext;
import org.sejda.core.notification.event.PercentageOfWorkDoneChangedEvent;
import org.sejda.core.notification.event.TaskExecutionCompletedEvent;
import org.sejda.core.notification.event.TaskExecutionFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple application demonstrating how Sejda can be used. It provides a simple command line interface for the Split by pages task.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class SplitByPages {

    private static final Logger LOG = LoggerFactory.getLogger(SplitByPages.class);

    private static final int MIN_EXPECTED_ARGS_NUM = 6;
    private static final TaskExecutionService EXECUTOR = new DefaultTaskExecutionService();

    private SplitByPages() {
        // hide
    }

    /**
     * @param args
     * @throws NotificationContextException
     */
    public static void main(String[] args) throws NotificationContextException {
        if (args.length < MIN_EXPECTED_ARGS_NUM) {
            printUsage();
            return;
        }
        SplitByPagesParameters params = createParameters(args);
        registerProgressListener();
        registerFailureListener();
        registerCompletedListener();
        executeTask(params);
    }

    private static void printUsage() {
        LOG.info("Usage: sejda-example -f /PATH_TO_INPUT/INPUT.pdf -o /OUTPUT_DIRECTORY -s n1,n2,n3.. -overwrite");
        LOG.info("Where /PATH_TO_INPUT/INPUT.pdf is the absolut path to the input pdf document.");
        LOG.info("Where /OUTPUT_DIRECTORY is the directory where output will be written.");
        LOG.info("Where n1,n2,n3.. is a comma separated list of page numbers where the document will be splitted at.");
        LOG.info("Where -overwrite is optional and instruct the utility to overwrite an existing file with the same name as the generated ones.");
    }

    private static SplitByPagesParameters createParameters(String[] args) {
        SplitByPagesParameters params = new SplitByPagesParameters();
        for (int i = 0; i < args.length; i++) {
            if ("-f".equals(args[i])) {
                File inputFile = new File(args[++i]);
                params.setSource(PdfFileSource.newInstanceNoPassword(inputFile));
            } else if ("-o".equals(args[i])) {
                File outputDirectory = new File(args[++i]);
                params.setOutput(DirectoryOutput.newInstance(outputDirectory));
            } else if ("-s".equals(args[i])) {
                String[] pages = args[++i].split(",");
                for (int j = 0; j < pages.length; j++) {
                    params.addPage(Integer.valueOf(pages[j].trim()));
                }
            } else if ("-overwrite".equals(args[i])) {
                params.setOverwrite(true);
            }
        }
        return params;
    }

    private static void registerProgressListener() throws NotificationContextException {
        GlobalNotificationContext.getContext().addListener(new ProgressListener());
    }

    private static void registerFailureListener() throws NotificationContextException {
        GlobalNotificationContext.getContext().addListener(new FailureListener());
    }

    private static void registerCompletedListener() throws NotificationContextException {
        GlobalNotificationContext.getContext().addListener(new CompletionListener());
    }

    private static void executeTask(SplitByPagesParameters parameters) {
        EXECUTOR.execute(parameters);
    }

    /**
     * Listener printing the percentage of work done by the task
     * 
     * @author Andrea Vacondio
     * 
     */
    private static class ProgressListener implements EventListener<PercentageOfWorkDoneChangedEvent> {

        public void onEvent(PercentageOfWorkDoneChangedEvent event) {
            LOG.info("Task progress: {}% done.", event.getPercentage().toPlainString());
        }
    }

    /**
     * Listener exiting with an error code in case of task failure
     * 
     * @author Andrea Vacondio
     * 
     */
    private static class FailureListener implements EventListener<TaskExecutionFailedEvent> {

        public void onEvent(TaskExecutionFailedEvent event) {
            LOG.error("Task execution failed.");
            // rethrow it to the main
            throw new SejdaRuntimeException(event.getFailingCause());
        }
    }

    /**
     * Listener informing the user about the task completion.
     * 
     * @author Andrea Vacondio
     * 
     */
    private static class CompletionListener implements EventListener<TaskExecutionCompletedEvent> {

        public void onEvent(TaskExecutionCompletedEvent event) {
            LOG.info("Task completed in {} millis.", event.getExecutionTime());
        }

    }
}
