package com.bamisu.log.gameserver.module.event.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.event.defind.EActionEvent;
import com.bamisu.log.gameserver.module.event.defind.EEventInGame;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecActionInEvent extends BaseCmd {

    public EActionEvent action = EActionEvent.NONE;
    public EEventInGame event = EEventInGame.NONE;

    public String id;
    public int count;

    public RecActionInEvent(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        event = EEventInGame.fromID(data.getUtfString(Params.EVENT));

        switch (event){
            case CHRISSMATE:
                action = EActionEvent.BUY;
                id = data.getUtfString(Params.ID);
                count = data.getInt(Params.COUNT);
                break;
        }
    }
}
