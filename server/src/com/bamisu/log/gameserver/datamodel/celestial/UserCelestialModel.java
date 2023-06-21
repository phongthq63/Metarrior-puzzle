package com.bamisu.log.gameserver.datamodel.celestial;

import com.bamisu.log.gameserver.module.celestial.CelestialManager;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.celestial.entities.LevelVO;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserCelestialModel extends DataModel {
    public long uid;
    public String cid;      // celestial id
    public Set<String> unlock = new HashSet<>();
    public int elysianCubes = 0;

    private LevelVO LevelInfo;     //Level + exp --- khong co dinh



    public static UserCelestialModel createUserCelestialModel(long uid, Zone zone){
        UserCelestialModel userCelestialModel = new UserCelestialModel();
        userCelestialModel.uid = uid;
        userCelestialModel.cid = CharactersConfigManager.getInstance().getCelestialConfig().get(0).id;

        userCelestialModel.saveToDB(zone);

        return userCelestialModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.uid), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static UserCelestialModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    private static UserCelestialModel copyFromDBtoObject(String uId, Zone zone) {
        UserCelestialModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserCelestialModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserCelestialModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Get id Celestial
     */
    public String readIdCelestial(){
        if(cid == null || cid.isEmpty()){
            cid = CharactersConfigManager.getInstance().getCelestialConfig().get(0).id;
        }
        return cid;
    }

    /**
     * Get level Celestial
     */
    public short readLevelCelestial(Zone zone){
        if(LevelInfo == null){
            LevelInfo = CelestialManager.getInstance().getLevelCelestial(zone, uid, readIdCelestial());
        }
        return LevelInfo.lv;
    }

    /**
     * Get id Celestial
     */
    public long readExpCelestial(Zone zone){
        if(LevelInfo == null){
            LevelInfo = CelestialManager.getInstance().getLevelCelestial(zone, uid, readIdCelestial());
        }
        return LevelInfo.exp;
    }

    public List<String> readListCelestialUnlocked(Zone zone){
        String idCelestialDefault = CharactersConfigManager.getInstance().getCelestialConfig().get(0).id;
        if(!unlock.contains(idCelestialDefault)){
            unlock.add(idCelestialDefault);
            saveToDB(zone);
        }
        return new ArrayList<>(unlock);
    }
}
