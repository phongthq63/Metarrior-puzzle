package com.bamisu.log.gameserver.module.notification.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.google.common.collect.Lists;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.List;

public class RecRemoveNotify extends BaseCmd {

    public List<String> listNotify;

    public RecRemoveNotify(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        listNotify = Lists.newArrayList(data.getUtfStringArray(Params.NOTIFY));
    }
}
