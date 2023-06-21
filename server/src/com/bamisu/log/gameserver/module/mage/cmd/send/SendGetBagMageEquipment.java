package com.bamisu.log.gameserver.module.mage.cmd.send;

import com.bamisu.gamelib.item.entities.SageEquipDataVO;
import com.bamisu.log.gameserver.entities.CMDUtilsServer;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.item.entities.SageEquipVO;
import com.bamisu.log.gameserver.module.mage.MageManager;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGetBagMageEquipment extends BaseMsg {

    public List<SageEquipDataVO> listEquip;
    public MageManager mageManager;

    public SendGetBagMageEquipment() {
        super(CMD.CMD_GET_BAG_MAGE_EQUIPMENT);
    }

    public SendGetBagMageEquipment(short errorCode) {
        super(CMD.CMD_GET_BAG_MAGE_EQUIPMENT, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        ISFSArray listPack = new SFSArray();
        ISFSObject pack;
        SageEquipVO equipCf;
        for(SageEquipDataVO index : listEquip){
            pack = new SFSObject();

            equipCf = ItemManager.getInstance().getSageEquipConfig(index.id);
            pack.putUtfString(Params.ModuleMage.HASH, index.hash);
            pack.putUtfString(Params.ID, index.id);
            pack.putShort(Params.STAR, equipCf.star);
            pack.putShort(Params.COUNT, (short) index.count);

            Stats statsItem = mageManager.getStatsItem(index);
            pack.putInt(Params.POWER, mageManager.getPower(statsItem));
            pack.putSFSArray(Params.ModuleHero.ATTRIBUTE,
                    CMDUtilsServer.statsMageToSFSAray(statsItem));

            listPack.addSFSObject(pack);
        }
        data.putSFSArray(Params.LIST, listPack);
    }
}
