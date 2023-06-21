package com.bamisu.puzzle.clientTest.action.campaign;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class BuyStoreCampaignAction extends ClientAction {
    public BuyStoreCampaignAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        byte[] arrayPosition = {-1, 0, 1, 2, 3, 4};
        byte position = arrayPosition[Utils.randomInRange(0, arrayPosition.length - 1)];

        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_BUY_STORE_CAMPAIGN);
        data.putByte(Params.POSITION, position);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_CAMPAIGN, data));
    }
}
