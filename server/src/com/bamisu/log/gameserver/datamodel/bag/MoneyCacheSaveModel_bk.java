package com.bamisu.log.gameserver.datamodel.bag;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.sql.game.dbo.MoneyChangeDBO;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class MoneyCacheSaveModel_bk extends DataModel {
    private final static long id = 0;

    public List<MoneyChangeDBO> cache = new ArrayList<>();
    private final Object lockCache = new Object();



    public static MoneyCacheSaveModel_bk createMoneyCacheSaveModel_bk(Zone zone){
        MoneyCacheSaveModel_bk MoneyCacheSaveModel_bk = new MoneyCacheSaveModel_bk();
        MoneyCacheSaveModel_bk.saveToDB(zone);

        return MoneyCacheSaveModel_bk;
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

    public static MoneyCacheSaveModel_bk copyFromDBtoObject(Zone zone) {
        MoneyCacheSaveModel_bk pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(id), MoneyCacheSaveModel_bk.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, MoneyCacheSaveModel_bk.class);
                if (pInfo != null) {
//                    pInfo.wrNiteLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        if(pInfo == null){
            pInfo = createMoneyCacheSaveModel_bk(zone);
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

    public boolean addCache(MoneyChangeDBO moneyChangeDBO, Zone zone){
        synchronized (lockCache){
            cache.add(moneyChangeDBO);
            return saveToDB(zone);
        }
    }

    public List<MoneyChangeDBO> readCache(){
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
