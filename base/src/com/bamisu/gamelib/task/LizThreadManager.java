package com.bamisu.gamelib.task;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Popeye on 11/24/2017.
 */
public class LizThreadManager {

    private Map<String, ExecutorService> executorServiceMap;
    private Map<String, ScheduledExecutorService> fixExecutorServiceMap;
    private static LizThreadManager ourInstance = new LizThreadManager();

    public static LizThreadManager getInstance() {
        return ourInstance;
    }

    private LizThreadManager() {
        this.executorServiceMap = new ConcurrentHashMap<>();
        this.fixExecutorServiceMap = new ConcurrentHashMap<>();
    }


    public ExecutorService getExecutorServiceByName(String name) {
        ExecutorService SCHEDULER = this.executorServiceMap.computeIfAbsent(name, n -> new ThreadPoolExecutor(
                4,
                20,
                60L,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(),
                new LizThreadFactory(n)));
        return SCHEDULER;
    }

    public ScheduledExecutorService getFixExecutorServiceByName(String name) {
        ScheduledExecutorService FIX_SCHEDULER = this.fixExecutorServiceMap.computeIfAbsent(name, n -> Executors.newScheduledThreadPool(4, new LizThreadFactory(n)));
        return FIX_SCHEDULER;
    }

    public ScheduledExecutorService getFixExecutorServiceByName(String name, int size) {
        ScheduledExecutorService FIX_SCHEDULER = this.fixExecutorServiceMap.computeIfAbsent(name, n -> Executors.newScheduledThreadPool(size, new LizThreadFactory(n)));
        return FIX_SCHEDULER;
    }

    public void shutdownFixService(String name) {
        ScheduledExecutorService e = this.fixExecutorServiceMap.get(name);
        if (e != null) {
            e.shutdownNow();
        }
    }

    public void shutdownService(String name) {
        ExecutorService e = this.executorServiceMap.get(name);
        if (e != null) {
            e.shutdownNow();
        }
    }

    public static class EThreadPool{
        public static final String TELEGRAM_BOT = "tele";
        public static final String SQL_QUERY = "sql_query";
        public static final String DARK_GATE = "dark_gate";
        public static final String ARENA = "arena";
        public static final String NOTIFY = "notify";
        public static final String CAMPAIGN = "campaign";
        public static final String ROOM = "room";
    }
}
