package com.bamisu.gamelib.base;

import com.bamisu.gamelib.base.datacontroller.ZoneDatacontroler;
//import com.bamisu.gamelib.base.db.RedisController;
import com.bamisu.gamelib.base.db.SQLController;
import com.bamisu.gamelib.base.event.InternalMessage;
import com.bamisu.gamelib.manager.UserManager;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.IClientRequestHandler;
import com.smartfoxserver.v2.extensions.IServerEventHandler;
import com.smartfoxserver.v2.extensions.SFSExtension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Popeye on 7/3/2017.
 */
public abstract class BaseExtension extends SFSExtension {
    private final Map<String, IServerEventHandler> handlers = new ConcurrentHashMap<>();
    private ZoneDatacontroler dataController;
    private SQLController sqlController;
//    private RedisController redisController;
    private UserManager userManager;
    private boolean isTestServer;

    @Override
    public void addRequestHandler(String requestId, IClientRequestHandler requestHandler) {
        super.addRequestHandler(requestId, requestHandler);
    }

    @Override
    public void addEventHandler(SFSEventType eventType, IServerEventHandler handler) {
        super.addEventHandler(eventType, handler);
    }

    @Override
    public void removeRequestHandler(String requestId) {
        super.removeRequestHandler(requestId);
    }

    @Override
    protected void removeEventHandler(SFSEventType eventType) {
        super.removeEventHandler(eventType);
    }

    public abstract void initConfig();

    public abstract void initLogic();

    public abstract void initDB();

    public abstract void initModule();

    public abstract void initLogger();


    public ISFSObject handleInternalMessage(InternalMessage message) {
        return null;
    }

    public final void addServerHandler(String handlerType, IServerEventHandler handler) {
        handlers.put(handlerType, handler);
    }

    public final void removeServerHandler(String handlerType) {
        if (handlers.containsKey(handlerType))
            handlers.remove(handlerType);
    }

    public final IServerEventHandler getServerHandler(String handlerType) {
        return handlers.get(handlerType);
    }

    public abstract void onServerReady();

    public ZoneDatacontroler getDataController() {
        if (dataController == null) {
            dataController = new ZoneDatacontroler(getParentZone());
        }
        return dataController;
    }

    public SQLController getSQLController() {
        if (sqlController == null) {
            sqlController = new SQLController(getParentZone().getName());
        }
        return sqlController;
    }

//    public RedisController getRedisController() {
//        if (redisController == null) {
//            redisController = new RedisController(System.getProperty("user.dir") + "/conf/redis.properties");
//        }
//        return redisController;
//    }

    public UserManager getUserManager() {
        if (userManager == null) {
            userManager = new UserManager(getParentZone());
        }
        return userManager;
    }

    public boolean isTestServer() {
        return isTestServer;
    }

    public void setTestServer(boolean testServer) {
        isTestServer = testServer;
    }
}
