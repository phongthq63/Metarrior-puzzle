package com.bamisu.log.gameserver.module.darkgate.config;

import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;

/**
 * Create by Popeye on 10:29 AM, 11/12/2020
 */
public class DarkGateConfigManager {
    private static DarkGateConfigManager ourInstance = new DarkGateConfigManager();

    public static DarkGateConfigManager getInstance() {
        return ourInstance;
    }

    private DarkGateConfigMainVO darkGateConfigMainVO;
    private DarkGateRewardsConfigVO darkGateRewardsConfigVO;

    private DarkGateConfigManager() {
        darkGateConfigMainVO = Utils.fromJson(Utils.loadConfig(ServerConstant.DarkGate.DART_GATE_MAIN), DarkGateConfigMainVO.class);
        darkGateRewardsConfigVO = Utils.fromJson(Utils.loadConfig(ServerConstant.DarkGate.DART_GATE_REWARDS), DarkGateRewardsConfigVO.class);
    }

    public DarkGateConfigMainVO getDarkGateConfigMainVO() {
        return darkGateConfigMainVO;
    }

    public DarkGateRewardsConfigVO getDarkGateRewardsConfigVO() {
        return darkGateRewardsConfigVO;
    }
}
