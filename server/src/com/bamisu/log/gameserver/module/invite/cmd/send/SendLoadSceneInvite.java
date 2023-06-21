package com.bamisu.log.gameserver.module.invite.cmd.send;

import com.bamisu.log.gameserver.module.invite.InviteManager;
import com.bamisu.log.gameserver.module.invite.config.entities.InviteBonusVO;
import com.bamisu.log.gameserver.module.invite.entities.InviteBonusDetail;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.List;

public class SendLoadSceneInvite extends BaseMsg {

    public String code;
    public List<InviteBonusDetail> list;

    public SendLoadSceneInvite() {
        super(CMD.CMD_LOAD_SCENE_INVITE_CODE);
    }

    public SendLoadSceneInvite(short errorCode) {
        super(CMD.CMD_LOAD_SCENE_INVITE_CODE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putUtfString(Params.CODE, code);

        List<InviteBonusVO> bonusCf = InviteManager.getInstance().getListInviteBonusConfig();
        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        out_loop:
        for(InviteBonusVO cf : bonusCf){
            objPack = new SFSObject();
            objPack.putUtfString(Params.ID, cf.id);

            for(InviteBonusDetail data : list){
                if(data.id.equals(cf.id)){

                    objPack.putShort(Params.POINT, data.point);
                    objPack.putShortArray(Params.COMPLETE, data.complete);
                    arrayPack.addSFSObject(objPack);
                    continue out_loop;
                }
            }

            objPack.putShort(Params.POINT, (short) 0);
            objPack.putShortArray(Params.COMPLETE, new ArrayList<>());
            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.LIST, arrayPack);
    }
}
