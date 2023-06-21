package com.bamisu.log.gameserver.datamodel.arena;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.log.gameserver.module.arena.defind.EArenaStatus;
import com.smartfoxserver.v2.entities.Zone;

public class ArenaManagerModel extends DataModel {
    private static final long id = 0;

    public short season;
    public String status;
    public boolean sendGift;
    public int timeStamp;


    private final static int time12h = 43200;

    private final Object lockSeason = new Object();



    public static ArenaManagerModel createRankArenaModel(Zone zone){
        ArenaManagerModel arenaManagerModel = new ArenaManagerModel();
        arenaManagerModel.season = 0;
        arenaManagerModel.status = EArenaStatus.OPEN.getId();
        arenaManagerModel.sendGift = false;
        arenaManagerModel.timeStamp = Utils.getTimestampInSecond();
        arenaManagerModel.saveToDB(zone);

        return arenaManagerModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(id), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArenaManagerModel copyFromDBtoObject(Zone zone) {
        ArenaManagerModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(id), ArenaManagerModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, ArenaManagerModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        if(pInfo == null){
            pInfo = ArenaManagerModel.createRankArenaModel(zone);
        }
        return pInfo;
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    public String readStatusArena(){
        synchronized (lockSeason){
            if(status == null){
                if(TimeUtils.isTimeTo(ETimeType.NEW_2_WEEK, timeStamp + time12h)){
                    status = EArenaStatus.CLOSE.getId();
                }else {
                    status = EArenaStatus.OPEN.getId();
                }
            }
            return status;
        }
    }

    public boolean isSeasonEnd(){
        synchronized (lockSeason){
            return readStatusArena().equals(EArenaStatus.CLOSE.getId());
        }
    }
    public boolean isSeasonOpen(){
        synchronized (lockSeason){
            return readStatusArena().equals(EArenaStatus.OPEN.getId());
        }
    }


    public int readTimeEndSeason(){
        synchronized (lockSeason){
            //Kiem tra qua 2 tuan chua
            if(TimeUtils.isTimeTo(ETimeType.NEW_2_WEEK, timeStamp)) return 0;
            //update trc 12h
            int timeEnd = TimeUtils.getDeltaTimeToTime(ETimeType.NEW_2_WEEK, timeStamp) - time12h;
            return (timeEnd > 0) ? timeEnd : 0;
        }
    }

    public int readTimeOpenSeason(){
        synchronized (lockSeason){
            //Kiem tra qua 2 tuan chua
            if(TimeUtils.isTimeTo(ETimeType.NEW_2_WEEK, timeStamp)) return 0;
            int timeEnd = TimeUtils.getDeltaTimeToTime(ETimeType.NEW_2_WEEK, timeStamp);

            return (timeEnd > 0) ? timeEnd : 0;
        }
    }

    public int readSeason(){
        synchronized (lockSeason){
            return season;
        }
    }


    public boolean updateOpenSeason(Zone zone){
        synchronized (lockSeason){
            status = EArenaStatus.OPEN.getId();
            season++;
            sendGift = false;
            timeStamp = Utils.getTimestampInSecond();
            return saveToDB(zone);
        }
    }
    public boolean updateCloseSeason(Zone zone){
        synchronized (lockSeason){
            status = EArenaStatus.CLOSE.getId();
            return saveToDB(zone);
        }
    }

    public boolean haveSendGift(){
        return sendGift;
    }
    public boolean updateSendGift(Zone zone){
        sendGift = true;
        return saveToDB(zone);
    }
}
