package com.bamisu.log.sdk;

import com.bamisu.log.gameserver.ZoneHandle;
import com.bamisu.log.gameserver.ZoneHandleInternalMessage;
import com.bamisu.log.gameserver.base.OnServerReadyHandler;
import com.bamisu.log.gameserver.module.campaign.CampaignHandler;
import com.bamisu.log.gameserver.module.adventure.AdventureHandler;
import com.bamisu.log.gameserver.module.bag.BagHandler;
import com.bamisu.log.gameserver.module.campaign.config.MainCampaignConfig;
import com.bamisu.log.gameserver.module.celestial.CelestialHandler;
import com.bamisu.log.gameserver.module.characters.CharacterHandler;
import com.bamisu.log.gameserver.module.guild.GuildHandler;
import com.bamisu.log.gameserver.module.hero.HeroHandler;
import com.bamisu.log.gameserver.module.item.ItemHandler;
import com.bamisu.log.gameserver.module.mage.MageHandler;
import com.bamisu.log.gameserver.module.mission.MissionHandler;
import com.bamisu.log.gameserver.module.user.UserHandler;
import com.bamisu.gamelib.ExtensionHandleInternalMessage;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.core.SFSEventType;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Create by Popeye on 4:39 PM, 4/21/2020
 */
public class SDKExtension extends BaseExtension {
    ExtensionHandleInternalMessage handleInternalMessage = new ZoneHandleInternalMessage(this);

    @Override
    public void init() {
        trace(" extension init!!!!");
        addEventHandler(SFSEventType.SERVER_READY, OnServerReadyHandler.class);
    }

    @Override
    public void onServerReady() {
        trace("onServerReady");
        initLogger();
        initConfig();
        initDB();
        initLogic();
        initModule();
        initTest();
        MainCampaignConfig.getInstance();
    }

    private void initTest() {
    }

    @Override
    public void initLogger() {
        trace("initLogger");
        Logger.getLogger("heroLog").info("initLogger");
    }

    @Override
    public void initConfig() {
        trace("initConfig");
    }

    @Override
    public void initDB() {
        trace("initDB");

        // couchbase
        try {
            getDataController().getController().set(getParentZone().getName() + "_initDB", String.valueOf(Utils.getTimestampInSecond()));
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        //ping to couchbase
        getApi().getNewScheduler(1).scheduleAtFixedRate(() -> {
            try {
                getDataController().getController().set(getParentZone().getName() + "_ping", Utils.getTimestampInSecond());
            } catch (DataControllerException e) {
                e.printStackTrace();
            }
        }, 5, 30, TimeUnit.SECONDS);
    }

    @Override
    public void initLogic() {
        trace("initLogic");
    }

    @Override
    public void initModule() {
        getUserManager();
        new UserHandler(this);
        new CharacterHandler(this);
        new ItemHandler(this);
        new HeroHandler(this);
        new BagHandler(this);
        new CampaignHandler(this);
        new MageHandler(this);
        new AdventureHandler(this);
        new GuildHandler(this);
        new CelestialHandler(this);
        new MissionHandler(this);

        new ZoneHandle(this);
    }

    @Override
    public Object handleInternalMessage(String cmdName, Object params) {
        return handleInternalMessage.handleInternalMessage(cmdName, params);
    }
}
