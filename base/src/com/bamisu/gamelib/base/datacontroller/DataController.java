package com.bamisu.gamelib.base.datacontroller;

import com.bamisu.gamelib.base.config.ConfigHandle;
import com.bamisu.gamelib.utils.business.CommonHandle;

public class DataController {
    private static IDataController _instance = null;

    public static IDataController getController() {
        synchronized (DataController.class) {
            if (_instance == null) {
                try {
                    _instance = (IDataController) Class.forName("com.bamisu.gamelib.base.datacontroller.couchbase.CouchbaseDataController").newInstance();
                } catch (Exception e) {
                    CommonHandle.writeErrLog(e);
                }
            }
        }

        return _instance;
    }
}
