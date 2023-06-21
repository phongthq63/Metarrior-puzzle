package com.bamisu.puzzle.clientTest.action.campaign;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class GetStoreCampaignAction extends ClientAction {
    public GetStoreCampaignAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_GET_STORE_CAMPAIGN);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_CAMPAIGN, data));
    }
}
