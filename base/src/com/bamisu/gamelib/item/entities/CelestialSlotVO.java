package com.bamisu.gamelib.item.entities;

public class CelestialSlotVO implements ISlotVO {
    public int position;
    public boolean status;
    public CelestialEquipDataVO equip;

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
