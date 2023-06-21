package com.bamisu.gamelib.item.entities;

public class StoneSlotVO implements ISlotVO {
    public int position;
    public boolean status;
    public StoneVO stoneVO;

    public static StoneSlotVO create(StoneSlotVO stoneSlotVO){
        StoneSlotVO slot = new StoneSlotVO();
        slot.status = stoneSlotVO.status;
        slot.stoneVO = StoneVO.create(stoneSlotVO.stoneVO);
        slot.position = stoneSlotVO.position;

        return slot;
    }


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
