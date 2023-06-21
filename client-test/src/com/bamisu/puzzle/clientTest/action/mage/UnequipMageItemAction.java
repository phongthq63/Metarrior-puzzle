package com.bamisu.puzzle.clientTest.action.mage;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class UnequipMageItemAction extends ClientAction {
    public UnequipMageItemAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        short[] arrayPosition = {-1, 0, 1, 2, 3, 5};
        short position = arrayPosition[Utils.randomInRange(0, arrayPosition.length - 1)];

        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_UNEQUIP_MAGE_ITEM);
        data.putShort(Params.POSITION, position);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_MAGE, data));
    }
}
