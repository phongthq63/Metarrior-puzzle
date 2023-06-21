package com.bamisu.log.gameserver.module.quest.cmd.send;

import com.bamisu.log.gameserver.datamodel.quest.entities.QuestInfo;
import com.bamisu.log.gameserver.datamodel.quest.entities.TabQuestInfo;
import com.bamisu.log.gameserver.module.quest.defind.EQuestType;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGetTableQuest extends BaseMsg {

    public List<TabQuestInfo> listTab;

    public SendGetTableQuest() {
        super(CMD.CMD_GET_TABLE_QUEST);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        ISFSArray tabPack;
        ISFSObject questPack;
        ISFSObject chestPack;
        for(TabQuestInfo tab : listTab){
            objPack = new SFSObject();
            objPack.putByte(Params.ID, Byte.parseByte(tab.type));
            objPack.putInt(Params.TIME, TimeUtils.getDeltaTimeToTime(ETimeType.fromID(EQuestType.fromID(tab.type).getIdETimeRefresh()), tab.timeStamp));

            tabPack = new SFSArray();
            for(QuestInfo quest : tab.quests){
                questPack = new SFSObject();
                questPack.putUtfString(Params.ID, quest.id);
                questPack.putByte(Params.COMPLETE, quest.complete);
                questPack.putInt(Params.COUNT, quest.point);

                tabPack.addSFSObject(questPack);
            }
            objPack.putSFSArray(Params.LIST, tabPack);

            switch (EQuestType.fromID(tab.type)){
                case DAILY:
                case WEEKLY:
                    chestPack = new SFSObject();
                    chestPack.putInt(Params.POINT, tab.chest.point);
                    chestPack.putUtfStringArray(Params.COMPLETE, tab.chest.complete);
                    objPack.putSFSObject(Params.CHEST, chestPack);
                    break;
            }

            arrayPack.addSFSObject(objPack);
        }

        data.putSFSArray(Params.TABLE, arrayPack);
    }
}
