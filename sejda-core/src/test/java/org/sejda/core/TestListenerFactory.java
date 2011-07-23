package org.sejda.core;

import java.math.BigDecimal;

import org.junit.Ignore;
import org.sejda.core.notification.EventListener;
import org.sejda.core.notification.event.PercentageOfWorkDoneChangedEvent;
import org.sejda.core.notification.event.TaskExecutionStartedEvent;

/**
 * Factory used by tests to create event listeners.
 * 
 * @author Andrea Vacondio
 * 
 */
@Ignore
public final class TestListenerFactory {

    private TestListenerFactory() {
        // Factory
    }

    /**
     * @return a percentage listener to use in tests.
     */
    public static TestListenerPercentage newPercentageListener() {
        return new TestListenerPercentage();
    }

    /**
     * @return a start listener to use in tests.
     */
    public static TestListenerStart newStartListener() {
        return new TestListenerStart();
    }

    /**
     * Simple listener to use during tests. Listens for a percentage of work done event.
     * 
     * @author Andrea Vacondio
     * 
     */
    public static class TestListenerPercentage implements EventListener<PercentageOfWorkDoneChangedEvent> {

        private BigDecimal percentage;

        public void onEvent(PercentageOfWorkDoneChangedEvent event) {
            percentage = event.getPercentage();
        }

        public BigDecimal getPercentage() {
            return percentage;
        }

    }

    /**
     * Simple listener to use during tests. Listens for a start event.
     * 
     * @author Andrea Vacondio
     * 
     */
    public static class TestListenerStart implements EventListener<TaskExecutionStartedEvent> {

        private boolean started = false;

        public void onEvent(TaskExecutionStartedEvent event) {
            started = true;
        }

        public boolean isStarted() {
            return started;
        }

    }
}
