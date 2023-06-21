package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.item.entities.FragmentVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGetListFragmentHero extends BaseMsg {

    public List<FragmentVO> listFragment;


    public SendGetListFragmentHero() {
        super(CMD.CMD_GET_LIST_FRAGMENT_HERO);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;

        SFSArray arrayPack = new SFSArray();
        SFSObject objectPack;
        for (FragmentVO vo: listFragment){
            objectPack = new SFSObject();

            objectPack.putUtfString(Params.ID, vo.id);
            objectPack.putInt(Params.AMOUNT, vo.amount);
            arrayPack.addSFSObject(objectPack);
        }
        data.putSFSArray(Params.LIST, arrayPack);
    }
}
