package com.github.fontoura.androidutils.plugins;

/**
 * A task that can be executed in the background and then continue in the foreground.
 * <p/>
 * This is a simple interface that can be used to execute tasks in the background and then
 * continue in the foreground.
 * <p/>
 * The {@link #doInBackground()} method is called in the background thread and may return a value.
 * If it throws an exception, the {@link #handleInForeground(Throwable)} method is called in the
 * foreground thread. Otherwise, the {@link #continueInForeground(Object)} method is called in the
 * foreground thread.
 *
 * @param <T> The generic type returned by the background task.
 */
public interface BackgroundTask<T> {

    /**
     * Performs some task in the background.
     * @return A value or {@code null} that will be passed to {@link #continueInForeground(Object)}.
     * @throws Throwable An exception that will be passed to {@link #handleInForeground(Throwable)}.
     */
    T doInBackground() throws Throwable;

    /**
     * Continues the task in the foreground.
     * <p/>
     * This method is called in the foreground thread after the background task is done, and takes
     * as a parameter the result of the background operation.
     * @param value The value returned by {@link #doInBackground()}.
     */
    void continueInForeground(T value);

    /**
     * Handles an exception in the foreground.
     * <p/>
     * This method is called in the foreground thread if the background task throws an exception.
     * @param t The exception thrown by {@link #doInBackground()}.
     */
    void handleInForeground(Throwable t);
}
