package com.bamisu.log.gameserver.datamodel.lucky;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.lucky.entities.LuckyWinner;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class LuckyWinnerModel extends DataModel {
    public String uid;
    public List<LuckyWinner> listSOGWin1;
    public List<LuckyWinner> listSOGWin2;
    public List<LuckyWinner> listSOGWin3;
    public List<LuckyWinner> listMEWAWin1;
    public List<LuckyWinner> listMEWAWin2;
    public List<LuckyWinner> listMEWAWin3;

    public static LuckyWinnerModel copyFromDBtoObject(String uId, Zone zone) {
        LuckyWinnerModel pInfo = null;
        try {
            String str = (String) getModel(uId, LuckyWinnerModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, LuckyWinnerModel.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

    public static LuckyWinnerModel createLuckyWinnerModel(Zone zone) {
        LuckyWinnerModel lwm = new LuckyWinnerModel();
        lwm.uid = Utils.dateNowToLong();
        lwm.listSOGWin1 = new ArrayList<>();
        lwm.listSOGWin2 = new ArrayList<>();
        lwm.listSOGWin3 = new ArrayList<>();
        lwm.listMEWAWin1 = new ArrayList<>();
        lwm.listMEWAWin2 = new ArrayList<>();
        lwm.listMEWAWin3 = new ArrayList<>();
        lwm.saveToDB(zone);
        return lwm;
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
