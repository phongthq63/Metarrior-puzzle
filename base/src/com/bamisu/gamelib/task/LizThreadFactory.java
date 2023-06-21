package com.bamisu.gamelib.task;

import com.bamisu.gamelib.entities.UncaughtExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class LizThreadFactory implements ThreadFactory {
	private static final Logger logger = LoggerFactory.getLogger(LizThreadFactory.class);
	private static final AtomicInteger poolNumber = new AtomicInteger(1);
	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;

	public static void main(String[] args) {
		new LizThreadFactory("a");
	}

	public LizThreadFactory(String prefix) {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : new ThreadGroup(Thread.currentThread().getThreadGroup(), prefix);

		if (prefix == null) prefix = "pool";
		namePrefix = prefix + "-" + poolNumber.getAndIncrement() + "-thread-";
	}

	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r,
				namePrefix + threadNumber.getAndIncrement() + "-" + r.getClass().getSimpleName(), 0);
		if (t.isDaemon()) t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY) t.setPriority(Thread.NORM_PRIORITY);
		/**
		 * Note: this will not work with an Executor
		 * Read the answers* here
		 * http://stackoverflow.com/questions/2248131/handling-exceptions-from-java-executorservice-
		 * tasks
		 * http://code.nomad-labs.com/2011/12/09/mother-fk-the-scheduledexecutorservice/
		 * 
		 * ALWAYS CATCH ALL EXCEPTION
		 */
		t.setUncaughtExceptionHandler(new UncaughtExceptionHandler());
		return t;
	}
}
