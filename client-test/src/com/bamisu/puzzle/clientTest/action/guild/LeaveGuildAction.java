package com.bamisu.puzzle.clientTest.action.guild;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class LeaveGuildAction extends ClientAction {
    public LeaveGuildAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        long[] arrayUid = {-1, 0, 10000, 83893};
        long uid = arrayUid[Utils.randomInRange(0, arrayUid.length - 1)];

        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_LEAVE_GUILD);
        data.putLong(Params.ID, uid);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_GUILD, data));
    }
}
