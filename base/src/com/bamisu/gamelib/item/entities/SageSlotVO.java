package com.bamisu.gamelib.item.entities;

public class SageSlotVO implements ISlotVO {
    public int position;
    public boolean status;
    public SageEquipDataVO equip;

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
