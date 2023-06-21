package com.bamisu.gamelib.base.datacontroller;

import com.bamisu.gamelib.base.datacontroller.couchbase.CouchbaseDataController;
import com.bamisu.gamelib.base.config.ConfigHandle;
import com.bamisu.gamelib.base.datacontroller.IDataController;
import com.bamisu.gamelib.base.datacontroller.couchbase.CouchbaseDataController;
import com.bamisu.gamelib.utils.business.CommonHandle;
import com.smartfoxserver.v2.entities.Zone;

/**
 * Create by Popeye on 9:49 AM, 10/23/2019
 */
public class ZoneDatacontroler {
    private Zone zone;
    private IDataController dataController;

    public ZoneDatacontroler(Zone zone) {
        this.zone = zone;
    }

    public synchronized IDataController getController(){
        if (dataController == null) {
            try {
                dataController = new CouchbaseDataController(zone);
            } catch (Exception e) {
                CommonHandle.writeErrLog(e);
            }
        }

        return dataController;
    }
}
