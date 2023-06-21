package com.bamisu.puzzle.clientTest.action.quest;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class GetTableQuestAction extends ClientAction {
    public GetTableQuestAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_GET_TABLE_QUEST);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_QUEST, data));
    }
}
