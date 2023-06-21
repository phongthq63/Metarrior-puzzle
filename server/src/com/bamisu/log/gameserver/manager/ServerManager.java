package com.bamisu.log.gameserver.manager;

import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.InfoConfigActive;
import com.bamisu.log.gameserver.datamodel.ServerVariableModel;
import com.bamisu.log.gameserver.entities.EModule;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ServerManager {
    private static volatile ServerManager ourInstance;
    public static ServerManager getInstance() {
        if(ourInstance == null){
            ourInstance = new ServerManager();
        }
        return ourInstance;
    }

    private ServerManager() {
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Ger Server Variable Model
     */
    @WithSpan
    public ServerVariableModel getServerVariableModel(Zone zone) {
        return ServerVariableModel.copyFromDBtoObject(zone);
//        return ((ZoneExtension)zone.getExtension()).getZoneCacheData().getServerVariableModelCache();
    }

    /**
     * Doc trang thai config tren server
     * @param zone
     * @return
     */
    @WithSpan
    public boolean isActiveEventModule(Zone zone){
        ServerVariableModel serverVariableModel = getServerVariableModel(zone);
        Map<String,InfoConfigActive> data = serverVariableModel.readActiveEventModule(zone);
        InfoConfigActive dataModule;

        for(EModule module : EModule.values()){
            dataModule = data.get(module.getId());

            if(dataModule != null && dataModule.active) return true;
        }

        return false;
    }
    @WithSpan
    public boolean isActiveEventModule(EModule module, Zone zone){
        InfoConfigActive data = getServerVariableModel(zone).readActiveEventModule(zone).get(module.getId());
        if(data != null){
            return data.active;
        }else {
            return false;
        }
    }

    /**
     * Update trang thai config tren server
     * @param idModule
     * @param zone
     * @return
     */
    @WithSpan
    public boolean updateActiveEventModule(String idModule, boolean active, int timeStamp, Zone zone){
        EModule module = EModule.fromID(idModule);
        if(module == null) return false;
        return updateActiveEventModule(module, active, timeStamp, zone);
    }
    @WithSpan
    public boolean updateActiveEventModule(EModule module, boolean active, int timeStamp, Zone zone){
        return getServerVariableModel(zone).updateActiveEventModule(module, active, timeStamp, zone);
    }


    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Bao tri toan bo server (1 may)
     */
    @WithSpan
    public synchronized boolean maintenanceAllServer(boolean PRE_MAINTENANCE){
        ServerConstant.PRE_MAINTENANCE = PRE_MAINTENANCE;
        if(ServerConstant.PRE_MAINTENANCE){
            LizThreadManager.getInstance().getFixExecutorServiceByName("maintenance", 1).
                    schedule(() ->
                    {
                        try{
                            disconectAllUserAllZone();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }, 0, TimeUnit.SECONDS);
        }else {

        }

        return true;
    }

    /**
     * Disconect toan bo user
     */
    @WithSpan
    private void disconectAllUserAllZone(){
        SmartFoxServer.getInstance().getZoneManager().getZoneList().parallelStream().
                filter(zone -> !zone.getName().equals("--=={{{ AdminZone }}}==--")).
                forEach(zone -> zone.getUserList().parallelStream().
                        forEach(user -> ExtensionUtility.getInstance().disconnectUser(user)));
    }

    /**
     * lấy ccu theo từng zone
     * @return
     */
    @WithSpan
    public Map<String, Integer> getCCU(){
        Map<String, Integer> ccuMap = new ConcurrentHashMap<>();
        SmartFoxServer.getInstance().getZoneManager().getZoneList().parallelStream().
                filter(zone -> !zone.getName().equals("--=={{{ AdminZone }}}==--")).
                forEach(zone -> ccuMap.put(zone.getName(), zone.getUserManager().getUserCount()));
        return ccuMap;
    }
}
