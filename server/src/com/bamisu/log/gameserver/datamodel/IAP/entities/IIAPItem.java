package com.bamisu.log.gameserver.datamodel.IAP.entities;

import com.smartfoxserver.v2.entities.Zone;

public interface IIAPItem {

    void refresh(Zone zone);
    boolean autoIncreasePoint();
    boolean checkTimeRefresh(Zone zone);
    boolean checkTimeDisapear(Zone zone);
    boolean canBuyMore(Zone zone);
}
