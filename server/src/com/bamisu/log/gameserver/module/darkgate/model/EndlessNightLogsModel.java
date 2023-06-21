package com.bamisu.log.gameserver.module.darkgate.model;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.darkgate.model.entities.EndlessNightLogVO;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 4:47 PM, 11/26/2020
 */
public class EndlessNightLogsModel extends DataModel {
    public long uid;
    public List<EndlessNightLogVO> logs = new ArrayList<>();

    public EndlessNightLogsModel() {
        logs = new ArrayList<>();
    }

    public EndlessNightLogsModel(long uid) {
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

    public static EndlessNightLogsModel copyFromDBtoObject(long uid, Zone zone) {
        EndlessNightLogsModel model = null;
        try {
            String str = (String) getModel(uid + "", EndlessNightLogsModel.class, zone);
            if (str != null) {
                model = Utils.fromJson(str, EndlessNightLogsModel.class);
                if (model != null) {
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if (model == null) {
            model = new EndlessNightLogsModel(uid);
            model.saveToDB(zone);
        }

        if(model.logs == null) model.logs = new ArrayList<>();

        return model;
    }

    public void push(int time, long point, Zone zone){
        if(logs.size() > 50){
            logs.remove(0);
        }
        logs.add(new EndlessNightLogVO(time, point));
        saveToDB(zone);
    }
}