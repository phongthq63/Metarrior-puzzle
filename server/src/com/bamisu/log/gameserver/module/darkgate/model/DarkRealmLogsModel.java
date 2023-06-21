package com.bamisu.log.gameserver.module.darkgate.model;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.darkgate.model.entities.DarkRealmLogVO;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 4:46 PM, 11/26/2020
 */
public class DarkRealmLogsModel extends DataModel {
    public long uid;
    public List<DarkRealmLogVO> logs = new ArrayList<>();

    public DarkRealmLogsModel() {

    }

    public DarkRealmLogsModel(long uid) {
        this.uid = uid;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(uid + "", zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static DarkRealmLogsModel copyFromDBtoObject(long uid, Zone zone) {
        DarkRealmLogsModel model = null;
        try {
            String str = (String) getModel(uid + "", DarkRealmLogsModel.class, zone);
            if (str != null) {
                model = Utils.fromJson(str, DarkRealmLogsModel.class);
                if (model != null) {
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if (model == null) {
            model = new DarkRealmLogsModel(uid);
            model.saveToDB(zone);
        }

        return model;
    }

    public void push(int time, long point, Zone zone){
        if(logs.size() > 50){
            logs.remove(0);
        }
        logs.add(new DarkRealmLogVO(time, point));
        saveToDB(zone);
    }
}
