package com.bamisu.gamelib.utils.business.scribe;

import com.bamisu.gamelib.base.config.ConfigHandle;
import com.bamisu.gamelib.utils.bean.ConstantMercury;
import com.bamisu.gamelib.utils.business.CommonHandle;
import com.bamisu.gamelib.utils.business.ILogController;
import org.apache.log4j.Logger;
import org.apache.scribe.LogEntry;
import org.apache.scribe.scribe.Client;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class ScribeLogController implements ILogController,Runnable
{
   public static final boolean IS_METRICLOG = (ConfigHandle.instance().getLong("isMetriclog") == 1);
  
   protected TSocket socket;
   protected TTransport transport;
   protected TProtocol protocol;
   protected Client client;
    
   protected List<LogEntry> entrys = new ArrayList<LogEntry>();
   
   protected BlockingQueue requestQueue;
   protected ExecutorService threadPool;
   
   private final Logger logger;
   private volatile boolean isActive;
   
   // So lan Log Fail Lien tiep
   private volatile int Num = 0;
  
    public ScribeLogController() 
    {
      socket = new TSocket(ConfigHandle.instance().get("lservers"), ConstantMercury.SCRIBE_PORT,30);
      transport = new TFramedTransport(socket);
      protocol = new TBinaryProtocol(transport, false, false);
//      client = new Client(protocol, protocol);
      isActive = IS_METRICLOG;
      logger = Logger.getLogger("scriber");
      
      requestQueue = new LinkedBlockingQueue<LogEntry>();
      threadPool =  Executors.newSingleThreadExecutor();
      threadPool.execute(this);
      
    }

    /**
     * Hàm thực hiện việc write log tới scribe server
     * @param mode dùng để phân biệt log action và log error
     * @param data nội dung cần log
     */
    public void writeLog(LogMode mode, String data)
    {            
       if(!IS_METRICLOG) return ;
       
       requestQueue.add(new LogEntry(ConfigHandle.instance().get(mode.value() + "_log_category"), data));
       
       // Ngan chan Tich Log qua nhieu khi Scriber log Fail toan tap
        if(Num > 36000)
        {
          synchronized(entrys)
          {
            requestQueue.clear();         
          }
          Num = 0 ;
        }
    }
    
    public Boolean flushAll()
    {
      if(entrys.isEmpty()) return true ;   
      try {
          if(!transport.isOpen())
            transport.open();
        
//            client.Log(entrys);
            entrys.clear();         
          
            transport.close();
            Num = 0 ;
         return true ;
      } catch (TTransportException e)
      {
        CommonHandle.writeWarnLog(e);
        
          Num ++ ;
          return false;
      } catch (TException e)
      {
         CommonHandle.writeWarnLog(e);
         
          Num ++ ;
          return false ;
       } catch (Exception e)
      {
        CommonHandle.writeWarnLog(e);
        
            Num ++ ;
            return false ;
      }
     
    }

  public void run()
  {
    Thread.currentThread().setName((new StringBuilder("Scriber Log")).toString());
     while(isActive) 
         try
         {
            LogEntry entry =  (LogEntry) requestQueue.take();
            entrys.add(entry);
            flushAll();
         }
         catch(Throwable t)
         {
             logger.warn((new StringBuilder("Problems in Scriber Log main loop: ")).append(t).append(", Thread: ").append(Thread.currentThread()).toString());
            
         }
     System.out.print("ScribeLogController threadpool shutting down.");
  }

  public void writeLog(String mode, String data)
  {
    if (!IS_METRICLOG)
      return;
    
    String catalog = ConfigHandle.instance().get(mode) ;
    
    if(catalog == null || catalog.length() < 2 )
       catalog = mode ;

    requestQueue.add(new LogEntry(ConfigHandle.instance().get(mode),
                                  data));

    // Ngan chan Tich Log qua nhieu khi Scriber log Fail toan tap
    if (Num > 36000)
    {
      synchronized (entrys)
      {
        requestQueue.clear();
      }
      Num = 0;
    }
  }
}
