package com.bamisu.log.gameserver.module.campaign.config.entities;

import com.bamisu.gamelib.item.entities.EStatusSlot;
import com.bamisu.gamelib.item.entities.ISlotVO;
import com.bamisu.gamelib.entities.ResourcePackage;

public class StoreCampaignSlotVO implements ISlotVO {

    public int position;
    public boolean status;
    public ResourcePackage reward;

    @Override
    public void lock() {
        status = EStatusSlot.LOCK.getValue();
    }

    @Override
    public void unlock() {
        status = EStatusSlot.UNLOCK.getValue();
    }

    @Override
    public boolean haveLock() {
        if(status == EStatusSlot.LOCK.getValue()){
            return status;
        }
        return EStatusSlot.UNLOCK.getValue();
    }
}
