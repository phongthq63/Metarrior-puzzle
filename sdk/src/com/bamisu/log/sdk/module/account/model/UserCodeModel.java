package com.bamisu.log.sdk.module.account.model;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.datacontroller.IDataController;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;

public class UserCodeModel extends DataModel {
    public String code;
    public String email;

    public UserCodeModel() {

    }

    public UserCodeModel(String email) {
        this.email = email;
        this.code = Utils.ranStr(6, 3);
    }

    public final boolean save(IDataController dataController) {
        try {
            this.saveModel(this.code, dataController);
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        return  false;
    }

    public final void delete(IDataController dataController) {
        try {
            this.deleteModel(this.code, dataController);
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
    }

    public static UserCodeModel load(String code, IDataController dataController) {
        UserCodeModel model = null;
        try {
            String str = (String) getModel(code, UserCodeModel.class, dataController);
            if (str != null) {
                model = Utils.fromJson(str, UserCodeModel.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        return model;
    }
}
