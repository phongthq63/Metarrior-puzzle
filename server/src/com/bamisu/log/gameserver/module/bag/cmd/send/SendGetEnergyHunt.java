package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.datamodel.bag.UserBagModel;
import com.bamisu.log.gameserver.datamodel.bag.entities.EnergyChargeInfo;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.bag.config.EnergyConfig;
import com.smartfoxserver.v2.entities.Zone;

public class SendGetEnergyHunt extends BaseMsg {
    public UserBagModel userBagModel;
    public EnergyChargeInfo energyInfo;
    public Zone zone;

    public SendGetEnergyHunt() {
        super(CMD.CMD_GET_HUNT_ENERGY_BAR);
    }

    @Override
    public void packData() {
        super.packData();

        EnergyConfig energyConfig = BagManager.getInstance().getEnergyHuntConfig();
        data.putInt(Params.MAX, energyConfig.max);
        data.putInt(Params.POINT, energyInfo.point);
        data.putInt(Params.ODD, energyInfo.timeOdd);
        data.putInt(Params.TIME_RESET, energyConfig.increaseTime);
        data.putInt(Params.INCREASE, energyInfo.increase);
        data.putUtfStringArray(Params.HERO, energyInfo.heros);
    }
}
