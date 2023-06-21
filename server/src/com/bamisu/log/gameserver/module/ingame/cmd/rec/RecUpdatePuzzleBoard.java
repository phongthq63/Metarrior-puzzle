package com.bamisu.log.gameserver.module.ingame.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.Collection;

/**
 * Create by Popeye on 3:13 PM, 7/2/2020
 */
public class RecUpdatePuzzleBoard extends BaseCmd {
    Collection<Integer> listDiamond;

    public RecUpdatePuzzleBoard(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        Collection<Integer> listDiamond = data.getIntArray(Params.PUZZLE);
    }
}
