package com.bamisu.gamelib.base.model;

import com.bamisu.gamelib.base.DatabaseObject;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.datacontroller.IDataController;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataModel extends DatabaseObject {
    /**
     *
     */
    private static final long serialVersionUID = 7430952960561920254L;

    public DataModel() {
        super();
    }

    public static Map<String, Object> multiGet(List<String> keys, Class c, Zone zone) throws DataControllerException {
        return DatabaseObject.multiget(genKeys(keys, c), zone);
    }

    // regular model set & get
    public void saveModel(String uId, Zone zone) throws DataControllerException {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName()).append(SEPERATOR).append(uId);
        DatabaseObject.set(builder.toString(), this, zone);
    }

    // regular model set & get
    public void saveModel(String uId, IDataController dataController) throws DataControllerException {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName()).append(SEPERATOR).append(uId);
        DatabaseObject.set(builder.toString(), this, dataController);
    }


    public void deleteModel(String uId, Zone zone) throws DataControllerException {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName()).append(SEPERATOR).append(uId);
        DatabaseObject.delete(builder.toString(), zone);
    }

    public void deleteModel(String uId, IDataController dataController) throws DataControllerException {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName()).append(SEPERATOR).append(uId);
        DatabaseObject.delete(builder.toString(), dataController);
    }

    public static Object getModel(String uId, Class c, Zone zone)
            throws DataControllerException {
        StringBuilder builder = new StringBuilder();
        builder.append(c.getSimpleName()).append(SEPERATOR).append(uId);
        Object obj = DatabaseObject.get(builder.toString(), zone);
        return obj != null ? obj.toString() : null;
    }

    public static Object getModel(String uId, Class c, IDataController dataController)
            throws DataControllerException {
        StringBuilder builder = new StringBuilder();
        builder.append(c.getSimpleName()).append(SEPERATOR).append(uId);
        Object obj = DatabaseObject.get(builder.toString(), dataController);
        return obj != null ? obj.toString() : null;
    }

    protected static List<String> genKeys(List<String> ids, Class c) {
        List<String> keys = new ArrayList<>(ids.size());
        StringBuilder builder;
        for (String id : ids) {
            builder = new StringBuilder();
            builder.append(c.getSimpleName()).append(SEPERATOR).append(id);
            keys.add(builder.toString());
        }

        return keys;
    }
}
