package com.bamisu.gamelib.entities;

import com.bamisu.gamelib.item.entities.IResourcePackage;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class ResourcePackage implements IResourcePackage { //Resource
    public String id;
    public int amount;

    public ResourcePackage(){}

    public ResourcePackage(String id, int amount){
        this.id = id;
        this.amount = amount;
    }

    public ResourcePackage(ResourcePackage resource) {
        this.amount = resource.amount;
        this.id = resource.id;
    }

    public String readType(){
        return id.substring(0, 3);
    }

    public ISFSObject toSFSObject(){
//        if (amount != 0){
            SFSObject sfsObject = new SFSObject();
            sfsObject.putUtfString(Params.ID, id);
            sfsObject.putLong(Params.AMOUNT, amount);
            return sfsObject;
//        }
    }

    public ISFSObject toSFSObjectGem(){
        String[] separate = this.id.split("_");
        SFSObject sfsObject = new SFSObject();
        sfsObject.putUtfString(Params.ID, separate[0]);
        sfsObject.putLong(Params.AMOUNT, amount);
        sfsObject.putInt(Params.LEVEL, Integer.parseInt(separate[1]));
        return sfsObject;
    }

    public ResourcePackage cloneNew() {
        return new ResourcePackage(this.id, this.amount);
    }

    @Override
    public String readId() {
        return id;
    }

    @Override
    public String readHash() {
        return null;
    }

    @Override
    public int readAmount() {
        return amount;
    }
}
