package com.bamisu.log.sdk.module.sql;

import com.bamisu.gamelib.base.db.SQLController;

/**
 * Create by Popeye on 4:25 PM, 9/10/2020
 */
public class SDKsqlManager {
    private static SDKsqlManager ourInstance = new SDKsqlManager();

    public static SDKsqlManager getInstance() {
        return ourInstance;
    }

    private SQLController sqlController;

    private SDKsqlManager() {
        sqlController = new SQLController("sdk");
    }

    public SQLController getSqlController() {
        return sqlController;
    }
}
