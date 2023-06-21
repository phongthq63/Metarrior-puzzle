package com.bamisu.log.gameserver.module.campaign.config;

import com.bamisu.log.gameserver.module.campaign.config.entities.BattleBackground;
import com.bamisu.log.gameserver.module.campaign.config.entities.BattleBackgroundConfig;
import com.bamisu.log.gameserver.module.characters.element.Element;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 7:26 PM, 6/10/2020
 */
public class BattleBackgroundConfigManager {
    private static BattleBackgroundConfigManager ourInstance = new BattleBackgroundConfigManager();

    public static BattleBackgroundConfigManager getInstance() {
        return ourInstance;
    }

    BattleBackgroundConfig config;
    private BattleBackgroundConfigManager() {
        config = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_BATTLE_BACKGROUND), BattleBackgroundConfig.class);
    }

    public String getBGFromTerrain(String terrain) {
        List<BattleBackground> list = new ArrayList<>();
        for(BattleBackground battleBackground : config.bgList){
            if(battleBackground.terrain.equalsIgnoreCase(terrain)) list.add(battleBackground);
        }

        if(list.isEmpty()){
            return "BBG001";
        }

        return list.get(Utils.randomInRange(0, list.size() - 1)).id;
    }
}
