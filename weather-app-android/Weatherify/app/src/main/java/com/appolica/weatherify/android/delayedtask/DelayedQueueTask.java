package com.appolica.weatherify.android.delayedtask;

import android.os.Handler;

import java.util.LinkedList;
import java.util.Queue;

public class DelayedQueueTask implements Runnable {
    private long delay;
    private boolean finished = false;
    private Queue<Runnable> taskQueue = new LinkedList<>();

    public DelayedQueueTask(long delay) {
        this.delay = delay;
    }

    public DelayedQueueTask(long delay, Queue<Runnable> taskQueue) {
        this.delay = delay;
        this.taskQueue = taskQueue;
    }

    public void start() {
        new Handler().postDelayed(this, delay);
    }

    @Override
    public void run() {
        finished = true;
        while (!taskQueue.isEmpty()) {
            taskQueue.poll().run();
        }
    }

    public void addTask(Runnable runnable) {
        if (isFinished()) {
            throw new IllegalStateException("All tasks have been already executed. The time ran out.");
        }

        taskQueue.offer(runnable);
    }

    public boolean isFinished() {
        return finished;
    }

    public static  class Builder {
        private long delay;
        private Queue<Runnable> taskQueue = new LinkedList<>();

        public Builder delay(long delay) {
            this.delay = delay;
            return this;
        }

        public Builder addTask(Runnable task) {
            this.taskQueue.offer(task);

            return this;
        }

        public DelayedQueueTask build() {
            return new DelayedQueueTask(delay, taskQueue);
        }
    }
}
