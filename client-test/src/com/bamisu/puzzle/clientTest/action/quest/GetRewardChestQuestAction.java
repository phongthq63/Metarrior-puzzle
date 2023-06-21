package com.bamisu.puzzle.clientTest.action.quest;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class GetRewardChestQuestAction extends ClientAction {
    public GetRewardChestQuestAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        String[] arrayId = {"chest_daily_20", "chest_daily_40", "chest_daily_60", "chest_daily_80", "chest_daily_100",
                "chest_weekly_20","chest_weekly_40", "chest_weekly_60", "chest_weekly_80", "chest_weekly_100",
                "19c9112"};
        String id = arrayId[Utils.randomInRange(0, arrayId.length - 1)];

        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_GET_REWARD_CHEST_QUEST);
        data.putUtfString(Params.ID, id);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_QUEST, data));
    }
}
