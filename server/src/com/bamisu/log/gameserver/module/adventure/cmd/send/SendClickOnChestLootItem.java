package com.bamisu.log.gameserver.module.adventure.cmd.send;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendClickOnChestLootItem extends BaseMsg {
    public List<ResourcePackage> listResource;
    public int area;
    public int station;
    public int time;
    public int timeMax;
    public SendClickOnChestLootItem() {
        super(CMD.CMD_CLICK_ON_CHEST_LOOT_ITEM);
    }

    public SendClickOnChestLootItem(short errorCode) {
        super(CMD.CMD_CLICK_ON_CHEST_LOOT_ITEM, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        SFSArray sfsArray = new SFSArray();
        if (listResource != null){
            for (ResourcePackage vo: listResource){
                if (vo.amount > 0){
                    sfsArray.addSFSObject(vo.toSFSObject());
                }
            }
        }

        data.putSFSArray(Params.LIST, sfsArray);
        data.putInt(Params.TIME, time);
        data.putInt(Params.TIME_MAX, timeMax);
        data.putInt(Params.AREA, area);
        data.putInt(Params.STATION, station);
    }
}
