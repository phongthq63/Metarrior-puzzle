package com.bamisu.log.gameserver.datamodel.campaign.entities;

import com.bamisu.log.gameserver.module.campaign.config.StoreCampaignSlotConfig;
import com.bamisu.log.gameserver.module.campaign.config.entities.StoreCampaignSlotVO;

import java.util.ArrayList;
import java.util.List;

public class UserStoreCampaignInfo {

    public List<StoreSlotCampaignInfo> slots;

    public static UserStoreCampaignInfo create(int chap) {
        UserStoreCampaignInfo info = new UserStoreCampaignInfo();
        info.genStore(chap);

        return info;
    }


    private void genStore(int chap){
        genSlot();
        genReward(chap);
    }
    private void genSlot(){
        slots = new ArrayList<>();
        for(StoreCampaignSlotVO slot : StoreCampaignSlotConfig.getInstance().list){
            slots.add(StoreSlotCampaignInfo.create(slot.position, slot.status));
        }
    }
    private void genReward(int indexChap){
        for(StoreSlotCampaignInfo slot : slots){
            slot.genReward(indexChap);
        }
    }
}
