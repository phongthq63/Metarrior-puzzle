package com.bamisu.log.sdk.module.account.model;

import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.datacontroller.couchbase.CouchbaseDataController;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;

/**
 * Create by Popeye on 10:08 AM, 9/7/2020
 */
public class AccountCountModel extends DataModel {
    public long incID;
    public String uid;

    public AccountCountModel() {
    }

    public AccountCountModel(long incID, String uid) {
        this.incID = incID;
        this.uid = uid;
    }

    public boolean save(SDKDatacontroler sdkDatacontroler) {
        try {
            this.saveModel(String.valueOf(incID), sdkDatacontroler.getController());
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static AccountCountModel copyFromDB(long incID, SDKDatacontroler sdkDatacontroler) {
        AccountCountModel model = null;
        try {
            String str = (String) getModel(String.valueOf(incID), AccountCountModel.class, sdkDatacontroler.getController());
            if (str != null) {
                model = Utils.fromJson(str, AccountCountModel.class);
                if (model != null) {
//                    model.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return model;
    }

    public synchronized static AccountCountModel create(String uid, SDKDatacontroler sdkDatacontroler) {
        long incID = ((CouchbaseDataController) sdkDatacontroler.getController()).getClient().incr("ACCOUNT_ID_COUNT", 1, 1L);
        AccountCountModel acountModel = new AccountCountModel(incID, uid);
        if (acountModel.save(sdkDatacontroler)) {
            return acountModel;
        }
        return null;
    }
}
