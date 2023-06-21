package com.bamisu.log.gameserver.module.invite.config.entities;

import com.bamisu.log.gameserver.module.IAPBuy.config.entities.FormulaConditionPropertiesVO;
import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.ArrayList;
import java.util.List;

public class InviteRewardVO {
    public String id;
    public List<List<ResourcePackage>> list;
    public short limit;
    public FormulaConditionPropertiesVO formula;

    public List<ResourcePackage> getRewardBonusInvite(int index){
        if(limit > 0 && index > limit)return new ArrayList<>();
        if(limit >= index)return list.get(index);
        if(formula == null) return list.get(list.size() - 1);
        if(list.size() - 1 >= index){
            return list.get(index);
        }else {
            //index > max index
            // max index - fer loop + (index - max index) % fer loop
            return list.get((list.size() - 1) - formula.f + (index - (list.size() - 1)) % formula.f);
        }
    }
}
