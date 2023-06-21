package com.bamisu.puzzle.clientTest.action.hero;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

public class LoadSceneTeamHeroAction extends ClientAction {
    public LoadSceneTeamHeroAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        String[] arrayTeamType = {"0", "1", "2", "3", "6", "asci3"};
        String teamType = arrayTeamType[Utils.randomInRange(0, arrayTeamType.length - 1)];

        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_LOAD_SCENE_TEAM_HERO);
        data.putUtfString(Params.TYPE, teamType);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_HERO, data));
    }
}
