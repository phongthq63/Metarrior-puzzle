package com.bamisu.log.gameserver.module.characters.blessing;

import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.log.gameserver.module.characters.blessing.entities.UnlockBlessingVO;
import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnlockBlessingConfig {
    public short initOpen;
    public List<UnlockBlessingVO> unlock;

    public List<ResourcePackage> readCostUnlockBlessingConfig(MoneyType moneyType, int count){
        for(UnlockBlessingVO cf : unlock){
            if(moneyType.getId().equals(cf.id) && cf.cost.containsKey((short)count)){
                return new ArrayList<>(Collections.singleton(new ResourcePackage(cf.id, -cf.cost.get((short)count))));
            }
        }
        return new ArrayList<>();
    }
}
