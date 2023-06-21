package com.bamisu.puzzle.clientTest.action.invite;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class LoadSceneInviteCodeAction extends ClientAction {
    public LoadSceneInviteCodeAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_LOAD_SCENE_INVITE_CODE);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_INVITE, data));
    }
}
