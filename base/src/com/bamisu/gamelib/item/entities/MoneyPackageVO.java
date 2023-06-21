package com.bamisu.gamelib.item.entities;

import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.gamelib.item.define.ResourceType;
import com.bamisu.gamelib.entities.ResourcePackage;

public class MoneyPackageVO extends ResourcePackage {

    public MoneyPackageVO(String id, int amount){
        this.amount = amount;
        this.id = id;
    }

    public MoneyPackageVO(MoneyType money, int amount){
        this.amount = amount;
        this.id = money.getId();
    }

    public MoneyPackageVO(){}

    public MoneyPackageVO(ResourcePackage resource){
        if (resource.id.substring(0,3).equals(ResourceType.MONEY.getType())){
            this.amount = resource.amount;
            this.id = resource.id;
        }
    }
}
