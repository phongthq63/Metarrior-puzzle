package com.bamisu.gamelib.base;

import com.smartfoxserver.v2.entities.Zone;

/**
 * Create by Popeye on 4:39 PM, 10/23/2019
 */
public abstract class ZoneComponent {
    private Zone zone;

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Zone getZone() {
        return zone;
    }
}
