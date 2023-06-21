package com.bamisu.log.battledataserver;

import com.bamisu.gamelib.base.config.ConfigHandle;
import com.bamisu.gamelib.base.datacontroller.IDataController;
import com.bamisu.gamelib.base.datacontroller.couchbase.CouchbaseDataController;
import com.bamisu.gamelib.utils.business.CommonHandle;

/**
 * Create by Popeye on 11:15 AM, 12/2/2020
 */
public class BattleDataDataController {
    public static BattleDataDataController instance;

    private IDataController dataController;

    public BattleDataDataController() {

    }

    public static BattleDataDataController getInstance() {
        if (instance == null) {
            instance = new BattleDataDataController();
        }

        return instance;
    }

    public synchronized IDataController getController() {
        if (dataController == null) {
            try {
                dataController = new CouchbaseDataController(
                        ConfigHandle.instance().get("bds_cb_addr"),
                        ConfigHandle.instance().get("bds_cb_uname"),
                        ConfigHandle.instance().get("bds_cb_uname"),
                        ConfigHandle.instance().get("bds_cb_pwd"));
//                dataController = new CouchbaseDataController("http://192.168.127.129:8091/pools", "sdk", "sdk", "13481004");
            } catch (Exception e) {
                CommonHandle.writeErrLog(e);
            }
        }

        return dataController;
    }
}
