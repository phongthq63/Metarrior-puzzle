package com.bamisu.log.gameserver.module.bot;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.model.DisplayNameModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.ValidateUtils;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.base.Authenticator;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 4:53 PM, 10/23/2020
 */
public class BotModel extends DataModel {
    private static final String ID = "4";
    public String botnameVersion = "v0.9";
    public List<Long> ids = new ArrayList<>();

    public BotModel() {

    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(ID, zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static BotModel copyFromDBtoObject(BotManager botManager) {
        BotModel model = null;
        try {
            String str = (String) getModel(ID, BotModel.class, botManager.zone);
            if (str != null) {
                model = Utils.fromJson(str, BotModel.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if (model == null) {
            model = new BotModel();
            model.saveToDB(botManager.zone);
        }

        // khởi tạo bot nếu khác version với file config
        if (!model.botnameVersion.equalsIgnoreCase(botManager.botNameVersion)) {
            for (int i = 1; i < botManager.botNames.length; i++) {
                try {
                    String name = botManager.botNames[i];
                    if(!ValidateUtils.isDisplayName(name)) continue;
                    if (DisplayNameModel.copyFromDBtoObject(name, botManager.zone) == null) {
                        UserModel um = Authenticator.createUser("bot_" + name + "_" + Utils.ranStr(5), botManager.zone, ((ZoneExtension) botManager.zone.getExtension()).getServerID());
                        um.displayName = name;
                        DisplayNameModel.create(um, botManager.zone);
                        um.type = 1;
                        um.saveToDB(botManager.zone);
                        model.ids.add(um.userID);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return model;
                }
            }
            model.botnameVersion = botManager.botNameVersion;
            model.saveToDB(botManager.zone);
        }

        return model;
    }
}
