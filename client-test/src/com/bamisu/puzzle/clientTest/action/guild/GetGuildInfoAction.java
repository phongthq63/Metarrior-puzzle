package com.bamisu.puzzle.clientTest.action.guild;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class GetGuildInfoAction extends ClientAction {
    public GetGuildInfoAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        long[] arrayGid = {-1, 0, 10000, 83893};
        long gid = arrayGid[Utils.randomInRange(0, arrayGid.length - 1)];

        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_GET_GUILD_INFO);
        data.putLong(Params.ID, gid);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_GUILD, data));
    }
}
