package com.bamisu.puzzle.clientTest.action.hero;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class OpenSlotHeroBlessingAction extends ClientAction {
    public OpenSlotHeroBlessingAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        String[] arrayTestMoney = {"MON1000", "MON1019", "MON1003", "MON2104", "as82kc912"};
        String idMoney = arrayTestMoney[Utils.randomInRange(0, arrayTestMoney.length - 1)];

        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_OPEN_SLOT_BLESSING);
        data.putUtfString(Params.ModuleChracter.ID, idMoney);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_HERO, data));
    }
}
