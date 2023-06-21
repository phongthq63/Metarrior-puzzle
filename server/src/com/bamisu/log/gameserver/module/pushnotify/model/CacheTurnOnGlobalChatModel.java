package com.bamisu.log.gameserver.module.pushnotify.model;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by Popeye on 11:54 AM, 1/20/2021
 */
public class CacheTurnOnGlobalChatModel extends DataModel {
    private static final String id = "0";

    public List<Long> listTurnOnGlobal = new ArrayList<>();
    public Map<String, List<Long>> mapTurnOnChanel = new HashMap<>();

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(id, zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static CacheTurnOnGlobalChatModel copyFromDBtoObject(Zone zone) {
        CacheTurnOnGlobalChatModel model = null;
        try {
            String str = (String) getModel(id, CacheTurnOnGlobalChatModel.class, zone);
            if (str != null) {
                model = Utils.fromJson(str, CacheTurnOnGlobalChatModel.class);
                if (model != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if(model == null){
            model = new CacheTurnOnGlobalChatModel();
            model.saveToDB(zone);
        }
        return model;
    }
}
