package com.bamisu.log.gameserver.module.mail.cmd.send;

import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.mail.MailManager;
import com.bamisu.log.gameserver.module.mail.entities.MailBoxVO;
import com.bamisu.log.gameserver.module.mail.entities.MailVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGetListMail extends BaseMsg {
    public List<MailVO> listMail;
    public SendGetListMail() {
        super(CMD.CMD_GET_LIST_MAIL);
    }

    public SendGetListMail(short errorCode) {
        super(CMD.CMD_GET_LIST_MAIL, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        SFSArray array = new SFSArray();
        if (listMail != null){
            for (MailVO mailVO: listMail){
                SFSObject object = new SFSObject();
                object.putUtfString(Params.ID, mailVO.idMail);
                object.putInt(Params.TIME, Utils.getTimestampInSecond() - Utils.getTimeSecondFromString(Utils.DATE_TIME_FORMAT, mailVO.time));
                object.putUtfString(Params.TITLE, mailVO.title);
                object.putBool(Params.STATUS_READ, mailVO.statusRead);
                object.putBool(Params.STATUS_RECEIVE, mailVO.statusReceive);
                object.putUtfString(Params.CONTENT, mailVO.content);
                SFSArray listGift = new SFSArray();
                for (ResourcePackage resource: mailVO.listGift){
                    SFSObject gift = new SFSObject();
                    gift.putUtfString(Params.ID_ITEM, resource.id);
                    gift.putInt(Params.AMOUNT, resource.amount);
                    listGift.addSFSObject(gift);
                }
                object.putSFSArray(Params.LIST_ITEM, listGift);
                array.addSFSObject(object);
            }
            data.putSFSArray(Params.LIST, array);
        }
    }
}
