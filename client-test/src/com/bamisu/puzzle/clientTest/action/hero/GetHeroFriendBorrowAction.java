package com.bamisu.puzzle.clientTest.action.hero;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class GetHeroFriendBorrowAction extends ClientAction {
    public GetHeroFriendBorrowAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        String[] arrayTeamType = {"0", "1", "2", "3", "6"};
        String teamType = arrayTeamType[Utils.randomInRange(0, arrayTeamType.length - 1)];

        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_GET_HERO_FRIEND_BORROW);
        data.putShort(Params.ID, Short.parseShort(teamType));
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_HERO, data));
    }
}
