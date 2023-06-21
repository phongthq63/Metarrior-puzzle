package com.bamisu.puzzle.clientTest.action.hero;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.base.ClientAction;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSObject;
import sfs2x.client.requests.ExtensionRequest;

/**
 * Create by Popeye on 10:48 AM, 7/28/2020
 */
public class SummonAction extends ClientAction {
    public SummonAction(Client client) {
        super(client);
    }

    @Override
    public void doAction() {
        String[] arrayIdSummon = {"0", "1", "2", "3", "greenNblue", "MON1010", "asdas332"};
        String idSummon = arrayIdSummon[Utils.randomInRange(0, arrayIdSummon.length - 1)];
        short[] arrayCount = {0, 1, 4, 10, 15};
        short count = arrayCount[Utils.randomInRange(0, arrayCount.length - 1)];

        SFSObject data = new SFSObject();
        data.putInt(Params.CMD_ID, CMD.CMD_SUMMON_USER_HERO);
        data.putUtfString(Params.ModuleChracter.ID, idSummon);
        data.putShort(Params.ModuleChracter.COUNT, count);
        client.getSfsClient().send(new ExtensionRequest(Params.Module.MODULE_HERO, data));
    }
}
