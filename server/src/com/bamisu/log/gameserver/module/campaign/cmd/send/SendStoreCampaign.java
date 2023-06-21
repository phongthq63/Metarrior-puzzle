package com.bamisu.log.gameserver.module.campaign.cmd.send;

import com.bamisu.log.gameserver.datamodel.campaign.entities.StoreSlotCampaignInfo;
import com.bamisu.log.gameserver.module.campaign.config.StoreCampaignSlotConfig;
import com.bamisu.log.gameserver.module.campaign.config.entities.RewardStoreCampaignSlotVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendStoreCampaign extends BaseMsg {

    public int totalStar;
    public List<StoreSlotCampaignInfo> slots;

    public SendStoreCampaign() {
        super(CMD.CMD_GET_STORE_CAMPAIGN);
    }

    @Override
    public void packData() {
        super.packData();

        List<RewardStoreCampaignSlotVO> slotCf = StoreCampaignSlotConfig.getInstance().store;
        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        ISFSObject rewardPack;
        ISFSObject costPack;
        ResourcePackage resource;
        for(StoreSlotCampaignInfo slot : slots){
            objPack = new SFSObject();
            objPack.putByte(Params.POSITION, slot.position);

            rewardPack = new SFSObject();
            rewardPack.putUtfString(Params.ID, slot.reward.id);
            rewardPack.putInt(Params.AMOUNT, slot.reward.amount);
            objPack.putSFSObject(Params.REWARD, rewardPack);

            resource = slotCf.get(slot.position - 1).cost;
            costPack = new SFSObject();
            costPack.putUtfString(Params.ID, resource.id);
            costPack.putInt(Params.AMOUNT, resource.amount);
            objPack.putSFSObject(Params.COST, costPack);

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.SLOT, arrayPack);
        data.putInt(Params.STAR, totalStar);
    }
}
