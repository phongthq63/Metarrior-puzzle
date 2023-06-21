package com.bamisu.gamelib.base.config;

import com.bamisu.gamelib.base.excepions.ExceptionMessageComposer;
import com.bamisu.gamelib.base.excepions.ExceptionMessageComposer;
import com.bamisu.gamelib.utils.DebugConsole;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


public class ConfigHandle {
    private static ConfigHandle _instance;
    private final static Object lock = new Object();

    private Random r = new Random(System.currentTimeMillis());

    private Properties props;
    private ConcurrentHashMap<String, Long> longPropsCaching;
    private ConcurrentHashMap<String, String[]> listPropsCaching;

    private ConfigHandle() {
        InputStream inputStream = null;
        try {
            longPropsCaching = new ConcurrentHashMap<String, Long>();
            listPropsCaching = new ConcurrentHashMap<String, String[]>();
            props = new Properties();
            inputStream = Files.newInputStream(new File(System.getProperty("user.dir") +
                    File.separator + "conf" +
                    File.separator +
                    "cluster.properties").toPath());
            props.load(inputStream);
      
        } catch (Exception e) {
            e.printStackTrace();
          ExceptionMessageComposer msg = new ExceptionMessageComposer(e);
          msg.setDescription("An error occurred during the Execution");
          DebugConsole.log.error(msg.toString());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ConfigHandle instance() {
        if (_instance == null) {
            synchronized (lock) {
                if (_instance == null) {
                    _instance = new ConfigHandle();
                }
            }
        }
        return _instance;
    }

    public String get(String name) {
        if (props != null)
            return props.getProperty(name);
        else
            return null;
    }

    public Integer getInt(String name) {
        if (props != null)
            return Integer.parseInt(props.getProperty(name));
        else
            return null;
    }

    public Long getLong(String name) {
        Long result = longPropsCaching.get(name);
        if (result == null) {
            result = new Long(0);
            try {
                result = Long.parseLong(props.getProperty(name));
            } catch (Exception e) 
            {
              e.printStackTrace();
              ExceptionMessageComposer msg = new ExceptionMessageComposer(e);
              msg.setDescription("An error occurred during the Execution");
              DebugConsole.log.error(msg.toString());
            }
            longPropsCaching.put(name, result);
        }
        return result;
    }
    
    public Boolean getBoolean(String name)
    {
      return ( getLong(name) == 1 );
    }

    public String getRandom(String name) {
        String[] data = listPropsCaching.get(name);
        if (data == null) {
            data = new String[0];
            try {
                data = props.getProperty(name).split(";");
            } catch (Exception e) 
            {
              ExceptionMessageComposer msg = new ExceptionMessageComposer(e);
              msg.setDescription("An error occurred during the Execution");
              DebugConsole.log.error(msg.toString());
            }
            listPropsCaching.put(name, data);
        }
        if (data.length > 0) {
            return data[r.nextInt(data.length)];
        } else
            return null;
    }
}
