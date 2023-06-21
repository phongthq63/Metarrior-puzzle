package com.bamisu.log.gameserver.module.characters.hero.entities;

import com.bamisu.gamelib.item.entities.EStatusSlot;
import com.bamisu.gamelib.item.entities.ISlotVO;
import com.bamisu.log.gameserver.module.hero.entities.HeroPosition;

public class HeroSlotVO implements ISlotVO {
    public int position;
    public boolean status;
    public String hashHero = "";

    public HeroSlotVO() {
    }

    public HeroSlotVO(HeroSlotVO heroSlotCf) {
        this.position = heroSlotCf.position;
        this.status = heroSlotCf.status;
        this.hashHero = heroSlotCf.hashHero;
    }

    public HeroSlotVO(HeroPosition heroPosition) {
        this.position = heroPosition.position;
        this.hashHero = heroPosition.hash;
        if(heroPosition.hash == null){
            this.unlock();
        }else {
            this.lock();
        }
    }

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
