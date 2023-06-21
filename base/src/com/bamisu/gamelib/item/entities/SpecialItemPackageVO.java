package com.bamisu.gamelib.item.entities;

import com.bamisu.gamelib.item.define.SpecialItem;
import com.bamisu.gamelib.entities.ResourcePackage;

public class SpecialItemPackageVO {
    public String id;
    public int amount;

    public SpecialItemPackageVO(String id, int amount){
        this.id = id;
        this.amount = amount;
    }

    public SpecialItemPackageVO(){

    }

    public ResourcePackage toResource(){
        ResourcePackage resourcePackage = new ResourcePackage(this.id, this.amount);
        return resourcePackage;
    }

}
