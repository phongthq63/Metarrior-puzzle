package com.bamisu.log.gameserver.datamodel;

import com.bamisu.log.gameserver.entities.EModule;
import com.bamisu.log.gameserver.entities.EStatus;
import com.bamisu.log.gameserver.manager.ServerManager;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.hero.exception.IndexOutOfBoundByChangeConfigException;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerVariableModel extends DataModel {
    private final static long id = 0;
    public int elementIndex;
    public int kingdomIndex;
    public int timeStamp;
    public Map<String,InfoConfigActive> mapActiveConfig = new HashMap<>();        //Doc config theo module active
    private final static int timeDay = 86400;     // 1day = 86400s



    private void init(){
        timeStamp = Utils.getTimestampInSecond();
        int day = timeStamp / timeDay;
        elementIndex = day % CharactersConfigManager.getInstance().getElementConfig().size();
        kingdomIndex = day % (int) CharactersConfigManager.getInstance().getKingdomSummonConfig().size();
    }

    public static ServerVariableModel createServerVariableModel(Zone zone){
        ServerVariableModel serverVariableModel = new ServerVariableModel();
        serverVariableModel.init();
        serverVariableModel.saveToDB(zone);

        return serverVariableModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.id), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ServerVariableModel copyFromDBtoObject(Zone zone) {
        ServerVariableModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(id), ServerVariableModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, ServerVariableModel.class);
                if (pInfo != null) {
//                    pInfo.wrNiteLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        if(pInfo == null){
            pInfo = createServerVariableModel(zone);
        }
        return pInfo;
    }



    /*-----------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------*/
    /**
     * Get Element Day
     * @param zone
     * @return
     */
    public int readElementDay(Zone zone){
        if(elementIndex >= CharactersConfigManager.getInstance().getElementConfig().size()){
            elementIndex = 0;
            new IndexOutOfBoundByChangeConfigException().printStackTrace();
            saveToDB(zone);
        }

        if(isNewDay()){
            updateNewDay(zone);
        }
        return elementIndex;
    }

    /**
     * Get Kingdom Day
     * @param zone
     * @return
     */
    public int readKingdomDay(Zone zone){
        if(kingdomIndex >= CharactersConfigManager.getInstance().getKingdomSummonConfig().size()){
            kingdomIndex = 0;
            new IndexOutOfBoundByChangeConfigException().printStackTrace();
            saveToDB(zone);
        }

        if(isNewDay()){
            updateNewDay(zone);
        }
        return kingdomIndex;
    }

    private void updateNewDay(Zone zone){
        int now = Utils.getTimestampInSecond();
        elementIndex = now / timeDay % CharactersConfigManager.getInstance().getElementConfig().size();
        kingdomIndex = now / timeDay % (int) CharactersConfigManager.getInstance().getKingdomSummonConfig().size();
        timeStamp = Utils.getTimestampInSecond();
        saveToDB(zone);
    }

    /**
     * Ckeck qua ngay moi
     * @return
     */
    private boolean isNewDay(){
        return Utils.isNewDay(timeStamp);
    }

    public Map<String,InfoConfigActive> readActiveEventModule(Zone zone){
        int now = Utils.getTimestampInSecond();
        boolean haveSave = false;
        for(String key : mapActiveConfig.keySet()){
            InfoConfigActive data = mapActiveConfig.get(key);

            if(data.timeStamp != -1 && now >= data.timeStamp){
                data.active = false;
                data.timeStamp = -1;
                haveSave = true;
            }
        }

        if(haveSave) saveToDB(zone);

        return mapActiveConfig;
    }

    public boolean updateActiveEventModule(EModule module, boolean active, int timeStamp, Zone zone){
        Map<String,InfoConfigActive> activeConfig = readActiveEventModule(zone);
        InfoConfigActive infoDefault = InfoConfigActive.create(module.getId());
        if(activeConfig.getOrDefault(module.getId(), infoDefault).active == active) return true;

        infoDefault.active = active;
        infoDefault.timeStamp = timeStamp;
        activeConfig.put(module.getId(), infoDefault);
        return saveToDB(zone);
    }
}
