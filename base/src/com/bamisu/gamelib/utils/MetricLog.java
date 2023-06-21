package com.bamisu.gamelib.utils;


import org.apache.log4j.Logger;

public class MetricLog {
    private static Logger logger = Logger.getLogger("MetricLog");

    public MetricLog() {
        super();
    }

  /*  public static void writeActionLog(short cmdId, User user, Object... ext) {
        if (!ServerConstant.IS_METRICLOG || MainExtension.isBot(user)) {
            return;
        }
        if (user != null) {
            Logdata logdata = new Logdata(cmdId, user);
            logdata.add(ext);
            LogController.GetController().writeLog(ILogController.LogMode.ACTION, logdata.toString());
            logger.info("log : " + logdata);
        }
    }*/

//    public static void writeLogLogin(User user) {
//        writeActionLog(CMD.CUSTOM_LOGIN, user);
//    }


}
