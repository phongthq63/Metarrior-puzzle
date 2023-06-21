package com.bamisu.log.gameserver.datamodel.friends;

import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.log.gameserver.module.event.event.grand_opening_checkin.GrandOpeningCheckInManager;
import com.bamisu.log.gameserver.module.friends.cmd.send.SendAddFriend;
import com.bamisu.log.gameserver.module.friends.entities.FriendDataVO;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class FriendModel extends DataModel {
    public long uid;
    public int points = 0;
    public long time = GrandOpeningCheckInManager.getInstance().getNewTime();
    public List<FriendDataVO> listBlock = new ArrayList<>();
    public List<Long> listRequest = new ArrayList<>();  //danh sách những người đang gửi lời mời kết bạn cho mình
    public List<FriendDataVO> listFriends = new ArrayList<>();

    public FriendModel(long uid){
        this.uid = uid;
//        initTest();
    }

    public FriendModel(){}

    private void initTest() {
//        this.listRequest.add(Long.valueOf(10000));
//        this.listRequest.add(Long.valueOf(10002));
//        this.listRequest.add(Long.valueOf(10004));
//        this.listRequest.add(Long.valueOf(10006));
//        this.listFriends.add(new FriendDataVO(10008, true, true));
//        this.listFriends.add(new FriendDataVO(10010, true, true));
//        this.listFriends.add(new FriendDataVO(10012, true, true));
//        this.listFriends.add(new FriendDataVO(10014, true, true));
//        this.listBlock.add(new FriendDataVO(10016, true, true));
//        this.listBlock.add(new FriendDataVO(10018, true, true));
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.uid), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static FriendModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    public static FriendModel copyFromDBtoObject(String uId, Zone zone) {
        FriendModel pInfo = null;
        try {
            String str = (String) getModel(uId, FriendModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, FriendModel.class);
                if (pInfo != null) {
                }
            }
        } catch (DataControllerException e) {
        }
        if (pInfo == null) {
            pInfo = new FriendModel(Long.parseLong(uId));
            pInfo.saveToDB(zone);
        }
        return pInfo;
    }

    /**
     * check xem đã block người chơi userID chưa?
     * @param friendID
     * @return
     */
    public boolean haveBlock(long friendID) {
        for (FriendDataVO friendDataVO : listBlock) {
            if (friendDataVO.uid == friendID) {
                return true;
            }
        }

        return false;
    }

    /**
     * check xem đã là bạn của nhau chưa
     * @param friendID
     * @return
     */
    public boolean haveFriend(long friendID) {
        //Friend exist in list friend
        for (FriendDataVO friendDataVO : listFriends) {
            if (friendDataVO.uid == friendID) {
                return true;
            }
        }

        return false;
    }

    /**
     * check xem đã nhận được lời mời từ người chơi này chưa
     * @param friendID
     * @return
     */
    public boolean haveRequestFromUser(long friendID) {
        for (long id : listRequest) {
            if (id == friendID) {
                return true;
            }
        }

        return false;
    }
}
