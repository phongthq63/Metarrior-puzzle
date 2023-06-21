package com.bamisu.puzzle.clientTest.action.quest;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class GetRewardQuestAction extends ClientAction {
    public GetRewardQuestAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        String[] arrayId = {"daily0", "daily1", "daily2", "daily3", "daily4", "daily5", "daily6", "daily7", "daily8", "daily9",
                "weekly0","weekly1", "weekly2", "weekly3", "weekly4", "weekly5", "weekly6", "weekly7",
                "all0", "all1", "all2", "all3", "all4", "all5", "all6", "all7", "all8", "all9", "all10",
                "19c9112"};
        String id = arrayId[Utils.randomInRange(0, arrayId.length - 1)];

        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_GET_REWARD_QUEST);
        data.putUtfString(Params.ID, id);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_QUEST, data));
    }
}
