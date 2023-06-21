package com.bamisu.log.gameserver.module.friends.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecAddAllFriendInSuggest extends BaseCmd {
    public Collection<Long> ids = new ArrayList<>();

    public RecAddAllFriendInSuggest(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        ids = data.getLongArray(Params.LIST);
    }
}
