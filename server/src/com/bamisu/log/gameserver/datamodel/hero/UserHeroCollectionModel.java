package com.bamisu.log.gameserver.datamodel.hero;

import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHeroCollectionModel extends DataModel {
    public long uid;
    public Map<String, Short> bonusStory = new HashMap<>();       //Id hero - Short -> kiem tra da nhan bonus story

    public static UserHeroCollectionModel createUserHeroCollectionModel(long uid, List<String> listIdBegin, Zone zone){
        UserHeroCollectionModel userHeroCollectionModel = new UserHeroCollectionModel();
        userHeroCollectionModel.uid = uid;
        userHeroCollectionModel.addBonusStoryHero(listIdBegin, zone);
//        userHeroCollectionModel.saveToDB(zone);

        return userHeroCollectionModel;
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

    public static UserHeroCollectionModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    private static UserHeroCollectionModel copyFromDBtoObject(String uId, Zone zone) {
        UserHeroCollectionModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserHeroCollectionModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserHeroCollectionModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }


    /*-------------------------------------------------------------------------------------------------------------*/
    /*-------------------------------------------------------------------------------------------------------------*/
    /**
     * Check xem da ton tai ko
     */
    public boolean isExistBonusStoryHero(String idHero){
        if(bonusStory.containsKey(idHero)){
            return true;
        }
        return false;
    }

    /**
     * Summon Hero
     */
    public boolean addBonusStoryHero(String idHero, Zone zone){
        if(isExistBonusStoryHero(idHero)){
            return false;
        }
        bonusStory.put(idHero, CharactersConfigManager.getInstance().getBonusStoryHero(idHero));
        return saveToDB(zone);
    }
    public boolean addBonusStoryHero(List<String> listIdHero, Zone zone){
        boolean flat = false;
        for(int i = 0; i < listIdHero.size(); i++){
            if(isExistBonusStoryHero(listIdHero.get(i))){
                continue;
            }
            bonusStory.put(listIdHero.get(i), CharactersConfigManager.getInstance().getBonusStoryHero(listIdHero.get(i)));
            flat = true;
        }
        if(flat){
            return saveToDB(zone);
        }
        return false;
    }


}
