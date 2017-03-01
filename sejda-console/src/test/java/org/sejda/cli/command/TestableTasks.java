/*
 * Created on 15 gen 2017
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli.command;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.sejda.cli.model.*;

/**
 * @author Andrea Vacondio
 *
 */
public class TestableTasks {

    public static final List<TestableTask> TASKS;

    static {
        TASKS = stream(ServiceLoader.load(TestableTasksProvider.class).spliterator(), false)
                .flatMap(p -> p.tasks().stream()).collect(toList());
    }

    public static List<TestableTask> allTasks() {
        return TASKS;
    }

    public static List<TestableTask> allTasksExceptFor(TestableTask... exceptFor) {
        List<TestableTask> result = new ArrayList<>(TASKS);
        result.removeAll(Arrays.asList(exceptFor));
        return result;
    }

    public static List<TestableTask> allTasksExceptFor(Collection<TestableTask> tasks) {
        List<TestableTask> result = new ArrayList<>(TASKS);
        result.removeAll(tasks);
        return result;
    }

    public static List<TestableTask> getTasksWithMultipleSouceFiles() {
        return getTasksWith(TestableTasks::hasMultiplePdfSource);
    }

    public static List<TestableTask> getTasksWithSingleSouceFiles() {
        return getTasksWith(TestableTasks::hasSinglePdfSource);
    }

    public static List<TestableTask> getTasksWithFolderOutputAndPdfInput() {
        return getTasksWith(t -> !TestableTasks.hasMultipleSource(t) && (TestableTasks.hasFolderOutput(t) || TestableTasks.hasFileOrFolderOutput(t)));
    }

    public static List<TestableTask> getTasksWithPrefixableOutput() {
        return getTasksWith(TestableTasks::hasPrefixableOutput);
    }

    public static List<TestableTask> getTasksWith(Predicate<? super TestableTask> p) {
        return TASKS.stream().filter(p).collect(Collectors.toList());
    }

    public static boolean hasFileOutput(TestableTask task) {
        return !hasFolderOutput(task) && !hasFileOrFolderOutput(task);
    }

    public static boolean hasFolderOutput(TestableTask task) {
        return isInheritingTraitsFrom(task, CliArgumentsWithDirectoryOutput.class);
    }

    public static boolean hasFileOrFolderOutput(TestableTask task) {
        return isInheritingTraitsFrom(task, CliArgumentsWithFileOrDirectoryOutput.class);
    }

    public static boolean hasPrefixableOutput(TestableTask task) {
        return isInheritingTraitsFrom(task, CliArgumentsWithPrefixableOutput.class);
    }

    public static boolean hasMultiplePdfSource(TestableTask task) {
        return isInheritingTraitsFrom(task, MultipleOptionalPdfSourceTaskCliArguments.class)
                || isInheritingTraitsFrom(task, MultiplePdfSourceTaskCliArguments.class);
    }

    public static boolean hasMultipleSource(TestableTask task) {
        return isInheritingTraitsFrom(task, MultipleSourceTaskCliArguments.class);
    }

    public static boolean hasSinglePdfSource(TestableTask task) {
        return isInheritingTraitsFrom(task, SinglePdfSourceTaskCliArguments.class);
    }

    public static boolean isInheritingTraitsFrom(TestableTask task, Class<?> parentClazz) {
        return parentClazz.isAssignableFrom(task.getCommand().getCliArgumentsClass());
    }
}
