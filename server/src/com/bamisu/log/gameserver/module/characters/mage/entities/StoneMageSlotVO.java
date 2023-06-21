package com.bamisu.log.gameserver.module.characters.mage.entities;

import com.bamisu.log.gameserver.datamodel.mage.entities.StoneMageInfo;
import com.bamisu.gamelib.item.entities.EStatusSlot;
import com.bamisu.gamelib.item.entities.ISlotVO;

public class StoneMageSlotVO implements ISlotVO {
    public boolean status;
    public StoneMageInfo stoneMageModel;

    @Override
    public void lock(){
        status = EStatusSlot.LOCK.getValue();
    }
    @Override
    public void unlock(){
        status = EStatusSlot.UNLOCK.getValue();
    }

    @Override
    public boolean haveLock(){
        if(status == EStatusSlot.LOCK.getValue()){
            return status;
        }
        return EStatusSlot.UNLOCK.getValue();
    }
}
