package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.datamodel.bag.UserBagModel;
import com.bamisu.log.gameserver.datamodel.bag.entities.EnergyChargeInfo;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.bag.config.EnergyConfig;
import com.smartfoxserver.v2.entities.Zone;

/**
 * Created by Quach Thanh Phong
 * On 12/6/2022 - 11:06 PM
 */
public class SendUpdateEnergy extends BaseMsg {

    public UserBagModel userBagModel;
    public EnergyChargeInfo energyInfo;
    public Zone zone;

    public SendUpdateEnergy() {
        super(CMD.CMD_UPDATE_ENERGY_BAR);
    }

    public SendUpdateEnergy(short errorCode) {
        super(CMD.CMD_UPDATE_ENERGY_BAR, errorCode);
    }

    @Override
    public void packData() {
        super.packData();

        EnergyConfig energyConfig = BagManager.getInstance().getEnergyConfig();
        data.putInt(Params.MAX, energyConfig.max);
        data.putInt(Params.POINT, energyInfo.point);
        data.putInt(Params.ODD, energyInfo.timeOdd);
        data.putInt(Params.TIME_RESET, energyConfig.increaseTime);
        data.putInt(Params.INCREASE, energyInfo.increase);
        data.putUtfStringArray(Params.HERO, energyInfo.heros);

//        ISFSArray arrayPack = new SFSArray();
//        ISFSObject objPack;
//        for(EnergyChangeVO cf : BagManager.getInstance().getEnergyConfig().up){
//            objPack = new SFSObject();
//
//            objPack.putUtfString(Params.ID, cf.id);
//            objPack.putInt(Params.COUNT, cf.max - userBagModel.readCountChargeUse(cf.id, zone));
//
//            arrayPack.addSFSObject(objPack);
//        }
//        data.putSFSArray(Params.ENERGY, arrayPack);
    }
}
