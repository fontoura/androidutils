package com.github.fontoura.androidutils.plugins;

import android.os.Handler;
import android.os.Looper;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An {@link ActivityPlugin} that allows scheduling tasks to run in the background.
 * <p/>
 * The scheduled activities are only executed when the activity is in the resumed state. If the
 * activity is paused, tasks can still be scheduled, but they will only be executed when the
 * activity is resumed again.
 * <p/>
 * This class is heavily inspired by the
 * <a href="https://nodejs.org/docs/latest-v10.x/api/timers.html">JavaScript timers</a>,
 * formally defined in the
 * <a href="ttps://www.w3.org/TR/2011/WD-html5-20110405/timers.html#timers">HTTP standard</a>.
 */
public class TimerPlugin implements ActivityPlugin {

    /**
     * The set of actions to be executed, sorted by execution time, from the earliest to the latest.
     */
    private SortedSet<TimerAction<?>> actions = new TreeSet<>((t1, t2) -> Long.compare(t1.nextTime, t2.nextTime));

    /**
     * Indicates whether the background thread is running.
     */
    private volatile boolean running = false;

    /**
     * Indicates whether the activity has been resumed.
     */
    private volatile boolean shouldBeActive = false;

    @Override
    public void onResume() {
        ActivityPlugin.super.onResume();
        synchronized (this) {
            shouldBeActive = true;
            if (actions.size() > 0 && !running) {
                running = true;
                new Thread(this::run).start();
            }
        }
    }

    @Override
    public void onPause() {
        synchronized (this) {
            shouldBeActive = false;
            if (running) {
                this.notifyAll();
            }
        }
    }

    /**
     * Checks if the background thread is running.
     * @return {@code true} if the background thread is running, {@code false} otherwise.
     */
    public boolean isBackgroundThreadRunning() {
        return running;
    }

    /**
     * Schedules a task to run once after a given interval.
     * <p/>
     * This method is very similar to the {@code setTimeout} function of JavaScript (see
     * <a href="https://nodejs.org/docs/latest-v10.x/api/timers.html#timers_settimeout_callback_delay_args">
     * documentation</a> here).
     * @param task The task to be executed.
     * @param interval The interval in milliseconds.
     * @param <T> The type of the result of the task.
     */
    public <T> void setTimeout(BackgroundTask<T> task, long interval) {
        addTask(task, interval, false);
    }

    /**
     * Schedules a task to run repeatedly in a given interval.
     * <p/>
     * This method is very similar to the {@code setInterval} function of JavaScript (see
     * <a href="https://nodejs.org/docs/latest-v10.x/api/timers.html#timers_setinterval_callback_delay_args">
     * documentation</a> here).
     * @param task The task to be executed.
     * @param interval The interval in milliseconds.
     * @param <T> The type of the result of the task.
     */
    public <T> void setInterval(BackgroundTask<T> task, long interval) {
        addTask(task, interval, true);
    }

    private <T> void addTask(BackgroundTask<T> task, long interval, boolean intervalTask) {
        TimerAction<T> action = new TimerAction<>(task, interval, intervalTask);

        boolean start = false;
        synchronized (this) {
            actions.add(action);
            if (!running) {
                if (shouldBeActive) {
                    start = true;
                    running = true;
                }
            } else if (actions.first() == action) {
                this.notifyAll();
            }
        }

        if (start) {
            new Thread(this::run).start();
        }
    }

    private void run() {
        while (true) {
            TimerAction<?> action;
            long now = System.currentTimeMillis();
            synchronized (this) {
                if (!shouldBeActive || actions.isEmpty()) {
                    running = false;
                    return;
                }

                action = actions.first();
                if (now < action.nextTime) {
                    long waitTime = action.nextTime - now;
                    try {
                        this.wait(waitTime);
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                    continue;
                }

                actions.remove(action);
            }

            action.run();

            synchronized (this) {
                if (action.interval > 0 && !action.canceled) {
                    action.nextTime = now + action.interval;
                    actions.add(action);
                }
            }
        }
    }

    private static class TimerAction<T> {
        final BackgroundTask<T> task;;
        final long interval;

        volatile long nextTime;
        volatile boolean canceled = false;

        public TimerAction(BackgroundTask<T> task, long timeout, boolean interval) {
            this.task = task;
            if (interval) {
                this.interval = timeout;
            } else {
                this.interval = 0;
            }
            this.nextTime = System.currentTimeMillis() + timeout;
        }


        void run() {
            T value;
            try {
                value = task.doInBackground();
            } catch (Throwable t) {
                new Handler(Looper.getMainLooper()).post(() -> task.handleInForeground(t));
                return;
            }
            new Handler(Looper.getMainLooper()).post(() -> task.continueInForeground(value));
        }
    }
}
