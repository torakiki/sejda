package org.sejda.model.task;

public class CancellationOption {

    private Cancellable cancellableTask;

    public void setCancellableTask(Cancellable cancellableTask) {
        this.cancellableTask = cancellableTask;
    }

    public boolean isCancellable() {
        return cancellableTask != null;
    }

    public void requestCancel() {
        if(!isCancellable()) throw new RuntimeException("Task not yet started");

        cancellableTask.cancel();
    }
}
