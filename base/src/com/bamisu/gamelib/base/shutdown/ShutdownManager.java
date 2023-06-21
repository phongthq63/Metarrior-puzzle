package com.bamisu.gamelib.base.shutdown;

import com.couchbase.client.deps.io.netty.util.internal.ConcurrentSet;

import java.util.Set;

public class ShutdownManager extends Thread {
    private static ShutdownManager _instance = null;
    private Set<IShutdownListener> listeners = new ConcurrentSet<>();

    public static ShutdownManager getInstance() {
        if (_instance == null) {
            _instance = new ShutdownManager();
        }
        return _instance;
    }

    private ShutdownManager() {
        Runtime.getRuntime().addShutdownHook(this);
    }

    @Override
    public void run() {
        for (IShutdownListener listener : listeners) {
            listener.onShutdown();
        }
    }

    public void register(IShutdownListener listener) {
        this.listeners.add(listener);
    }

    public void unRegister(IShutdownListener listener) {
        this.listeners.remove(listener);
    }
}
