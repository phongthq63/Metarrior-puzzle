package com.bamisu.log.gameserver.datamodel.hero;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroSlotBlessing;
import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;

public class UserBlessingHeroModel extends DataModel {

    public long uid;
    public short size = CharactersConfigManager.getInstance().getUnlockBlessingConfig().initOpen;
    public short unlockEssence = 0;
    public short unlockDiamont = 0;

    public boolean haveRefreshBlessing = true;     //Co che giup khong p duyet nh lan
    public List<HeroSlotBlessing> listBlessing = new ArrayList<>();


    public static UserBlessingHeroModel createUserBlessingHeroModel(long uid, Zone zone) {
        UserBlessingHeroModel userBlessingHeroModel = new UserBlessingHeroModel();
        userBlessingHeroModel.uid = uid;
        userBlessingHeroModel.saveToDB(zone);

        return userBlessingHeroModel;
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

    public static UserBlessingHeroModel copyFromDBtoObject(long uId, Zone zone) {
        UserBlessingHeroModel userBlessingHeroModel = copyFromDBtoObject(String.valueOf(uId), zone);
        if(userBlessingHeroModel == null){
            userBlessingHeroModel = UserBlessingHeroModel.createUserBlessingHeroModel(uId, zone);
        }
        return userBlessingHeroModel;
    }

    private static UserBlessingHeroModel copyFromDBtoObject(String uId, Zone zone) {
        UserBlessingHeroModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserBlessingHeroModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserBlessingHeroModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }



    /*--------------------------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------------------------*/
    /**
     * Lay list slot ban phuoc (da kiem tra xem co can lam moi khong)
     * @param zone
     * @return
     */
    public List<HeroSlotBlessing> readSlotBlessing(Zone zone){
        if(haveRefreshBlessing){
            refreshListBlessing();
            saveToDB(zone);
        }
        return listBlessing;
    }
    public HeroSlotBlessing readSlotBlessing(int position, Zone zone){
        if(haveRefreshBlessing){
            refreshListBlessing();
            saveToDB(zone);
        }

        for(HeroSlotBlessing slot : listBlessing){
            if(slot.position == position){
                return slot;
            }
        }
        return null;
    }


    /**
     * Add hero
     */
    public boolean addHeroBlessing(String hashHero, String idHero, int star, int level, int position, Zone zone){
        for(HeroSlotBlessing slot : listBlessing){
            if(slot.position == position || hashHero.equals(slot.hashHero)){
                return false;
            }
        }

        listBlessing.add(HeroSlotBlessing.create(hashHero, idHero, level, position));

        return saveToDB(zone);
    }

    /**
     * Delete hero
     */
    public boolean removeHeroBlessing(String hashHero, Zone zone){
        for(HeroSlotBlessing slot : listBlessing){
            if(hashHero.equals(slot.hashHero)){
                slot.hashHero = null;
                slot.idHero = null;
                slot.timeStamp = Utils.getTimestampInSecond();

                haveRefreshBlessing = true;
                return saveToDB(zone);
            }
        }
        return false;
    }
    public boolean removeHeroBlessing(List<String> listHashHero, Zone zone){
        Iterator<HeroSlotBlessing> iterator = listBlessing.iterator();
        while (iterator.hasNext()){
            HeroSlotBlessing slot = iterator.next();

            if(listHashHero.contains(slot.hashHero)){
                iterator.remove();
                haveRefreshBlessing = true;
            }
        }
        return saveToDB(zone);
    }


    public boolean removeSlotBlessingCountdown(int position, Zone zone){
        for(int i = 0; i < listBlessing.size(); i++){
            if(listBlessing.get(i).position == position){
                listBlessing.remove(i);
                return saveToDB(zone);
            }
        }
        return false;
    }


    /**
     * Mo khoa o blessing
     */
    public boolean unlockSlotBlessing(MoneyType moneyType, Zone zone){
        switch (moneyType){
            case MIRAGE_ESSENCE:
                unlockEssence++;
                break;
            case DIAMOND:
                unlockDiamont++;
                break;
            default:
                return false;
        }
        size++;

        //Event
        Map<String,Object> eventData = new HashMap<>();
        int count = unlockEssence + unlockDiamont;
        eventData.put(Params.COUNT, count);
        GameEventAPI.ariseGameEvent(EGameEvent.OPEN_SLOT_BLESSING_HERO, uid, eventData, zone);

        return saveToDB(zone);
    }






    /*--------------------------------------------------------------------------------------------------------------*/
    /**
     * Lam moi slot ban puoc
     */
    private void refreshListBlessing(){
        Iterator<HeroSlotBlessing> iterator = listBlessing.iterator();
        HeroSlotBlessing slot;
        boolean flag = true;
        while (iterator.hasNext()){
            slot = iterator.next();
            if(!slot.haveCheck()){
                continue;
            }else {
                //Kiem tra xem loai bo dc khong
                if(slot.canRemove()){
                    iterator.remove();
                    continue;
                }
                if(flag){
                    flag = false;
                }
            }
        }
        haveRefreshBlessing = !flag;
    }
}
