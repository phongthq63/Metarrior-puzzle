package com.bamisu.log.gameserver.datamodel.arena;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.arena.ArenaManager;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.log.gameserver.module.vip.defines.EGiftVip;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;
import java.util.stream.Collectors;

public class UserArenaModel extends DataModel {

    public long uid;
    public int season;
    public int point;
    public short countFight;
    public int timeStamp;


    private final Object lockSeason = new Object();
    private final Object lockPoint = new Object();
    private final Object lockFight = new Object();



    public static UserArenaModel createUserArenaModel(long uid, Zone zone){
        UserArenaModel userArenaModel = new UserArenaModel();
        userArenaModel.uid = uid;
        userArenaModel.point = 1000;
        userArenaModel.timeStamp = Utils.getTimestampInSecond();
        userArenaModel.saveToDB(zone);

        return userArenaModel;
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

    public static UserArenaModel copyFromDBtoObject(long uid, Zone zone) {
        UserArenaModel userArenaModel = copyFromDBtoObject(String.valueOf(uid), zone);
        if(userArenaModel == null){
            userArenaModel = UserArenaModel.createUserArenaModel(uid, zone);
        }
        userArenaModel.updateNewSeason(zone);
        return userArenaModel;
    }

    private static UserArenaModel copyFromDBtoObject(String uid, Zone zone) {
        UserArenaModel pInfo = null;
        try {
            String str = (String) getModel(uid, UserArenaModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserArenaModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

    public static Map<Long, UserArenaModel> copyFromDBtoObject(List<Long> uids, Zone zone) {
        Map<Long, UserArenaModel> pInfo = null;
        try {
            Map<String, Object> strs = multiGet(uids.stream().map(Objects::toString).collect(Collectors.toList()), UserArenaModel.class, zone);
            if (strs != null) {
                pInfo = new HashMap<>();
                for (Object key : strs.keySet()) {
                    UserArenaModel userArenaModel = Utils.fromJson((String) strs.get(key), UserArenaModel.class);
                    if(userArenaModel == null){
                        userArenaModel = UserArenaModel.createUserArenaModel(Long.valueOf(key.toString()), zone);
                    }
                    userArenaModel.updateNewSeason(zone);
                    pInfo.put(Long.valueOf(key.toString().split(SEPERATOR)[1]), userArenaModel);
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    public boolean increaseArenaPoint(int point, Zone zone){
        synchronized (lockPoint){
            this.point += point;
        }
        return saveToDB(zone);
    }

    public int readArenaPoint(){
        synchronized (lockPoint){
            return point;
        }
    }

    public int readCountFightFree(Zone zone){
        synchronized (lockFight){
            int freeFight = VipManager.getInstance().getBonus(uid, zone, EGiftVip.FREE_BATTLES_IN_ARENA);
            int free = freeFight - readCountFight(zone);
            return (free > 0) ? free : 0;
        }
    }

    public boolean fightArena(Zone zone){
        synchronized (lockFight){
            countFight++;
            return saveToDB(zone);
        }
    }

    public int readCountFight(Zone zone){
        if(Utils.isNewDay(timeStamp)){
            countFight = 0;
            timeStamp = Utils.getTimestampInSecond();
            saveToDB(zone);
        }
        return countFight;
    }

    public int readSeason(){
        synchronized (lockSeason){
            return season;
        }
    }

    public boolean updateNewSeason(Zone zone){
        int currentSeason = ArenaManager.getInstance().getCurrentSeason(zone);
        synchronized (lockSeason){
            if(season != currentSeason){
                season = currentSeason;
                point = ArenaManager.getInstance().getRankArenaConfig(point).endSeason;
                return saveToDB(zone);
            }
        }
        return true;
    }
}
