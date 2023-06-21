package com.bamisu.gamelib.entities;

import com.bamisu.gamelib.task.LizThreadManager;
import org.apache.log4j.Logger;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

	static final Logger logger = Logger.getLogger(LizThreadManager.class);

	public UncaughtExceptionHandler() {}

	// Implement your own way of logging here
	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		String msg = String.format("Uncaught exception in Thread %s: %s", t.getName(), e.getMessage());
		logger.error(msg, e);
	}
}
