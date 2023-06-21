package com.bamisu.log.gameserver.datamodel.IAP;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.sql.game.dbo.IAPPackageDBO;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class IAPCacheSaveModel extends DataModel {
    private final static long id = 0;

    public List<IAPPackageDBO> cache = new ArrayList<>();
    private final Object lockCache = new Object();



    public static IAPCacheSaveModel createIAPCacheSaveModel(Zone zone){
        IAPCacheSaveModel moneyCacheSaveModel = new IAPCacheSaveModel();
        moneyCacheSaveModel.saveToDB(zone);

        return moneyCacheSaveModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.id), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static IAPCacheSaveModel copyFromDBtoObject(Zone zone) {
        IAPCacheSaveModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(id), IAPCacheSaveModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, IAPCacheSaveModel.class);
                if (pInfo != null) {
//                    pInfo.wrNiteLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        if(pInfo == null){
            pInfo = createIAPCacheSaveModel(zone);
        }
        return pInfo;
    }



    /*-----------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------------*/
    public boolean haveCache(){
        synchronized (lockCache){
            return !cache.isEmpty();
        }
    }

    public boolean addCache(IAPPackageDBO iapPackageDBO, Zone zone){
        synchronized (lockCache){
            cache.add(iapPackageDBO);
            return saveToDB(zone);
        }
    }

    public List<IAPPackageDBO> readCache(){
        synchronized (lockCache){
            return cache;
        }
    }

    public boolean clearCache(Zone zone){
        synchronized (lockCache){
            cache.clear();
            return saveToDB(zone);
        }
    }
}
