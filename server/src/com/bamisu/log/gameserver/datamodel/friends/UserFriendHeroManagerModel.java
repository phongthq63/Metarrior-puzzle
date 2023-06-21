package com.bamisu.log.gameserver.datamodel.friends;

import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.log.gameserver.module.friends.FriendHeroManager;
import com.bamisu.log.gameserver.module.hero.define.ETeamType;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;

public class UserFriendHeroManagerModel extends DataModel {

    public long uid;
    public Map<String,Integer> mapCountAssign = new HashMap<>();
    public int timeStamp;

    private final Object lockAssign = new Object();



    public static UserFriendHeroManagerModel create(long uid, Zone zone){
        UserFriendHeroManagerModel userFriendHeroManagerModel = new UserFriendHeroManagerModel();
        userFriendHeroManagerModel.uid = uid;
        userFriendHeroManagerModel.timeStamp = Utils.getTimestampInSecond();
        userFriendHeroManagerModel.saveToDB(zone);

        return userFriendHeroManagerModel;
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

    public static UserFriendHeroManagerModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    private static UserFriendHeroManagerModel copyFromDBtoObject(String uId, Zone zone) {
        UserFriendHeroManagerModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserFriendHeroManagerModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserFriendHeroManagerModel.class);
                if (pInfo != null) {
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if (pInfo == null) {
            pInfo = UserFriendHeroManagerModel.create(Long.parseLong(uId), zone);
        }

        return pInfo;
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    public int getCountAssignHero(ETeamType teamType, Zone zone){
        synchronized (lockAssign){
            if(isTimeRefreshCountAssign()){
                refreshCountAssignHero(zone);
            }

            return mapCountAssign.getOrDefault(teamType.getId(), 0);
        }
    }

    public boolean updateCountAssignHero(ETeamType teamType, int count, Zone zone){
        synchronized (lockAssign){
            int countSave = mapCountAssign.getOrDefault(teamType.getId(), 0);
            mapCountAssign.put(teamType.getId(), countSave + count);
            return saveToDB(zone);
        }
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    private boolean isTimeRefreshCountAssign(){
        return TimeUtils.isTimeTo(ETimeType.fromID(FriendHeroManager.getInstance().getTimeRefreshCountAssignHeroConfig()), timeStamp);
    }

    private void refreshCountAssignHero(Zone zone){
        mapCountAssign.clear();
        timeStamp = Utils.getTimestampInSecond();
        saveToDB(zone);
    }
}
