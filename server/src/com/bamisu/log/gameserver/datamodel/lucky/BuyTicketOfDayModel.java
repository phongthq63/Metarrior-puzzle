package com.bamisu.log.gameserver.datamodel.lucky;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.lucky.entities.LuckyBuyTicketInfo;
import com.smartfoxserver.v2.entities.Zone;
import java.util.ArrayList;
import java.util.List;

public class BuyTicketOfDayModel extends DataModel {
    public String uid;
    public List<LuckyBuyTicketInfo> history;

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.uid), zone);
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static BuyTicketOfDayModel copyFromDBtoObject(String uId, Zone zone) {
        BuyTicketOfDayModel pInfo = null;
        try {
            String str = (String) getModel(uId, BuyTicketOfDayModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, BuyTicketOfDayModel.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

    public static BuyTicketOfDayModel createUserHistory(Zone zone) {
        BuyTicketOfDayModel userHisModel = new BuyTicketOfDayModel();
        userHisModel.uid = Utils.dateNowToLong();
        userHisModel.history = new ArrayList<>();
        userHisModel.saveToDB(zone);
        return userHisModel;
    }

}
