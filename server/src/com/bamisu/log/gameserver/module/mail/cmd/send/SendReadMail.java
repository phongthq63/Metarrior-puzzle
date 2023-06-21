package com.bamisu.log.gameserver.module.mail.cmd.send;

import com.bamisu.log.gameserver.module.mail.entities.MailVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class SendReadMail extends BaseMsg {
//    public MailVO mailVO;
    public SendReadMail() {
        super(CMD.CMD_READ_MAIL);
    }

    public SendReadMail(short errorCode) {
        super(CMD.CMD_READ_MAIL, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
//        SFSObject object = new SFSObject();
//        object.putLong(Params.SENDER, mailVO.senderId);
//        SFSArray listGift = new SFSArray();
//        for (ResourcePackage resource: mailVO.listGift){
//            SFSObject gift = new SFSObject();
//            gift.putUtfString(Params.ID_ITEM, resource.id);
//            gift.putInt(Params.AMOUNT, resource.amount);
//            listGift.addSFSObject(gift);
//        }
//        object.putSFSArray(Params.LIST_ITEM, listGift);
//        data.putSFSObject(Params.MAIL, object);
    }
}
