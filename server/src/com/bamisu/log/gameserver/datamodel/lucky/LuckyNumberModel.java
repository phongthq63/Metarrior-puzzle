package com.bamisu.log.gameserver.datamodel.lucky;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;

import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class LuckyNumberModel extends DataModel {
    public int num1;
    public int num2;
    public int num3;
    public int time; //tam thoi chua dung
    public String day;
    String uid;

    public static LuckyNumberModel createLuckyNumberModel(int num1, int num2, int num3, int time, String day) {
        LuckyNumberModel u = new LuckyNumberModel();
        u.num1 = num1;
        u.num2 = num2;
        u.num3 = num3;
        u.time = time;
        u.day = day;
        u.uid = Utils.dateNowToLong();
        return u;
    }

    public static LuckyNumberModel copyFromDBtoObject(String uId, Zone zone) {
        LuckyNumberModel pInfo = null;
        try {
            String str = (String) getModel(uId, LuckyNumberModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, LuckyNumberModel.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
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
}
