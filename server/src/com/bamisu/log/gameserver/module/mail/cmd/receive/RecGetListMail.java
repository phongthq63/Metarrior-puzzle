package com.bamisu.log.gameserver.module.mail.cmd.receive;

import com.bamisu.log.gameserver.module.mail.entities.MailBoxVO;
import com.bamisu.gamelib.base.data.BaseCmd;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class RecGetListMail extends BaseCmd {
    public RecGetListMail(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {

    }
}
