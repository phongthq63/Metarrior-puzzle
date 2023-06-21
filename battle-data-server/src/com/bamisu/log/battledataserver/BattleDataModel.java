package com.bamisu.log.battledataserver;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;

/**
 * Create by Popeye on 11:23 AM, 12/2/2020
 */
public class BattleDataModel extends DataModel {
    public String battleID;
    public String data;

    public BattleDataModel() {
    }

    public BattleDataModel(String battleID, String data) {
        this.battleID = battleID;
        this.data = data;
    }

    public final boolean saveToDB(BattleDataDataController dataController) {
        try {
            saveModel(battleID, dataController.getController());
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static BattleDataModel copyFromDBtoObject(String uId, BattleDataDataController dataController) {
        BattleDataModel pInfo = null;
        try {
            String str = (String) getModel(uId, BattleDataModel.class, dataController.getController());
            if (str != null) {
                pInfo = Utils.fromJson(str, BattleDataModel.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }
}
