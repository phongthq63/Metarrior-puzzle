package com.bamisu.gamelib.utils.business;

import com.bamisu.gamelib.base.config.ConfigHandle;
import com.bamisu.gamelib.base.config.ConfigHandle;
import org.apache.log4j.Logger;


public class Debug 
{
    public static boolean DEBUG = (ConfigHandle.instance().getLong("debug_trace") == 1);
    public static boolean TRACE_DB = (ConfigHandle.instance().getLong("trace_db") == 1);
    public static boolean TRACE_IN_PACKAGE = (ConfigHandle.instance().getLong("trace_in_package") == 1);
    public static boolean TRACE_OUT_PACKAGE = (ConfigHandle.instance().getLong("trace_out_package") == 1);
    
    public static Logger log = Logger.getLogger("debug");

    public static void trace(Object... objs) {
        StringBuilder str = new StringBuilder();
        String separator = " ";
        for (Object o : objs) {
            str.append(separator).append(o);
        }
        log.debug(str.toString());
    }
  
    public static void info(Object... objs) {
        StringBuilder str = new StringBuilder();
        String separator = " | ";
        for (Object o : objs) {
            str.append(separator).append(o);
        }
        log.info(str.toString());
    }
  
    public static void warn(Object... objs) {
        StringBuilder str = new StringBuilder();
        String separator = " | ";
        for (Object o : objs) {
            str.append(separator).append(o);
        }
        log.warn(str.toString());
  
    }
  
    public static void printStackTrace() {
        StackTraceElement[] objs = Thread.currentThread().getStackTrace();
        StringBuilder str = new StringBuilder();
        String separator = "\n";
        for (Object o : objs) {
            str.append(separator).append(o);
        }
        trace(str.toString());
    }
  
    public static void system(Object... objs) {
        StringBuilder str = new StringBuilder();
        String separator = " | ";
        for (Object o : objs) {
            str.append(separator).append(o);
        }
        log.fatal(str.toString());
    }
    
    public static void trace(Object s) {
        if (DEBUG) {
            log.debug(s);
        }
    }
    public static void debug(Object s) {
        if (DEBUG) {
            log.debug(s);
        }
    }

    public static void traceDB(String s) {
        if (TRACE_DB) {
            System.out.println(s);
        }
    }

    public static void traceInPackage(String s) {
        if (TRACE_IN_PACKAGE) {
            System.out.println(s);
        }
    }

    public static void traceOutPackage(String s) {
        if (TRACE_OUT_PACKAGE) {
            System.out.println(s);
        }
    }
}
