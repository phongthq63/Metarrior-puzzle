package com.bamisu.log.gameserver.datamodel.guild;

import com.bamisu.log.gameserver.datamodel.guild.entities.GiftGuildInfo;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;

public class UserGuildModel extends DataModel {
    public long uid;
    public long gid;
    public short createGuild;
    public boolean checkIn = false;

    public List<GiftGuildInfo> giftSave = new ArrayList<>();
    public Set<String> giftClaimed = new HashSet<>();

    public int resetDayTimeStamp;
    public int joinGuildTimestamp;   //dùng để check in guild
    public int leaveGuildTimestamp;   //dùng để check join guild
    public int searchTimestamp; //dùng để check search quá nhanh

    private final static int time1day = 86400;

    private final Object lockGuild = new Object();
    private final Object lockGift = new Object();



    public static UserGuildModel createUserGuildModel(long uid, long gid, Zone zone){
        UserGuildModel userGuildModel = new UserGuildModel();
        userGuildModel.uid = uid;
        userGuildModel.gid = gid;
        userGuildModel.resetDayTimeStamp = Utils.getTimestampInSecond();
        userGuildModel.saveToDB(zone);

        return userGuildModel;
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

    public static UserGuildModel copyFromDBtoObject(long uid, Zone zone) {
        UserGuildModel userGuildModel = copyFromDBtoObject(String.valueOf(uid), zone);
        if(userGuildModel == null){
            userGuildModel = UserGuildModel.createUserGuildModel(uid, 0, zone);
        }
        return userGuildModel;
    }

    private static UserGuildModel copyFromDBtoObject(String uid, Zone zone) {
        UserGuildModel pInfo = null;
        try {
            String str = (String) getModel(uid, UserGuildModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserGuildModel.class);
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
     * Tao Guild
     */
    public boolean createGuild(long gid, Zone zone){
        synchronized (lockGuild){
            this.gid = gid;
            this.createGuild++;
            this.joinGuildTimestamp = Utils.getTimestampInSecond();
        }
        return saveToDB(zone);
    }

    public boolean canJoinGuild(){
        //TH vao guild lan dau -> chua roi Guild lan nao -> leaveGuildTimestamp = 0
        if(leaveGuildTimestamp == 0 || Utils.getTimestampInSecond() - leaveGuildTimestamp > GuildManager.getInstance().getTimeRequestJoinGuild()){
            return true;
        }
        return false;
    }
    /**
     * Vao guild
     */
    public boolean joinGuild(long gid, Zone zone){
        synchronized (lockGuild){
            this.gid = gid;
            this.joinGuildTimestamp = Utils.getTimestampInSecond();
        }
        return saveToDB(zone);
    }

    /**
     * In Guild
     */
    public boolean inGuild(){
        synchronized (lockGuild){
            return gid != 0;
        }
    }

    /**
     * Luu time thoat guild
     */
    public boolean outGuild(Zone zone){
        synchronized (lockGuild){
            gid = 0;
            joinGuildTimestamp = -1;
            leaveGuildTimestamp = Utils.getTimestampInSecond();
            giftSave.clear();
            giftClaimed.clear();
            return saveToDB(zone);
        }
    }

    public boolean haveCheckIn(Zone zone){
        if(isNewDay()){
            checkIn = false;
            resetDayTimeStamp = Utils.getTimestampInSecond();
            saveToDB(zone);
        }
        if(!inGuild()) return true;
        return checkIn;
    }

    public boolean checkIn(Zone zone){
        checkIn = true;
        return saveToDB(zone);
    }

    public Set<String> readListGiftClaimedGuild(Zone zone){
        synchronized (lockGift){
            if(giftClaimed == null){
                giftClaimed = new HashSet<>();
                saveToDB(zone);
            }

            return giftClaimed;
        }
    }
    public List<GiftGuildInfo> readListGiftSaveGuild(Zone zone){
        synchronized (lockGift){
            if(giftSave == null){
                giftSave = new ArrayList<>();
                saveToDB(zone);
            }else {
                int now = Utils.getTimestampInSecond();
                boolean haveSave = false;

                Iterator<GiftGuildInfo> iterator = giftSave.iterator();
                GiftGuildInfo giftInfo;
                while (iterator.hasNext()){
                    giftInfo = iterator.next();

                    if(giftInfo.timeExpert != -1 && giftInfo.timeExpert + time1day <= now){
                        giftClaimed.remove(giftInfo.hash);
                        iterator.remove();
                        haveSave = true;
                    }
                }

                if(haveSave) saveToDB(zone);
            }

            return giftSave;
        }
    }

    public boolean claimGiftGuild(GiftGuildInfo gift, Zone zone){
        synchronized (lockGift){
            giftSave.add(gift);
            giftClaimed.add(gift.hash);
            return saveToDB(zone);
        }
    }

    public boolean removeGiftGuild(String hash, Zone zone){
        synchronized (lockGift){
            for(int i = 0; i < giftSave.size(); i++){
                if(giftSave.get(i).hash.equals(hash)){
                    giftSave.remove(i);
                    return saveToDB(zone);
                }
            }
        }
        return false;
    }
    public boolean removeAllGiftGuild(Zone zone){
        synchronized (lockGift){
            giftSave.clear();
            return saveToDB(zone);
        }
    }

    public int readTimeJoinGuild(Zone zone){
        if(joinGuildTimestamp == 0){
            joinGuildTimestamp = Utils.getTimestampInSecond();
            saveToDB(zone);
        }
        return joinGuildTimestamp;
    }


    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Kiem tra xem ngay moi khong
     * @return
     */
    private boolean isNewDay(){
        return Utils.isNewDay(resetDayTimeStamp);
    }


    public boolean isTimeRefreshSearch(Zone zone){
        int now = Utils.getTimestampInSecond();
        if(searchTimestamp == 0 || now - searchTimestamp >= GuildManager.getInstance().getTimeRefreshSearchGuild()){
            searchTimestamp = now;
            return saveToDB(zone);
        }
        return false;
    }
}
