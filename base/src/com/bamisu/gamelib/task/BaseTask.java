package com.bamisu.gamelib.task;

import org.apache.log4j.Logger;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class BaseTask {
    protected final ScheduledExecutorService SCHEDULER;
    protected ScheduledFuture<?> scheduledFuture;
    protected Logger logger = Logger.getLogger("sqlLog");
    public BaseTask() {
        SCHEDULER = LizThreadManager.getInstance().getFixExecutorServiceByName(LizThreadManager.EThreadPool.SQL_QUERY);
    }
    public ScheduledExecutorService getSCHEDULER() {
        return SCHEDULER;
    }
    protected int countError = 0;
}
