package org.sejda.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.AbstractNotificationEvent;
import org.sejda.model.notification.event.PercentageOfWorkDoneChangedEvent;
import org.sejda.model.notification.event.TaskExecutionFailedEvent;
import org.sejda.model.notification.event.TaskExecutionStartedEvent;
import org.sejda.model.notification.event.TaskExecutionWarningEvent;

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
     * @return a start listener to use in tests.
     */
    public static TestListenerFailed newFailedListener() {
        return new TestListenerFailed();
    }

    public static TestListenerWarnings newWarningsListener() {
        return new TestListenerWarnings();
    }

    /**
     * @return a general listener that can listen on any event to use in tests.
     */
    public static <T extends AbstractNotificationEvent> TestListenerAny<T> newGeneralListener() {
        return new TestListenerAny<T>();
    }

    /**
     * Simple listener to use during tests. General purpose, can listen on any event
     * 
     * @author Andrea Vacondio
     * 
     */
    public static class TestListenerAny<T extends AbstractNotificationEvent> implements EventListener<T> {

        private boolean listened = false;

        @Override
        public void onEvent(T event) {
            listened = true;
        }

        public boolean hasListened() {
            return listened;
        }

    }

    /**
     * Simple listener to use during tests. Listens for a percentage of work done event.
     * 
     * @author Andrea Vacondio
     * 
     */
    public static class TestListenerPercentage implements EventListener<PercentageOfWorkDoneChangedEvent> {

        private BigDecimal percentage;
        private boolean undeterminate;

        @Override
        public void onEvent(PercentageOfWorkDoneChangedEvent event) {
            percentage = event.getPercentage();
            undeterminate = event.isUndetermined();
        }

        public BigDecimal getPercentage() {
            return percentage;
        }

        public boolean isUndeterminate() {
            return undeterminate;
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

        @Override
        public void onEvent(TaskExecutionStartedEvent event) {
            started = true;
        }

        public boolean isStarted() {
            return started;
        }

    }

    /**
     * Simple listener to use during tests. Listens for a start event.
     * 
     * @author Andrea Vacondio
     * 
     */
    public static class TestListenerFailed implements EventListener<TaskExecutionFailedEvent> {

        private boolean failed = false;

        @Override
        public void onEvent(TaskExecutionFailedEvent event) {
            failed = true;
        }

        public boolean isFailed() {
            return failed;
        }
    }

    /**
     * Simple listener to use during tests. Listens for a warning event.
     *
     */
    public static class TestListenerWarnings implements EventListener<TaskExecutionWarningEvent> {

        private List<String> warnings = new ArrayList<>();

        @Override
        public void onEvent(TaskExecutionWarningEvent event) {
            warnings.add(event.getWarning());
        }

        public List<String> getWarnings() {
            return warnings;
        }
    }
}
