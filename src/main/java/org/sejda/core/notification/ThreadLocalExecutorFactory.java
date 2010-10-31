/*
 * Created on 06/mag/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.notification;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Factory that returns a single threaded executor for the current thread.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class ThreadLocalExecutorFactory {

    private ThreadLocalExecutorFactory() {
        // hide
    }

    private static ThreadLocal<ExecutorService> threadLocal = new ThreadLocal<ExecutorService>() {
        protected ExecutorService initialValue() {
            return Executors.newSingleThreadExecutor();
        }
    };

    /**
     * @return the local executor
     */
    public static ExecutorService getLocalExecutor() {
        return threadLocal.get();
    }
}
