package com.bamisu.gamelib.manager;

import org.apache.log4j.Logger;

/**
 * Create by Popeye on 4:41 PM, 12/12/2019
 */
public class LoggerManager {
    private static LoggerManager ourInstance = new LoggerManager();

    public static LoggerManager getInstance() {
        return ourInstance;
    }

    private LoggerManager() {
    }
}
