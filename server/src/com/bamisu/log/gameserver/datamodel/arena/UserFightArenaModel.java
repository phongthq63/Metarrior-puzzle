package com.bamisu.log.gameserver.datamodel.arena;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.arena.ArenaManager;
import com.smartfoxserver.v2.entities.Zone;

import java.util.List;
import java.util.stream.Collectors;

public class UserFightArenaModel extends DataModel {

    public long uid;
    public int saeson;
    public List<Long> fight;
    public int timeStamp;



    public static UserFightArenaModel createUserFightArenaModel(long uid, Zone zone){
        UserFightArenaModel userFightArenaModel = new UserFightArenaModel();
        userFightArenaModel.uid = uid;
        userFightArenaModel.saeson = ArenaManager.getInstance().getCurrentSeason(zone);
        userFightArenaModel.fight = ArenaManager.getInstance().searchEnemyArena(uid, zone).parallelStream().
                collect(Collectors.toList());
        userFightArenaModel.timeStamp = 0;
        userFightArenaModel.saveToDB(zone);

        return userFightArenaModel;
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

    public static UserFightArenaModel copyFromDBtoObject(long uid, Zone zone) {
        UserFightArenaModel userFightArenaModel = copyFromDBtoObject(String.valueOf(uid), zone);
        if(userFightArenaModel == null){
            userFightArenaModel = UserFightArenaModel.createUserFightArenaModel(uid, zone);
        }
        return userFightArenaModel;
    }

    private static UserFightArenaModel copyFromDBtoObject(String uid, Zone zone) {
        UserFightArenaModel pInfo = null;
        try {
            String str = (String) getModel(uid, UserFightArenaModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserFightArenaModel.class);
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
    public boolean canRefresh(Zone zone){
        int now = Utils.getTimestampInSecond();
        if(now - timeStamp > 2){
            timeStamp = now;
            return saveToDB(zone);
        }else {
            return false;
        }
    }

    public boolean updateSeason(int season, Zone zone){
        this.saeson = season;
        return saveToDB(zone);
    }
}
