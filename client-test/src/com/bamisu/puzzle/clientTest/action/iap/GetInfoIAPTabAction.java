package com.bamisu.puzzle.clientTest.action.iap;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class GetInfoIAPTabAction extends ClientAction {
    public GetInfoIAPTabAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        String[] arrayId = {"tab36", "tab37", "tab38", "tab39", "tab41", "tab42", "tab43", "tab44", "tab45", "tab46", "tab47", "xa77a72"};
        String id = arrayId[Utils.randomInRange(0, arrayId.length - 1)];

        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_GET_INFO_IAP_TAB);
        data.putUtfString(Params.ID, id);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_IAP_STORE, data));
    }
}
