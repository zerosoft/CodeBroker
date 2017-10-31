package com.codebroker.core.message;

import java.io.Serializable;

/**
 * 调度任务
 */
public class ScheduleTask implements Serializable {

    /**
     * 只执行一次
     */
    private boolean once;
    /**
     * 延迟
     */
    private long delay;
    /**
     * 间隔
     */
    private long interval;

    private Runnable task;

    public boolean isOnce() {
        return once;
    }

    public void setOnce(boolean once) {
        this.once = once;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public Runnable getTask() {
        return task;
    }

    public void setTask(Runnable task) {
        this.task = task;
    }
}
