package com.bamisu.log.gameserver.module.mage.cmd.send;

import com.bamisu.log.gameserver.datamodel.mage.SageSkillModel;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSArray;

/**
 * Create by Popeye on 5:43 PM, 3/6/2020
 */
public class SendSkillTree extends BaseMsg {
    public SageSkillModel sageSkillModel;
    public int maxPoint;

    public SendSkillTree() {
        super(CMD.CMD_GET_SKILL_TREE);
    }

    public SendSkillTree(short errorCode) {
        super(CMD.CMD_GET_SKILL_TREE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putSFSArray(Params.SKILLS, SFSArray.newFromJsonData(Utils.toJson(sageSkillModel.skills)));
        data.putInt(Params.MAX_SKILL_POINT, maxPoint);
    }
}
