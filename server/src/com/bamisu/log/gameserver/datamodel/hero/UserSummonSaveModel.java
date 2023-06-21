package com.bamisu.log.gameserver.datamodel.hero;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.hero.entities.CountSummonInfo;
import com.smartfoxserver.v2.entities.Zone;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Quach Thanh Phong
 * On 7/12/2021 - 11:59 PM
 */
public class UserSummonSaveModel extends DataModel {

    public long uid;
    public boolean guaranteedPurple1 = false;
    public boolean guaranteedPurple10 = false;
    public Map<String, CountSummonInfo> summon = new HashMap<>();



    public static UserSummonSaveModel createUserSummonSaveModel(long uid, Zone zone){
        UserSummonSaveModel userSummonSaveModel = new UserSummonSaveModel();
        userSummonSaveModel.uid = uid;
        userSummonSaveModel.saveToDB(zone);

        return userSummonSaveModel;
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

    public static UserSummonSaveModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    private static UserSummonSaveModel copyFromDBtoObject(String uId, Zone zone) {
        UserSummonSaveModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserSummonSaveModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserSummonSaveModel.class);
                if (pInfo != null) {
//                    pInfo.wrNiteLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }



    /*-----------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------*/
    public boolean haveGuaranteedPurple11(){
        return guaranteedPurple1;
    }
    public boolean haveGuaranteedPurple10(){
        return guaranteedPurple10;
    }

    public void updateGuaranteedPurple1(boolean value, Zone zone){
        guaranteedPurple1 = value;
        saveToDB(zone);
    }
    public void updateGuaranteedPurple10(boolean value, Zone zone){
        guaranteedPurple10 = value;
        saveToDB(zone);
    }

    public int readSummon1(String idSummon){
        return summon.getOrDefault(idSummon, CountSummonInfo.create(idSummon)).summon1;
    }

    public int readSummon10(String idSummon){
        return summon.getOrDefault(idSummon, CountSummonInfo.create(idSummon)).summon10;
    }

    public void updateSummon1(String idSummon, int count, Zone zone){
        CountSummonInfo data = summon.getOrDefault(idSummon, CountSummonInfo.create(idSummon));
        if(data.summon1 == count) return;

        data.summon1 = count;
        summon.put(idSummon, data);
        saveToDB(zone);
    }

    public void updateSummon10(String idSummon, int count, Zone zone){
        CountSummonInfo data = summon.getOrDefault(idSummon, CountSummonInfo.create(idSummon));
        if(data.summon10 == count) return;

        data.summon10 = count;
        summon.put(idSummon, data);
        saveToDB(zone);
    }
}
