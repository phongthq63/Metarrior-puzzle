package com.bamisu.log.gameserver.module.bag.defind;

import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.EVip;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.module.bag.BagHandler;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.smartfoxserver.v2.entities.Zone;

import java.util.Arrays;
import java.util.List;

public enum  EEnergyChargeType {
    FREE("free", null),
    VIP1_VIP2("0", Arrays.asList(EVip.ARCHMAGE.getId(), EVip.PROTECTOR.getId())),
    VIP("1", Arrays.asList(EVip.PROTECTOR.getId()));

    String id;
    List<Integer> condition;

    EEnergyChargeType(String id, List<Integer> condition) {
        this.id = id;
        this.condition = condition;
    }

    public static EEnergyChargeType fromID(String id){
        for(EEnergyChargeType index : EEnergyChargeType.values()){
            if(index.id.equals(id)){
                return index;
            }
        }
        return null;
    }

    public boolean haveSatifyCondition(long uid, Zone zone){
        if(condition == null || condition.isEmpty()) return true;

        UserModel userModel = ((BagHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_BAG)).getUserModel(uid);
        for(int con : condition){
            if(VipManager.getInstance().haveVip(userModel, EVip.fromIntValue(con))) return true;
        }
        return false;
    }
}
