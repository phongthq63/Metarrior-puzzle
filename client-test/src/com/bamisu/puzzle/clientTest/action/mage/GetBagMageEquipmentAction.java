package com.bamisu.puzzle.clientTest.action.mage;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class GetBagMageEquipmentAction extends ClientAction {
    public GetBagMageEquipmentAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        short[] arrayPos = {-1, 0, 1, 2, 3, 4};
        short pos = arrayPos[Utils.randomInRange(0, arrayPos.length - 1)];

        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_GET_BAG_MAGE_EQUIPMENT);
        data.putShort(Params.POSITION, pos);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_MAGE, data));
    }
}
