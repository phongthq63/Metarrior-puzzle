package com.bamisu.log.gameserver.module.tree;

import com.bamisu.log.gameserver.datamodel.bag.UserBagModel;
import com.bamisu.log.gameserver.datamodel.event.GrandOpeningCheckInModel;
import com.bamisu.log.gameserver.datamodel.friends.FriendModel;
import com.bamisu.log.gameserver.datamodel.mail.MailModel;
import com.bamisu.log.gameserver.datamodel.vip.HonorModel;
import com.bamisu.log.gameserver.module.WoL.defines.WoLConquerStatus;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.bag.SpecialItemManager;
import com.bamisu.log.gameserver.module.event.event.grand_opening_checkin.GrandOpeningCheckInManager;
import com.bamisu.log.gameserver.module.friends.FriendManager;
import com.bamisu.log.gameserver.module.friends.define.EStatusHeartPoint;
import com.bamisu.log.gameserver.module.friends.entities.FriendDataVO;
import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.item.define.SpecialItem;
import com.bamisu.gamelib.item.entities.FragmentVO;
import com.bamisu.gamelib.item.entities.SpecialItemPackageVO;
import com.bamisu.log.gameserver.module.mail.MailManager;
import com.bamisu.log.gameserver.module.mail.entities.MailVO;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.log.gameserver.module.vip.entities.HonorDataVO;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventTreeManager {
    private static EventTreeManager ourInstance = null;
    public static EventTreeManager getInstance(){
        if (ourInstance == null){
            ourInstance = new EventTreeManager();
        }
        return ourInstance;
    }

    /**
     * NOT_CLAIM_GIFT_HONOR
     * return: List id, where can be received
     * if don't have return list empty
     */
    public List<Integer> checkGiftHonor(Zone zone, long uid){
        List<Integer> listID = new ArrayList<>();
        HonorModel honorModel = VipManager.getInstance().getVipModel(uid, zone);
        for (HonorDataVO honorDataVO: honorModel.listHonor){
            if (honorDataVO.status == WoLConquerStatus.CAN_RECEIVE.getStatus()){
                listID.add(honorDataVO.id);
            }
        }
        return listID;
    }

    /**
     *
     * return: List id, where can be received
     * if don't have return list empty
     */
    public boolean checkEventLogin(Zone zone, long uid){
        GrandOpeningCheckInModel grandOpeningCheckInModel = GrandOpeningCheckInManager.getInstance().getGrandOpeningCheckInModel(uid, zone);
        for (Integer integer: grandOpeningCheckInModel.listGift){
            if (integer.intValue() == WoLConquerStatus.CAN_RECEIVE.getStatus()){
                return true;
            }
        }
        return false;
    }

}
