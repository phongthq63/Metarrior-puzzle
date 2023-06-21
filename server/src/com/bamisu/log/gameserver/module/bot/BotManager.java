package com.bamisu.log.gameserver.module.bot;

import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.ZoneExtension;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 5:16 PM, 10/23/2020
 */
public class BotManager {
    public Zone zone;
    public ZoneExtension zoneExtension;
    public BotModel botModel;
    public String[] botNames;
    public String botNameVersion = "";

    public BotManager(ZoneExtension zoneExtension) {
        this.zoneExtension = zoneExtension;
        this.zone = zoneExtension.getParentZone();
        loadBotName();
        botModel = BotModel.copyFromDBtoObject(this);
    }

    private void loadBotName() {
        String str = Utils.loadFile(System.getProperty("user.dir") + "/conf/bot/botname.txt");
        botNames = str.split("\r\n");
        botNameVersion = botNames[0];

    }

    public UserModel getOne() {
        return zoneExtension.getUserManager().getUserModel(botModel.ids.get(Utils.randomInRange(0, botModel.ids.size() - 1)));
    }

    public boolean isBot(long uid) {
        return botModel.ids.contains(uid);
    }
}
