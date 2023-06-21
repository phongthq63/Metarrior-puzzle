package com.bamisu.log.gameserver.module.guild.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.manager.UserManager;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.guild.entities.GiftGuildInfo;
import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyManager;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPPackageVO;
import com.bamisu.log.gameserver.module.guild.define.EGuildGiftType;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SendGetListGiftGuild extends BaseMsg {

    public List<GiftGuildInfo> listGift;
    public Set<String> setClaimed;
    public UserManager userManager;

    public SendGetListGiftGuild() {
        super(CMD.CMD_GET_LIST_GIFT_GUILD);
    }

    public SendGetListGiftGuild(short errorCode) {
        super(CMD.CMD_GET_LIST_GIFT_GUILD, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        int now = Utils.getTimestampInSecond();

        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        boolean claimed;
        UserModel userModel;
        IAPPackageVO iapPackageCf;
        ISFSArray rewardPack;
        ISFSObject rewardObj;
        List<String> constantItem = Arrays.asList("RES1025", "RES1026", "MON1014");
        for(GiftGuildInfo data : listGift){
            objPack = new SFSObject();

            objPack.putUtfString(Params.HASH, data.hash);
            objPack.putUtfString(Params.ID, data.id);
            objPack.putUtfString(Params.TYPE, data.type);

            claimed = setClaimed.contains(data.hash);
            objPack.putBool(Params.USE, claimed);
            if(claimed){
                rewardPack = new SFSArray();

                for(ResourcePackage res : data.resources){
                    rewardObj = new SFSObject();

                    rewardObj.putUtfString(Params.ID, res.id);
                    rewardObj.putLong(Params.AMOUNT, res.amount);
                    rewardObj.putBool(Params.DISPLAY, !constantItem.contains(res.id));

                    rewardPack.addSFSObject(rewardObj);
                }

                objPack.putSFSArray(Params.REWARD, rewardPack);
            }else {
                if(data.description != null){
                    switch (EGuildGiftType.fromID(data.type)){
                        case BUY:
                            userModel = userManager.getUserModel(data.description.uid);
                            iapPackageCf = IAPBuyManager.getInstance().getIAPPackageConfig(data.description.idPackage, userManager.getZone());

                            if(userModel != null && iapPackageCf != null){
                                objPack.putUtfString(Params.FROM, userModel.displayName);
                                objPack.putUtfString(Params.BUY, iapPackageCf.giftName);
                            }
                            break;
                        case UP_LEVEL_GIFT:
                            objPack.putInt(Params.LEVEL, data.description.level);
                            break;
                    }

                }

                objPack.putInt(Params.TIME, (data.timeExpert < 0) ? -1 : data.timeExpert - now);
            }

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.GIFT, arrayPack);
    }
}
