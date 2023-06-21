package com.bamisu.log.gameserver.module.nft.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendBreedHero extends BaseMsg {

    public List<HeroModel> models;

    public SendBreedHero() {
        super(CMD.CMD_BREED);
    }

    public SendBreedHero(short errorCode) {
        super(CMD.CMD_BREED, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) {
            return;
        }

        ISFSArray hash = new SFSArray();
        for (HeroModel model : this.models) {
            ISFSObject obj = new SFSObject();
            obj.putText(Params.HASH, model.hash);
            obj.putLong(Params.TIME, model.timeClaim);
            hash.addSFSObject(obj);
        }

        data.putSFSArray(Params.LIST, hash);
    }
}
