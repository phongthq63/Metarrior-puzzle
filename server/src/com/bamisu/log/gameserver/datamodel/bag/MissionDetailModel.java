package com.bamisu.log.gameserver.datamodel.bag;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.bag.entities.MissionDetail;
import com.smartfoxserver.v2.entities.Zone;

import java.util.HashMap;
import java.util.Map;

public class MissionDetailModel extends DataModel {
    public static final String key = "0";
    public Map<Long, MissionDetail> mapMissionDetail = new HashMap();

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(key, zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static MissionDetailModel copyFromDBtoObject(Zone zone) {
        MissionDetailModel model = null;
        try {
            String str = (String) getModel(key, MissionDetailModel.class, zone);
            if (str != null) {
                model = Utils.fromJson(str, MissionDetailModel.class);
                if (model != null) {
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if (model == null) {
            model = new MissionDetailModel();
            model.saveToDB(zone);
        }

        return model;
    }
}
