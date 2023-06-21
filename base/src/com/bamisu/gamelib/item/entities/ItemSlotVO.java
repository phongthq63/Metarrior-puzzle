package com.bamisu.gamelib.item.entities;

public class ItemSlotVO implements ISlotVO {
    public int position;
    public boolean status;
    public EquipDataVO equip;

    public ItemSlotVO() {
    }

    public ItemSlotVO(ItemSlotVO itemSlotVO) {
        this.position = itemSlotVO.position;
        this.status = itemSlotVO.status;
        this.equip = itemSlotVO.equip;
    }

    /**
     * Co item
     */
    @Override
    public void lock() {
        status = EStatusSlot.LOCK.getValue();
    }

    /**
     * Khong co item
     */
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
