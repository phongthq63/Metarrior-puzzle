package com.bamisu.gamelib.utils.business;

import com.bamisu.gamelib.base.excepions.ExceptionMessageComposer;
import com.bamisu.gamelib.base.excepions.ExceptionMessageComposer;
import com.bamisu.gamelib.utils.DebugConsole;

import java.io.PrintWriter;
import java.io.StringWriter;


public class CommonHandle {
    private CommonHandle() {
    }

    public static void writeActionLog(String ownerId, String victimId,
                                      String ipOrUrl, String action,
                                      String param1, String param2,
                                      String param3, String param4,
                                      String param5, int goldChange,
                                      int xuChange, int expChange) {

        String data =
            ownerId + "|" + victimId + "|" + ipOrUrl + "|" + action + "|" +
            param1 + "|" + param2 + "|" + param3 + "|" + param4 + "|" +
            param5 + "|" + goldChange + "|" + xuChange + "|" + expChange;

        LogController.GetController().writeLog(ILogController.LogMode.ACTION, data);
    }

    public static void writeErrLog(Throwable t)
    {
        if(Debug.DEBUG)  t.printStackTrace();
       ExceptionMessageComposer msg = new ExceptionMessageComposer(t);
       msg.setDescription("An error occurred during the Execution");
       DebugConsole.log.error(msg.toString());
    }
    
    public static void writeWarnLog(Throwable t)
    {
        if(Debug.DEBUG)  t.printStackTrace();
       ExceptionMessageComposer msg = new ExceptionMessageComposer(t);
       msg.setDescription("An warning occurred during the Execution");
       DebugConsole.log.warn(msg.toString());
    }
    
   public static void writeDebugLog(Throwable t)
   {
      if(Debug.DEBUG)  t.printStackTrace();
     ExceptionMessageComposer msg = new ExceptionMessageComposer(t);
     msg.setDescription("An warning occurred during the Execution");
     DebugConsole.log.debug(msg.toString());
   }
   
  public static void writeInfoLog(Throwable t)
  {
     if(Debug.DEBUG)  t.printStackTrace();
    ExceptionMessageComposer msg = new ExceptionMessageComposer(t);
    msg.setDescription("An warning occurred during the Execution");
    DebugConsole.log.info(msg.toString());
  }
    
    public static void writeErrLog(String s) {
        LogController.GetController().writeLog(ILogController.LogMode.ERROR, s);
    }
    
    public static void writePaymentLog(String data){
        LogController.GetController().writeLog(ILogController.LogMode.PAYMENT, data);
    }

    public static void writeInfoLog(String data) {
        LogController.GetController().writeLog(ILogController.LogMode.INFO,data);
    }
    
    private static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }
}
