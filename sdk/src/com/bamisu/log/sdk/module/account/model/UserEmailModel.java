package com.bamisu.log.sdk.module.account.model;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.datacontroller.IDataController;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;

public class UserEmailModel extends DataModel {

    public String accountID;
    public String username;
    public String email;
    public boolean isVerified;

    public UserEmailModel() {

    }

    public UserEmailModel(String accountID, String username, String email) {
        this.accountID = accountID;
        this.username = username;
        this.email = email;
        this.isVerified = true;
    }

    public final boolean save(IDataController dataController) {
        try {
            this.saveModel(this.email, dataController);
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static UserEmailModel load(String email, IDataController dataController) {
        UserEmailModel model = null;
        try {
            String str = (String) getModel(email, UserEmailModel.class, dataController);
            if (str != null) {
                model = Utils.fromJson(str, UserEmailModel.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        return model;
    }
}
