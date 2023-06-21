package com.bamisu.log.sdk.telegrambot;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.datacontroller.IDataController;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;

/**
 * Create by Popeye on 9:35 AM, 12/15/2020
 */
public class BotTokenModel extends DataModel {
    public static final String key = "0";
    public int token = 0;

    public BotTokenModel() {
    }

    public BotTokenModel(int token) {
        this.token = token;
    }

    public final boolean saveToDB(IDataController dataController) {
        try {
            this.saveModel(key, dataController);
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static BotTokenModel copyFromDBtoObject(IDataController dataController) {
        BotTokenModel model = null;
        try {
            String str = (String) getModel(key, BotTokenModel.class, dataController);
            if (str != null) {
                model = Utils.fromJson(str, BotTokenModel.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if(model == null){
            model = new BotTokenModel();
            model.saveToDB(dataController);
        }
        return model;
    }
}
