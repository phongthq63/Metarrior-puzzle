package com.bamisu.log.gameserver.datamodel.campaign.entities;

import com.bamisu.gamelib.item.entities.EStatusSlot;
import com.bamisu.log.gameserver.module.campaign.config.StoreCampaignSlotConfig;
import com.bamisu.log.gameserver.module.campaign.config.entities.RewardStoreCampaignSlotVO;
import com.bamisu.gamelib.item.entities.ISlotVO;
import com.bamisu.log.gameserver.module.hunt.config.entities.RewardRateVO;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.gamelib.entities.LIZRandom;
import com.bamisu.gamelib.entities.RandomObj;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;

public class StoreSlotCampaignInfo implements ISlotVO {
    public byte position;
    public ResourcePackage reward;
    public boolean status;
    public int buy;
    public int timeStame;

    public static StoreSlotCampaignInfo create(int position, boolean status){
        StoreSlotCampaignInfo data = new StoreSlotCampaignInfo();
        data.position = (byte) position;
        data.status = status;
        data.buy = 0;
        data.timeStame = Utils.getTimestampInSecond();

        return data;
    }

    public boolean genReward(int indexChap){
        if(haveLock())return false;

        LIZRandom rd = new LIZRandom();
        for(RewardStoreCampaignSlotVO slotCf : StoreCampaignSlotConfig.getInstance().store){
            if(position == slotCf.slot){

                for(int i = slotCf.reward.size() - 1; i >= indexChap; i--){
                    if(indexChap >= slotCf.reward.get(i).chap){

                        for(RewardRateVO rate : slotCf.reward.get(i).reward){
                            rd.push(new RandomObj(new ResourcePackage(rate.id, rate.amount), rate.rate));
                        }
                        reward = (ResourcePackage) rd.next().value;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean refresh(){
        if(isTimeRefresh()){
            reward = null;
            buy = 0;
            timeStame = Utils.getTimestampInSecond();
            return true;
        }
        return false;
    }
    private boolean isTimeRefresh(){
        for(RewardStoreCampaignSlotVO reward : StoreCampaignSlotConfig.getInstance().store){
            if(reward.slot == position && TimeUtils.isTimeTo(ETimeType.fromID(reward.timeRefresh), timeStame)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void lock() {
        status = EStatusSlot.LOCK.getValue();
    }

    @Override
    public void unlock() {
        status = EStatusSlot.UNLOCK.getValue();
    }

    @Override
    public boolean haveLock() {
        if(status == EStatusSlot.LOCK.getValue()){
            return status;
        }
        return EStatusSlot.UNLOCK.getValue();
    }
}
