package com.bamisu.gamelib.utils.management;

import com.bamisu.gamelib.utils.management.interface_.IExtensionZoneManager;
import com.bamisu.gamelib.utils.management.interface_.IExtensionZoneManager;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.managers.IZoneManager;

/**
 * Created by Popeye on 6/28/2017.
 */
public class ExtensionZoneManager implements IExtensionZoneManager {
    private static ExtensionZoneManager ourInstance = new ExtensionZoneManager();

    public static ExtensionZoneManager getInstance() {
        return ourInstance;
    }

    private IZoneManager zoneManager;
    private ExtensionZoneManager() {
        zoneManager = SmartFoxServer.getInstance().getZoneManager();
    }

    @Override
    public Zone getZoneByName(String zoneName) {
        return zoneManager.getZoneByName(zoneName);
    }
}
