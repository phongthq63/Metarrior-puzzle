package com.bamisu.gamelib.base;

import com.bamisu.gamelib.base.datacontroller.DataController;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.datacontroller.IDataController;
import com.bamisu.gamelib.base.datacontroller.ZoneDatacontroler;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.business.Debug;
import com.smartfoxserver.v2.entities.Zone;
import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DatabaseObject implements Serializable {
    /**
     *
     */
    public static final long serialVersionUID = -8051864677112093909L;
    public static final String SEPERATOR = "_";
    public static final Integer DEFAULT_CACHE_EXPIRED = 86400;//1day

    public DatabaseObject() {
        super();
    }

    public String toJson() {
        return Utils.toJson(this);
    }

    public static int count = 0;
    public static final void set(String key, DatabaseObject dbObjcet, Zone zone)
            throws DataControllerException {
        String json = dbObjcet.toJson();
        Debug.traceDB("DatabaseObject set: " + Utils.byteToKByte(json.getBytes().length) + " " + key + "|" + json);

        ((BaseExtension)zone.getExtension()).getDataController().getController().set(key, json);
    }


    public static final void set(String key, DatabaseObject dbObjcet, IDataController dataController)
            throws DataControllerException {
        String json = dbObjcet.toJson();
        Debug.traceDB("DatabaseObject set: " + Utils.byteToKByte(json.getBytes().length) + " " + key + "|" + json);

        dataController.set(key, json);
    }

    public static final void set(String key, DatabaseObject dbObjcet, int expiredTime, Zone zone)
            throws DataControllerException {
        String json = dbObjcet.toJson();
        Debug.traceDB("DatabaseObject set: " + Utils.byteToKByte(json.getBytes().length) + " " + key + "|" + json);

        ((BaseExtension)zone.getExtension()).getDataController().getController().set(key, expiredTime, json);

    }

    public static final void setCache(String key, DatabaseObject dbObject, Zone zone)
            throws DataControllerException {
        ((BaseExtension)zone.getExtension()).getDataController().getController().setCache(key, DEFAULT_CACHE_EXPIRED,
                dbObject.toJson());
    }

    public static final void setCache(String key, int expire,
                                      DatabaseObject dbObject, Zone zone) throws DataControllerException {
        ((BaseExtension)zone.getExtension()).getDataController().getController().setCache(key, expire, dbObject.toJson());
    }

    public static final void delete(String key, Zone zone) throws DataControllerException {
        ((BaseExtension)zone.getExtension()).getDataController().getController().delete(key);

    }

    public static final void delete(String key, IDataController dataController) throws DataControllerException {
        dataController.delete(key);
    }

    public static final void deleteCache(String key, Zone zone)
            throws DataControllerException {
        ((BaseExtension)zone.getExtension()).getDataController().getController().deleteCache(key);
    }

    public static final Object get(String key, Zone zone) throws DataControllerException {
//        Debug.trace("get:" + key);
        return ((BaseExtension)zone.getExtension()).getDataController().getController().get(key);
    }

    public static final Object get(String key, IDataController dataController) throws DataControllerException {
//        Debug.trace("get:" + key);
        return dataController.get(key);
    }

    public static final long getCASValue(String name, Zone zone)
            throws DataControllerException {
        return ((BaseExtension)zone.getExtension()).getDataController().getController().getCASValue(name);
    }

    // minhvv test getCAS
    public static final CASValue getS(String name, Zone zone)
            throws DataControllerException {
        return ((BaseExtension)zone.getExtension()).getDataController().getController().getS(name);
    }

    public static final CASResponse checkAndSet(String name, long casValue,
                                                DatabaseObject data, Zone zone) throws DataControllerException {
        return ((BaseExtension)zone.getExtension()).getDataController().getController().checkAndSet(name, casValue, data);
    }

    public static final Map<String, Object> multiget(List<String> keys, Zone zone)
            throws DataControllerException {
        return ((BaseExtension)zone.getExtension()).getDataController().getController().multiget(keys);
    }

    public static final Object getCache(String key, Zone zone)
            throws DataControllerException {
        return ((BaseExtension)zone.getExtension()).getDataController().getController().getCache(key);
    }
}
