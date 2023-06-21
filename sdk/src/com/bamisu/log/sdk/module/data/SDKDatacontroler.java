package com.bamisu.log.sdk.module.data;

import com.bamisu.log.sdk.base.SDKConfigHandle;
import com.bamisu.gamelib.base.datacontroller.IDataController;
import com.bamisu.gamelib.base.datacontroller.couchbase.CouchbaseDataController;
import com.bamisu.gamelib.utils.business.CommonHandle;

/**
 * Create by Popeye on 10:34 AM, 4/22/2020
 */
public class SDKDatacontroler {
    public static SDKDatacontroler instance;

    private IDataController dataController;

    public SDKDatacontroler(){

    }

    public static SDKDatacontroler getInstance(){
        if(instance == null){
            instance = new SDKDatacontroler();
        }

        return instance;
    }

    public synchronized IDataController getController(){
        if (dataController == null) {
            try {
                dataController = new CouchbaseDataController(
                        SDKConfigHandle.instance().get("cb_addr"),
                        SDKConfigHandle.instance().get("cb_uname"),
                        SDKConfigHandle.instance().get("cb_uname"),
                        SDKConfigHandle.instance().get("cb_pwd"));
//                dataController = new CouchbaseDataController("http://192.168.127.129:8091/pools", "sdk", "sdk", "13481004");
            } catch (Exception e) {
                CommonHandle.writeErrLog(e);
            }
        }

        return dataController;
    }
}
