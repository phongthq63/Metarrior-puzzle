package com.bamisu.puzzle.clientTest.action.guild;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class GetListGuildInfoAction extends ClientAction {
    public GetListGuildInfoAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        String[] arrayKey = {"", "239dx9"};
        String key = arrayKey[Utils.randomInRange(0, arrayKey.length - 1)];

        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_GET_LIST_GUILD_INFO);
        data.putUtfString(Params.ID, key);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_GUILD, data));
    }
}
