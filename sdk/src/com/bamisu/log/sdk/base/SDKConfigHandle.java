package com.bamisu.log.sdk.base;

import com.bamisu.gamelib.base.excepions.ExceptionMessageComposer;
import com.bamisu.gamelib.utils.DebugConsole;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by Popeye on 4:44 PM, 4/25/2020
 */
public class SDKConfigHandle {
    private static SDKConfigHandle _instance;
    private final static Object lock = new Object();

    private Random r = new Random(System.currentTimeMillis());

    private Properties props;
    private ConcurrentHashMap<String, Long> longPropsCaching;
    private ConcurrentHashMap<String, String[]> listPropsCaching;
    private ConcurrentHashMap<String, Properties> propsByGames;

    private SDKConfigHandle() {
        try {
            longPropsCaching = new ConcurrentHashMap<String, Long>();
            listPropsCaching = new ConcurrentHashMap<String, String[]>();
            props = new Properties();
            props.load(Files.newInputStream(new File(System.getProperty("user.dir") +
                    File.separator + "conf" +
                    File.separator + "sdk" +
                    File.separator +
                    "sdk-cluster.properties").toPath()));

            // Load games props
//            propsByGames = new ConcurrentHashMap<String, Properties>();
//            String[] games = props.getProperty("games").split(";");
//            for (int i = 0; i < games.length; i++) {
//                Properties p = new Properties();
//                File f =
//                        new File(System.getProperty("user.dir") + File.separator +
//                                "conf" + File.separator + games[i] + ".properties");
//                if (f.exists())
//                    p.load(new FileInputStream(f));
//                p.setProperty("defaultScore",
//                        p.getProperty("defaultScore", "0"));
//                propsByGames.put(games[i], p);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionMessageComposer msg = new ExceptionMessageComposer(e);
            msg.setDescription("An error occurred during the Execution");
            DebugConsole.log.error(msg.toString());
        }
    }

    public static SDKConfigHandle instance() {
        if (_instance == null) {
            synchronized (lock) {
                if (_instance == null) {
                    _instance = new SDKConfigHandle();
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
            } catch (Exception e) {
                e.printStackTrace();
                ExceptionMessageComposer msg = new ExceptionMessageComposer(e);
                msg.setDescription("An error occurred during the Execution");
                DebugConsole.log.error(msg.toString());
            }
            longPropsCaching.put(name, result);
        }
        return result;
    }

    public Boolean getBoolean(String name) {
        return (getLong(name) == 1);
    }

    public Boolean getBoolean(String game, String name) {
        return (getLong(game, name) == 1);
    }

    public String getRandom(String name) {
        String[] data = listPropsCaching.get(name);
        if (data == null) {
            data = new String[0];
            try {
                data = props.getProperty(name).split(";");
            } catch (Exception e) {
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

    public String get(String game, String name) {
        if (propsByGames.get(game) != null)
            return propsByGames.get(game).getProperty(name);
        else
            return null;
    }

    public Long getLong(String game, String name) {
        Long result = null;
        try {
            result = Long.parseLong(propsByGames.get(game).getProperty(name));
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionMessageComposer msg = new ExceptionMessageComposer(e);
            msg.setDescription("An error occurred during the Execution");
            DebugConsole.log.error(msg.toString());
        }
        return result;
    }

    public int getInt(String game, String name) {
        int result = 0;
        try {
            result =
                    Integer.parseInt(propsByGames.get(game).getProperty(name).trim());
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionMessageComposer msg = new ExceptionMessageComposer(e);
            msg.setDescription("An error occurred during the Execution");
            DebugConsole.log.error(msg.toString());
        }
        return result;
    }
}
