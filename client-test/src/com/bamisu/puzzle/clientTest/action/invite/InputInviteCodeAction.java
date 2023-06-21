package com.bamisu.puzzle.clientTest.action.invite;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class InputInviteCodeAction extends ClientAction {
    public InputInviteCodeAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        String[] arrayCode = {"", "dsc8scd832324", "RUC12348"};
        String code = arrayCode[Utils.randomInRange(0, arrayCode.length - 1)];

        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_GET_INFO_IAP_TAB);
        data.putUtfString(Params.CODE, code);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_INVITE, data));
    }
}
