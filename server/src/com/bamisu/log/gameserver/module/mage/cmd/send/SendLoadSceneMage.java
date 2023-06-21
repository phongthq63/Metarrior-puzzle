package com.bamisu.log.gameserver.module.mage.cmd.send;

import com.bamisu.log.gameserver.datamodel.mage.UserMageModel;
import com.bamisu.log.gameserver.entities.CMDUtilsServer;
import com.bamisu.log.gameserver.module.characters.mage.entities.StoneMageSlotVO;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.item.entities.SageEquipVO;
import com.bamisu.gamelib.item.entities.SageSlotVO;
import com.bamisu.log.gameserver.module.mage.MageManager;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class SendLoadSceneMage extends BaseMsg {

    public UserMageModel userMageModel;
    public Stats stats;
    public Zone zone;

    public SendLoadSceneMage() {
        super(CMD.CMD_LOAD_SCENE_MAGE);
    }

    public SendLoadSceneMage(short errorCode) {
        super(CMD.CMD_LOAD_SCENE_MAGE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putUtfString(Params.ModuleMage.SKIN, userMageModel.readSkin());
        data.putShort(Params.ModuleChracter.LEVEL, userMageModel.readLevel(zone));
        data.putLong(Params.ModuleChracter.EXP, userMageModel.readExp(zone));
        data.putSFSObject(Params.ModuleHero.ATTRIBUTE, CMDUtilsServer.statsMageToSFSObject(stats));

        ISFSObject stoneMageInfo = new SFSObject();
        stoneMageInfo.putUtfString(Params.ModuleMage.USE, userMageModel.readStoneUse());
        ISFSArray listStone = new SFSArray();
        ISFSObject stone;
        for(StoneMageSlotVO slot : userMageModel.stoneSlot){
            if(!slot.haveLock()){
                stone = new SFSObject();
                stone.putUtfString(Params.ID, slot.stoneMageModel.id);
                stone.putShort(Params.ModuleChracter.LEVEL, slot.stoneMageModel.level);
                stone.putLong(Params.ModuleChracter.EXP, slot.stoneMageModel.exp);
                stone.putSFSArray(Params.ModuleHero.ATTRIBUTE,
                        CMDUtilsServer.statsMageToSFSAray(
                                MageManager.getInstance().getStatsItem(slot.stoneMageModel)));

                listStone.addSFSObject(stone);
            }
        }
        stoneMageInfo.putSFSArray(Params.LIST, listStone);
        data.putSFSObject(Params.ModuleMage.STONE, stoneMageInfo);

        ISFSArray equipment = new SFSArray();
        ISFSObject item;
        SageEquipVO equipCf;
        for(SageSlotVO itemSlotVO : userMageModel.equipment){
            if (itemSlotVO.equip != null && itemSlotVO.status != false){
                item = new SFSObject();

                equipCf = ItemManager.getInstance().getSageEquipConfig(itemSlotVO.equip.id);
                item.putUtfString(Params.ModuleBag.HASH, itemSlotVO.equip.hash);
                item.putShort(Params.STAR, equipCf.star);
                item.putUtfString(Params.ID, itemSlotVO.equip.id);

                Stats statsItem = MageManager.getInstance().getStatsItem(itemSlotVO.equip);
                item.putInt(Params.POWER, MageManager.getInstance().getPower(statsItem));
                item.putSFSArray(Params.ModuleHero.ATTRIBUTE,
                        CMDUtilsServer.statsMageToSFSAray(statsItem));

                equipment.addSFSObject(item);
            }
        }
        data.putSFSArray(Params.ModuleHero.EQUIPMENT, equipment);
    }
}
