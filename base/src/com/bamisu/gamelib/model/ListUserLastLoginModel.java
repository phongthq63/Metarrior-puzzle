package com.bamisu.gamelib.model;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 6:30 PM, 9/14/2020
 */
public class ListUserLastLoginModel extends DataModel {
    public List<Long> ids = new ArrayList<>();

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel("0", zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static final ListUserLastLoginModel copyFromDBtoObject(Zone zone) {
        ListUserLastLoginModel model = null;
        try {
            String str = (String) DataModel.getModel("", ListUserLastLoginModel.class, zone);
            if (str != null) {
                model = Utils.fromJson(str, ListUserLastLoginModel.class);
                if (model != null) {
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if(model == null){
            model = new ListUserLastLoginModel();
            model.saveToDB(zone);
        }
        return model;
    }
}
