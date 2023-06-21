package com.bamisu.log.gameserver.datamodel.hunt;

import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.datamodel.hunt.entities.HuntInfo;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.log.gameserver.module.vip.defines.EGiftVip;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.List;


public class UserHuntModel extends DataModel {

    public long uid;
    public HuntInfo huntInfo;
    public int timeStamp;
    public long winHunt;
    public int lose;

    public static UserHuntModel createUserHuntModel(long uid, Zone zone) {
        UserHuntModel userHuntModel = new UserHuntModel();
        userHuntModel.uid = uid;
        userHuntModel.huntInfo = userHuntModel.genHunt(uid, zone);
        userHuntModel.timeStamp = Utils.getTimestampInSecond();
        userHuntModel.saveToDB(zone);

        return userHuntModel;
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

    public static UserHuntModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    private static UserHuntModel copyFromDBtoObject(String uId, Zone zone) {
        UserHuntModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserHuntModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserHuntModel.class);
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
     * Lam moi hoan toan ngay moi
     */
    private void refreshDay(Zone zone){
        //Lam moi cuoc san
        huntInfo = genHunt(uid, zone);
        //Luu time moi
        timeStamp = Utils.getTimestampInSecond();
        //Luu data
        saveToDB(zone);
    }

    public void refreshHunt(Zone zone){
        huntInfo = genHunt(uid, zone, isNewDay());
        saveToDB(zone);
    }

    /**
     * gen hunt
     * @return
     */
    public HuntInfo genHunt(long uid, Zone zone){
//        int s = 0;
//        int d = 0;
//        List<HeroModel> heroModelList = HeroManager.getInstance().getTeamStrongestUserHeroModel(uid, zone);
//        for(HeroModel heroModel : heroModelList){
//            if(heroModel != null){
//                s += heroModel.readLevel();
//                d ++;
//            }
//        }
//        int avgLevel = s / d;
        return genHunt(uid, zone, true);
    }
    public HuntInfo genHunt(long uid, Zone zone, boolean isNewDay){
        return HuntInfo.create(HeroManager.getInstance().getMaxLevelUpHeroModel(uid, zone), isNewDay);
    }

    public HuntInfo readHunt(Zone zone){
        if(isNewDay()){
            refreshDay(zone);
        }
        return huntInfo;
    }

    public boolean updateHpEnemy(List<Float> listHp, Zone zone){
        huntInfo.updateCurrentHpEnemy(listHp);
        return saveToDB(zone);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    public int readLose(){
        return lose;
    }

    public boolean increaseLose(Zone zone){
        lose++;
        return saveToDB(zone);
    }

    /**
     * Kiem tra xem qua ngay moi chua
     * @return
     */
    private boolean isNewDay(){
        if(Utils.isNewDay(timeStamp)){
            return true;
        }
        return false;
    }
}
