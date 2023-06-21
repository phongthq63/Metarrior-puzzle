package com.bamisu.log.gameserver.datamodel.campaign;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.adventure.entities.AFKDetail;
import com.smartfoxserver.v2.entities.Zone;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by Popeye on 11:26 AM, 12/28/2020
 */
public class AFKDetailModel extends DataModel {
    public static final String key = "0";
    public Map<Long, AFKDetail> mapAFKDetail = new HashMap();

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(key, zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static AFKDetailModel copyFromDBtoObject(Zone zone) {
        AFKDetailModel model = null;
        try {
            String str = (String) getModel(key, AFKDetailModel.class, zone);
            if (str != null) {
                model = Utils.fromJson(str, AFKDetailModel.class);
                if (model != null) {
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if (model == null) {
            model = new AFKDetailModel();
            model.saveToDB(zone);
        }

        return model;
    }
}
