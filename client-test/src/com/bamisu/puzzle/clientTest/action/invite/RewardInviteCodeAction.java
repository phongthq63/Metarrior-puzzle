package com.bamisu.puzzle.clientTest.action.invite;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class RewardInviteCodeAction extends ClientAction {
    public RewardInviteCodeAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        String[] arrayId = {"Starter", "Champion", "Ultimate", "asdfuad333"};
        String id = arrayId[Utils.randomInRange(0, arrayId.length - 1)];

        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_GET_REWARD_INVITE_CODE);
        data.putUtfString(Params.ID, id);
        data.putShort(Params.POINT, (short) 1);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_INVITE, data));
    }
}
