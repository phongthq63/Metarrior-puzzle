package com.bamisu.puzzle.clientTest.action.celestial;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class ChangeCelestialAction extends ClientAction {
    public ChangeCelestialAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        String[] arrayCid = {"", "CEL000", "CEL001", "uxei434"};
        String cid = arrayCid[Utils.randomInRange(0, arrayCid.length - 1)];

        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_CHANGE_CELESTIAL);
        data.putUtfString(Params.ID, cid);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_CELESTIAL, data));
    }
}
