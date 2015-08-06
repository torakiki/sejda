/*
 * Created on 17/ott/2011
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.example;

import java.io.File;

import org.sejda.core.notification.context.GlobalNotificationContext;
import org.sejda.core.service.DefaultTaskExecutionService;
import org.sejda.core.service.TaskExecutionService;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.PercentageOfWorkDoneChangedEvent;
import org.sejda.model.notification.event.TaskExecutionCompletedEvent;
import org.sejda.model.notification.event.TaskExecutionFailedEvent;
import org.sejda.model.output.DirectoryTaskOutput;
import org.sejda.model.parameter.SplitByPagesParameters;
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
     */
    public static void main(String[] args) {
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
                params.setOutput(new DirectoryTaskOutput(outputDirectory));
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

    private static void registerProgressListener() {
        GlobalNotificationContext.getContext().addListener(new ProgressListener());
    }

    private static void registerFailureListener() {
        GlobalNotificationContext.getContext().addListener(new FailureListener());
    }

    private static void registerCompletedListener() {
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
