package com.bamisu.log.gameserver.datamodel.event;

import com.bamisu.log.gameserver.module.WoL.defines.WoLConquerStatus;
import com.bamisu.log.gameserver.module.event.event.grand_opening_checkin.GrandOpeningCheckInManager;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class GrandOpeningCheckInModel extends DataModel {
    public long uid;
    public List<Integer> listGift = new ArrayList<>(); //Status of gift
    public long time = GrandOpeningCheckInManager.getInstance().getNewTime();
    public GrandOpeningCheckInModel(long uId) {
        this.uid = uId;
        init();
    }

    private void init() {
        for (int i = 0; i< GrandOpeningCheckInManager.getInstance().getListGift().size(); i++){
            if (i == 0){
                this.listGift.add(WoLConquerStatus.CAN_RECEIVE.getStatus());
            }else{
                this.listGift.add(WoLConquerStatus.INCOMPLETE.getStatus());
            }
        }
    }

    public GrandOpeningCheckInModel() {
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

    public static GrandOpeningCheckInModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    public static GrandOpeningCheckInModel copyFromDBtoObject(String uId, Zone zone) {
        GrandOpeningCheckInModel pInfo = null;
        try {
            String str = (String) getModel(uId, GrandOpeningCheckInModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, GrandOpeningCheckInModel.class);
                if (pInfo != null) {
                }
            }
        } catch (DataControllerException e) {
        }
        if (pInfo == null) {
            pInfo = new GrandOpeningCheckInModel(Long.parseLong(uId));
            pInfo.saveToDB(zone);
        }
        return pInfo;
    }

    public static GrandOpeningCheckInModel create(long uId, Zone zone) {
        GrandOpeningCheckInModel d = new GrandOpeningCheckInModel(uId);
        if (d.saveToDB(zone)) {
            return d;
        }
        return null;
    }
}
