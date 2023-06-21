package com.bamisu.gamelib.utils.business;


import com.bamisu.gamelib.utils.business.scribe.ScribeLogController;
import com.bamisu.gamelib.utils.business.scribe.ScribeLogController;

public class LogController
{
    
    static ScribeLogController _instance;
    static final Object lock = new Object();
  
    public static ILogController GetController() 
    {
      if (_instance == null) {
          synchronized (lock) {
              if (_instance == null) {
                  _instance = new ScribeLogController();
              }
          }
      }
      return _instance;
    }
    
   
}
