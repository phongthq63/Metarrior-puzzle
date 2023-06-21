package com.bamisu.log.gameserver.module.friends;

import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.bag.UserBagModel;
import com.bamisu.log.gameserver.datamodel.campaign.UserCampaignDetailModel;
import com.bamisu.log.gameserver.datamodel.friends.UserFriendHeroManagerModel;
import com.bamisu.log.gameserver.datamodel.friends.FriendModel;
import com.bamisu.log.gameserver.datamodel.guild.GuildModel;
import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.event.event.grand_opening_checkin.GrandOpeningCheckInManager;
import com.bamisu.log.gameserver.module.friends.cmd.send.*;
import com.bamisu.log.gameserver.module.friends.define.EHeartPoint;
import com.bamisu.log.gameserver.module.friends.define.EStatusFriend;
import com.bamisu.log.gameserver.module.friends.define.EStatusHeartPoint;
import com.bamisu.log.gameserver.module.friends.entities.*;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.log.gameserver.module.vip.defines.EGiftVip;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;

public class FriendManager {
    private static FriendManager ourInstance = null;

    public static FriendManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new FriendManager();
        }
        return ourInstance;
    }

    FriendHandler friendHandler;
    ZoneExtension zoneExtension;
    Zone zone;

    private FriendConfig friendConfig;

    private FriendManager() {
        friendConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Friend.FILE_PATH_CONFIG_FRIEND), FriendConfig.class);
    }

    public FriendManager(FriendHandler friendHandler) {
        this.friendHandler = friendHandler;
        this.zoneExtension = (ZoneExtension) friendHandler.getParentExtension();
        this.zone = zoneExtension.getParentZone();
    }

    public FriendModel getFriendModel(long uid, Zone zone) {
        FriendModel friendModel = FriendModel.copyFromDBtoObject(uid, zone);

        //xoa chinh minh trong danh sach ban
        List<FriendDataVO> listRemove = new ArrayList<>();
        for (FriendDataVO friendDataVO : friendModel.listFriends) {
            if (friendDataVO.uid == uid) {
                listRemove.add(friendDataVO);
            }
        }
        if (!listRemove.isEmpty()) {
            friendModel.listFriends.removeAll(listRemove);
            friendModel.saveToDB(zone);
        }

        return friendModel;
    }

    public FriendConfig getFriendConfig() {
        friendConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Friend.FILE_PATH_CONFIG_FRIEND), FriendConfig.class);
        return friendConfig;
    }

    /**
     * Get list friends from user
     */
    public void getListFriends(User user, long uid, BaseExtension extension) {
        FriendModel friendModel = getFriendModel(uid, user.getZone());
        UserBagModel bag = BagManager.getInstance().getUserBagModel(uid, user.getZone());
        //Refresh time
        refreshListFriend(friendModel, user.getZone());
        //---------
        List<FriendInfoVO> listFriend = new ArrayList<>();
        for (FriendDataVO vo : friendModel.listFriends) {
            FriendInfoVO friendInfoVO = new FriendInfoVO();
            UserModel um = extension.getUserManager().getUserModel(vo.uid);
            friendInfoVO.uid = vo.uid;
            friendInfoVO.server = um.serverId;
            friendInfoVO.name = um.displayName;
            friendInfoVO.active = checkActiveOrNot(um, ExtensionUtility.getInstance().getUserById(vo.uid));
            friendInfoVO.campaign = UserCampaignDetailModel.copyFromDBtoObject(vo.uid, user.getZone()).userMainCampaignDetail.readNextStation();
            friendInfoVO.power = HeroManager.getInstance().getPower(vo.uid, zone);
            friendInfoVO.avatar = um.avatar;
            friendInfoVO.level = BagManager.getInstance().getLevelUser(um.userID, user.getZone());
            friendInfoVO.receive = vo.receive;
            friendInfoVO.send = vo.send;
            friendInfoVO.avatarFrame = um.avatarFrame;
            friendInfoVO.gender = um.gender;
            listFriend.add(friendInfoVO);
        }
        SendShowInfoListFriends send = new SendShowInfoListFriends();
        send.list = listFriend;
        send.resourcePackage = bag.mapMoney.get(MoneyType.FRIENDSHIP_BANNER.getId());
        friendHandler.send(send, user);
    }

    /**
     * 0: Active
     * Else: time ago(timestamp)
     */
    private int checkActiveOrNot(UserModel um, User user) {
        if (user != null) {
            return EStatusFriend.ACTIVE.getStatus();
        } else {
            if (um.lastLogout <= 0) {
                return (int) um.lastLogin;
            } else {
                return um.lastLogout;
            }
        }
    }


    public boolean checkIsFriend(long uid, Zone zone, long uidFriend) {
        FriendModel friendModel = getFriendModel(uid, zone);
        for (FriendDataVO friendDataVO : friendModel.listFriends) {
            if (friendDataVO.uid == uidFriend) {
                return true;
            }
        }
        return false;
    }

    public void sendPointToOneUser(User user, BaseExtension extension, long uidFriend) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        FriendModel friendModel = getFriendModel(uid, user.getZone());
//        UserBagModel bag = BagManager.getInstance().getUserBagModel(uid, user.getZone());
//        if (friendModel.points >= getFriendConfig().points){
//        if (friendModel.points >= VipManager.getInstance().getBonus(uid, user.getZone(), EGiftVip)){
//            SendSendPointToOneUser send = new SendSendPointToOneUser(ServerConstant.ErrorCode.ERR_RECEIVED_ENOUGH_POINT);
//            friendHandler.send(send, user);
//            return;
//        }
        for (FriendDataVO vo : friendModel.listFriends) {
            if (vo.uid == uidFriend) {

                //Changing in friend
                changingInFriend(uidFriend, zone, uid);

                vo.send = EStatusHeartPoint.SENT.getStatus();
                if (friendModel.saveToDB(user.getZone())) {
                    SendSendPointToOneUser send = new SendSendPointToOneUser();
                    friendHandler.send(send, user);

                    //Event
                    Map<String, Object> data = new HashMap<>();
                    List<ResourcePackage> listMoney = new ArrayList<>();
                    listMoney.add(new ResourcePackage(MoneyType.FRIENDSHIP_BANNER.getId(), EHeartPoint.SEND_HEART_POINT.getPoint()));
                    data.put(Params.LIST, listMoney);
                    GameEventAPI.ariseGameEvent(EGameEvent.SEND_MONEY, uid, data, extension.getParentZone());
                    return;
                }
            }
        }
        SendSendPointToOneUser send = new SendSendPointToOneUser(ServerConstant.ErrorCode.ERR_SYS);
        friendHandler.send(send, user);
    }

    private void changingInFriend(long uid, Zone zone, long idFriend) {
        FriendModel friend = getFriendModel(uid, zone);
        for (FriendDataVO friendDataVO : friend.listFriends) {
            if (idFriend == friendDataVO.uid) {
                friendDataVO.receive = EStatusHeartPoint.UNSENT.getStatus();
                friend.saveToDB(zone);
                return;
            }
        }
    }

    public void receivePointFromOneUser(User user, BaseExtension extension, long uidFriend) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        FriendModel friendModel = getFriendModel(uid, user.getZone());
//        if (friendModel.points >= getFriendConfig().points){
        if (friendModel.points >= VipManager.getInstance().getBonus(uid, user.getZone(), EGiftVip.RECEIVE_FRIENDSHIP_HEARTS)) {
            SendSendPointToOneUser send = new SendSendPointToOneUser(ServerConstant.ErrorCode.ERR_RECEIVED_ENOUGH_POINT);
            friendHandler.send(send, user);
            return;
        }
//        UserBagModel bag = BagManager.getInstance().getUserBagModel(uid, user.getZone());
        for (FriendDataVO vo : friendModel.listFriends) {
            if (vo.uid == uidFriend) {
                ResourcePackage resourcePackage = new ResourcePackage(MoneyType.FRIENDSHIP_BANNER.getId(), EHeartPoint.SEND_HEART_POINT.getPoint());
                List<ResourcePackage> list = new ArrayList<>();
                friendModel.points += EHeartPoint.SEND_HEART_POINT.getPoint();
                list.add(resourcePackage);
                vo.receive = EStatusHeartPoint.SENT.getStatus();
                if (friendModel.saveToDB(user.getZone()) && BagManager.getInstance().addItemToDB(list, uid, zone, UserUtils.TransactionType.RECEIVE_FRIEND_POINT)) {
                    SendReceivePointFromOneUser send = new SendReceivePointFromOneUser();
                    friendHandler.send(send, user);
                    return;
                }
            }
        }
        SendReceivePointFromOneUser send = new SendReceivePointFromOneUser(ServerConstant.ErrorCode.ERR_SYS);
        friendHandler.send(send, user);
    }

    public void receiveAndSendAllUser(User user, BaseExtension extension) {
        UserModel um = extension.getUserManager().getUserModel(user);
        FriendModel friendModel = getFriendModel(um.userID, user.getZone());
        if (friendModel.points >= VipManager.getInstance().getBonus(um.userID, user.getZone(), EGiftVip.RECEIVE_FRIENDSHIP_HEARTS)) {
            SendSendPointToOneUser send = new SendSendPointToOneUser(ServerConstant.ErrorCode.ERR_RECEIVED_ENOUGH_POINT);
            friendHandler.send(send, user);
            return;
        }
        int count = 0;
        for (FriendDataVO vo : friendModel.listFriends) {
            if (vo.receive == EStatusHeartPoint.UNSENT.getStatus() && vo.send == EStatusHeartPoint.UNSENT.getStatus()) {
                vo.receive = EStatusHeartPoint.SENT.getStatus();
                vo.send = EStatusHeartPoint.SENT.getStatus();
                changingInFriend(vo.uid, zone, um.userID);
                count++;
            } else if (vo.receive == EStatusHeartPoint.UNSENT.getStatus() && vo.send == EStatusHeartPoint.SENT.getStatus()) {
                vo.receive = EStatusHeartPoint.SENT.getStatus();
                count++;
            } else if (vo.receive == EStatusHeartPoint.SENT.getStatus() && vo.send == EStatusHeartPoint.UNSENT.getStatus()) {
                vo.send = EStatusHeartPoint.SENT.getStatus();
                changingInFriend(vo.uid, zone, um.userID);
//                count++;
            }
        }
        int point = EHeartPoint.SEND_HEART_POINT.getPoint() * count;
        ResourcePackage resourcePackage = new ResourcePackage(MoneyType.FRIENDSHIP_BANNER.getId(), point);
        List<ResourcePackage> list = new ArrayList<>();
        list.add(resourcePackage);
        if (friendModel.saveToDB(user.getZone()) && BagManager.getInstance().addItemToDB(list, um.userID, user.getZone(), UserUtils.TransactionType.RECEIVE_FRIEND_POINT)) {
            SendReceiveAndSendAllUser send = new SendReceiveAndSendAllUser();
            friendHandler.send(send, user);

            //Event
            Map<String, Object> data = new HashMap<>();
            List<ResourcePackage> listMoney = new ArrayList<>();
            listMoney.add(new ResourcePackage(MoneyType.FRIENDSHIP_BANNER.getId(), EHeartPoint.SEND_HEART_POINT.getPoint()));
            data.put(Params.LIST, listMoney);
            GameEventAPI.ariseGameEvent(EGameEvent.SEND_MONEY, um.userID, data, extension.getParentZone());
            return;
        }
        SendReceiveAndSendAllUser send = new SendReceiveAndSendAllUser(ServerConstant.ErrorCode.ERR_SYS);
        friendHandler.send(send, user);
    }

    public void listFriendsBlocked(User user, BaseExtension extension) {
        UserModel um = extension.getUserManager().getUserModel(user);
        FriendModel friendModel = getFriendModel(um.userID, user.getZone());
        List<BlockedFriendVO> list = new ArrayList<>();
        for (FriendDataVO friendDataVO : friendModel.listBlock) {
            BlockedFriendVO blockedFriendVO = new BlockedFriendVO();
            UserModel userModel = extension.getUserManager().getUserModel(friendDataVO.uid);
            blockedFriendVO.uid = userModel.userID;
            blockedFriendVO.avatar = userModel.avatar;
            blockedFriendVO.level = BagManager.getInstance().getLevelUser(um.userID, user.getZone());
            blockedFriendVO.name = userModel.displayName;
            blockedFriendVO.avatarFrame = userModel.avatarFrame;
            blockedFriendVO.power = HeroManager.getInstance().getPower(blockedFriendVO.uid, user.getZone());
            list.add(blockedFriendVO);
        }
        SendListFriendsBlocked send = new SendListFriendsBlocked();
        send.list = list;
        friendHandler.send(send, user);
    }

    public void restoreBlockedFriend(User user, BaseExtension extension, long uidFriend) {
        UserModel userModel = extension.getUserManager().getUserModel(user);
        FriendModel friendModel = getFriendModel(userModel.userID, user.getZone());
//        if (friendModel.listFriends.size() >= getFriendConfig().friends){
//            SendRestoreBlockedFriend send = new SendRestoreBlockedFriend(ServerConstant.ErrorCode.ERR_MAX_LIST_FRIENDS);
//            friendHandler.send(send, user);
//            return;
//        }
        for (FriendDataVO friendDataVO : friendModel.listBlock) {
            if (uidFriend == friendDataVO.uid) {
//                friendModel.listFriends.add(friendDataVO);
                friendModel.listBlock.remove(friendDataVO);
                if (friendModel.saveToDB(user.getZone())) {
                    SendRestoreBlockedFriend send = new SendRestoreBlockedFriend();
                    friendHandler.send(send, user);
                    return;
                }
            }
        }
        SendRestoreBlockedFriend send = new SendRestoreBlockedFriend(ServerConstant.ErrorCode.ERR_SYS);
        friendHandler.send(send, user);
    }

    public void blockFriend(User user, BaseExtension extension, long uidFriend) {
        UserModel userModel = extension.getUserManager().getUserModel(user);
        FriendModel friendModel = getFriendModel(userModel.userID, user.getZone());
        if (friendModel.listBlock.size() >= getFriendConfig().block) {
            SendBlockFriend send = new SendBlockFriend(ServerConstant.ErrorCode.ERR_LIST_BLOCK_FULL);
            friendHandler.send(send, user);
            return;
        }

        //Check in list block
        for (int i = 0; i < friendModel.listBlock.size(); i++) {
            if (friendModel.listBlock.get(i).uid == uidFriend) {
                return;
            }
        }

        //In list friend
        for (FriendDataVO friendDataVO : friendModel.listFriends) {
            if (uidFriend == friendDataVO.uid) {
                //Changing in user
                friendModel.listFriends.remove(friendDataVO);
                friendModel.listBlock.add(friendDataVO);

                //Changing in user's friend
                UserModel friend = extension.getUserManager().getUserModel(friendDataVO.uid);
                FriendModel friendData = getFriendModel(friend.userID, zone);
                for (FriendDataVO vo : friendData.listFriends) {
                    if (vo.uid == userModel.userID) {
                        friendData.listFriends.remove(vo);
                        break;
                    }
                }


                if (friendModel.saveToDB(user.getZone()) && friendData.saveToDB(zone)) {
                    SendBlockFriend send = new SendBlockFriend();
                    friendHandler.send(send, user);
                    return;
                }
            }
        }

        //Searching player wanna block
        UserModel player = ((ZoneExtension) user.getZone().getExtension()).getUserManager().getUserModelByKey(String.valueOf(uidFriend));
        if (player != null) {
            FriendDataVO friendDataVO = new FriendDataVO(player.userID, true, true);
            friendModel.listBlock.add(friendDataVO);
            friendModel.saveToDB(user.getZone());
            SendBlockFriend send = new SendBlockFriend();
            friendHandler.send(send, user);
            return;
        }

        SendBlockFriend send = new SendBlockFriend(ServerConstant.ErrorCode.ERR_SYS);
        friendHandler.send(send, user);
    }

    public void deleteFriend(User user, BaseExtension extension, long uidFriend) {
        UserModel userModel = extension.getUserManager().getUserModel(user);
        FriendModel friendModel = getFriendModel(userModel.userID, user.getZone());
        for (FriendDataVO friendDataVO : friendModel.listFriends) {
            if (uidFriend == friendDataVO.uid) {
                friendModel.listFriends.remove(friendDataVO);

                //Changing in user's friend
                UserModel friend = extension.getUserManager().getUserModel(friendDataVO.uid);
                FriendModel friendData = getFriendModel(friend.userID, zone);
                for (FriendDataVO vo : friendData.listFriends) {
                    if (vo.uid == userModel.userID) {
                        friendData.listFriends.remove(vo);
                        break;
                    }
                }
                if (friendModel.saveToDB(user.getZone()) && friendData.saveToDB(zone)) {
                    SendDeleteFriend send = new SendDeleteFriend();
                    friendHandler.send(send, user);
                    return;
                }
            }
        }
        SendDeleteFriend send = new SendDeleteFriend(ServerConstant.ErrorCode.ERR_SYS);
        friendHandler.send(send, user);
    }

    public void listRequestAddFriend(User user, BaseExtension extension) {
        UserModel userModel = extension.getUserManager().getUserModel(user);
        FriendModel friendModel = getFriendModel(userModel.userID, user.getZone());
        List<BlockedFriendVO> list = new ArrayList<>();
        for (Long id : friendModel.listRequest) {
            BlockedFriendVO blockedFriendVO = new BlockedFriendVO();
            UserModel friend = extension.getUserManager().getUserModel(id);
            blockedFriendVO.power = HeroManager.getInstance().getPower(friend.userID, user.getZone());
            ;
            blockedFriendVO.name = friend.displayName;
            blockedFriendVO.level = BagManager.getInstance().getLevelUser(friend.userID, user.getZone());
            blockedFriendVO.avatar = friend.avatar;
            blockedFriendVO.uid = friend.userID;
            blockedFriendVO.avatarFrame = friend.avatarFrame;
            list.add(blockedFriendVO);
        }
        SendShowListRequestAddFriend send = new SendShowListRequestAddFriend();
        send.list = list;
        friendHandler.send(send, user);
    }

    public void deleteAllRequest(User user, BaseExtension extension) {
        UserModel userModel = extension.getUserManager().getUserModel(user);
        FriendModel friendModel = getFriendModel(userModel.userID, user.getZone());
        friendModel.listRequest.clear();
        if (friendModel.saveToDB(user.getZone())) {
            SendDeleteAllRequest send = new SendDeleteAllRequest();
            friendHandler.send(send, user);
            return;
        }
        SendDeleteAllRequest send = new SendDeleteAllRequest(ServerConstant.ErrorCode.ERR_SYS);
        friendHandler.send(send, user);
    }

    public void deleteOneRequest(User user, BaseExtension extension, long uid) {
        UserModel userModel = extension.getUserManager().getUserModel(user);
        FriendModel friendModel = getFriendModel(userModel.userID, user.getZone());
//        friendModel.listRequest.removeIf(id -> uid == id);
        for (Long id : friendModel.listRequest) {
            if (uid == id) {
                friendModel.listRequest.remove(id);
                friendModel.saveToDB(user.getZone());
                SendDeleteOneRequest send = new SendDeleteOneRequest();
                friendHandler.send(send, user);
                return;
            }
        }
        SendDeleteOneRequest send = new SendDeleteOneRequest(ServerConstant.ErrorCode.ERR_SYS);
        friendHandler.send(send, user);
    }

    /**
     * @param user
     * @param extension
     * @param uid
     * @param botID     -1 nếu là người
     */
    public void acceptOneRequest(User user, BaseExtension extension, long uid, long botID) {
        UserModel um = (botID == -1) ? extension.getUserManager().getUserModel(user) : extension.getUserManager().getUserModel(botID);
        FriendModel you = getFriendModel(um.userID, friendHandler.getParentExtension().getParentZone());
        if (you.listFriends.size() >= getFriendConfig().friends) {
            if (botID == -1) {    //chỉ gửi về nếu ko phải bot
                SendAcceptOneRequest send = new SendAcceptOneRequest(ServerConstant.ErrorCode.ERR_MAX_LIST_FRIENDS);
                friendHandler.send(send, user);
            }
            return;
        }
        for (long id : you.listRequest) {
            if (id == uid) {
                UserModel friendModel = extension.getUserManager().getUserModel(id);
                FriendModel friend = getFriendModel(friendModel.userID, zone);
                if (friend.listFriends.size() >= getFriendConfig().friends) {
                    if (botID == -1) {    //chỉ gửi về nếu ko phải bot
                        SendAcceptOneRequest send = new SendAcceptOneRequest(ServerConstant.ErrorCode.ERR_MAX_LIST_FRIENDS_OF_FRIEND);
                        friendHandler.send(send, user);
                    }
                    return;
                }

                you.listRequest.remove(id);
                FriendDataVO friendDataVO = new FriendDataVO(id, EStatusHeartPoint.UNSENT.getStatus(), EStatusHeartPoint.SENT.getStatus());
                friend.listFriends.add(new FriendDataVO(um.userID, EStatusHeartPoint.UNSENT.getStatus(), EStatusHeartPoint.SENT.getStatus()));
                you.listFriends.add(friendDataVO);

                if (friend.saveToDB(zone) && you.saveToDB(zone)) {
                    if (botID == -1) {    //chỉ gửi về nếu ko phải bot
                        SendAcceptOneRequest send = new SendAcceptOneRequest();
                        friendHandler.send(send, user);
                    }
                    return;
                }
            }
        }
        if (botID == -1) {    //chỉ gửi về nếu ko phải bot
            SendAcceptOneRequest send = new SendAcceptOneRequest(ServerConstant.ErrorCode.ERR_SYS);
            friendHandler.send(send, user);
        }
    }

    public void acceptAllRequest(User user, BaseExtension extension) {
        UserModel um = extension.getUserManager().getUserModel(user);
        FriendModel you = getFriendModel(um.userID, user.getZone());
        //Check list friend
        if (you.listFriends.size() >= getFriendConfig().friends) {
            SendAcceptAllRequest send = new SendAcceptAllRequest(ServerConstant.ErrorCode.ERR_MAX_LIST_FRIENDS);
            friendHandler.send(send, user);
            return;
        }
        int count = getFriendConfig().friends - you.listFriends.size();
        //Enough space to add all request
        if (count > you.listRequest.size()) {
            Iterator<Long> i = you.listRequest.iterator();
            while (i.hasNext()) {
//            for (long id: you.listRequest){
                Long id = i.next();
                FriendModel friend = getFriendModel(id, zone);
                if (friend.listFriends.size() < getFriendConfig().friends) {
                    FriendDataVO yourData = new FriendDataVO(um.userID, EStatusHeartPoint.UNSENT.getStatus(), EStatusHeartPoint.SENT.getStatus());
                    FriendDataVO friendData = new FriendDataVO(id, EStatusHeartPoint.UNSENT.getStatus(), EStatusHeartPoint.SENT.getStatus());
                    i.remove();
                    friend.listFriends.add(yourData);
                    you.listFriends.add(friendData);
                    friend.saveToDB(zone);
                }
            }
        } else { //Not enough space to add all request
            while (count > 0) {
                Iterator<Long> i = you.listRequest.iterator();
                while (i.hasNext()) {
//                for (long id: you.listRequest){
                    Long id = i.next();
                    FriendModel friend = getFriendModel(id, zone);
                    if (friend.listFriends.size() < getFriendConfig().friends) {
                        FriendDataVO yourData = new FriendDataVO(um.userID, EStatusHeartPoint.UNSENT.getStatus(), EStatusHeartPoint.SENT.getStatus());
                        FriendDataVO friendData = new FriendDataVO(id, EStatusHeartPoint.UNSENT.getStatus(), EStatusHeartPoint.SENT.getStatus());
                        friend.listFriends.add(yourData);
                        you.listFriends.add(friendData);
                        i.remove();
                        friend.saveToDB(zone);
                        count--;
                    }
                }
                break;
            }
        }
        you.saveToDB(zone);
        SendAcceptAllRequest send = new SendAcceptAllRequest();
        friendHandler.send(send, user);
    }

    public void searchingUser(User user, BaseExtension extension, String key) {
        UserModel player = ((ZoneExtension) user.getZone().getExtension()).getUserManager().getUserModelByKey(key);
        UserModel um = extension.getUserManager().getUserModel(user);
        if (player == null || um.displayName.equals(player.displayName)) {
            SendSearchingUser send = new SendSearchingUser(ServerConstant.ErrorCode.ERR_USER_NOT_EXIST);
            friendHandler.send(send, user);
            return;
        }

        BlockedFriendVO blockedFriendVO = new BlockedFriendVO(player.userID, player.avatar, BagManager.getInstance().getLevelUser(player.userID, user.getZone()), player.displayName, HeroManager.getInstance().getPower(player.userID, user.getZone()), player.avatarFrame);
        List<BlockedFriendVO> list = new ArrayList<>();
        list.add(blockedFriendVO);
        SendSearchingUser send = new SendSearchingUser();
        send.list = list;
        friendHandler.send(send, user);
    }

    public void showInfoDetailFriend(User user, BaseExtension extension, long uid) {
        UserModel um = extension.getUserManager().getUserModel(user);
        UserModel player = extension.getUserManager().getUserModel(uid);
//        GuildManagerModel.copyFromDBtoObject(zone);
        GuildModel guildModel = GuildManager.getInstance().getGuildModelByUserID(player.userID, user.getZone());
        FriendInfoFullVO friendInfoFullVO = new FriendInfoFullVO();
        if (guildModel != null) {
            friendInfoFullVO.gAvatar = guildModel.readAvatar();
            friendInfoFullVO.gName = guildModel.gName;
        }
        friendInfoFullVO.statusBlock = checkBlock(um, player, user.getZone());
        friendInfoFullVO.uid = player.userID;
        friendInfoFullVO.server = player.serverId;
        friendInfoFullVO.name = player.displayName;
        friendInfoFullVO.statusText = player.statusText;
        friendInfoFullVO.active = checkActiveOrNot(player, ExtensionUtility.getInstance().getUserById(player.userID));
        friendInfoFullVO.campaign = UserCampaignDetailModel.copyFromDBtoObject(player.userID, user.getZone()).userMainCampaignDetail.readNextStation();
        friendInfoFullVO.power = HeroManager.getInstance().getPower(player.userID, user.getZone());
        ;
        friendInfoFullVO.avatar = player.avatar;
        friendInfoFullVO.level = BagManager.getInstance().getLevelUser(player.userID, user.getZone());
        friendInfoFullVO.avatarFrame = player.avatarFrame;
        friendInfoFullVO.gender = player.gender;
        friendInfoFullVO.listHero = HeroManager.getInstance().getTeamStrongestUserHeroModel(player.userID, user.getZone());
        SendShowInfoDetailFriend send = new SendShowInfoDetailFriend();
        send.vo = friendInfoFullVO;
        friendHandler.send(send, user);
    }

    private boolean checkBlock(UserModel um, UserModel player, Zone zone) {
        FriendModel friendModel = getFriendModel(um.userID, zone);
        for (FriendDataVO friendDataVO : friendModel.listBlock) {
            if (friendDataVO.uid == player.userID) {
                return true;
            }
        }
        return false;
    }

    /**
     * refresh time in next time
     */
    public void refreshListFriend(FriendModel friendModel, Zone zone) {
        long newTime = GrandOpeningCheckInManager.getInstance().getNewTime();
        if (friendModel.time % newTime != 0) {
            if (friendModel.listFriends.size() != 0) {
                for (FriendDataVO friendDataVO : friendModel.listFriends) {
                    friendDataVO.send = EStatusHeartPoint.UNSENT.getStatus();
                }
                friendModel.points = 0;
                friendModel.time = newTime;
                friendModel.saveToDB(zone);
            }
        }
    }

    public void suggestAddFriend(User user, BaseExtension extension) {
        UserModel um = extension.getUserManager().getUserModel(user);
        FriendModel player = getFriendModel(um.userID, user.getZone());
        List<BlockedFriendVO> listSuggest = getListSuggest(extension, user, player);
        SendSuggestAddFriend send = new SendSuggestAddFriend();
        send.list = listSuggest;
        friendHandler.send(send, user);
    }

    //Just have id user
    private List<BlockedFriendVO> getListSuggest(BaseExtension extension, User user, FriendModel you) {
        List<BlockedFriendVO> listSuggest = new ArrayList<>();

        //them bot vao neu chua co ban
        if (you.listFriends.isEmpty()) {
            UserModel userModel = zoneExtension.getBotManager().getOne();
            if (userModel != null) {
                BlockedFriendVO blockedFriendVO = new BlockedFriendVO(userModel.userID, userModel.avatar, BagManager.getInstance().getLevelUser(userModel.userID, user.getZone()), userModel.displayName, HeroManager.getInstance().getPower(userModel.userID, zone), userModel.avatarFrame);
                listSuggest.add(blockedFriendVO);
            }
        }

        //ngẫu nhiên người chơi login gần nhất
        //loại bỏ những người chưa có tên hiển thị
        List<Long> listId = extension.getUserManager().getLastLoginUser(5);
        List<Long> removeList = new ArrayList<>();
        for (long id : listId) {
            if (zoneExtension.getUserManager().getUserModel(id).displayName.isEmpty()) {
                removeList.add(id);
            }
        }
        if (!removeList.isEmpty()) {
            listId.removeAll(removeList);
        }

        //loại bỏ những người đã là bạn hoặc đang bị block hoặc đã gửi lời mời
        outerloop:
        for (Long friendID : listId) {
            if (friendID == you.uid) continue; //loại bỏ chính mình
            if (you.haveFriend(friendID)) continue;   // đã là bạn
            if (you.haveBlock(friendID)) continue;     //  đã block mình

            //loại bỏ những người đã gửi kết bạn cho mình hoặc đã block mình
            FriendModel fFriendModel = FriendManager.getInstance().getFriendModel(friendID, zone);
            if (fFriendModel == null) continue;
            if (fFriendModel.haveFriend(you.uid)) continue;   //đã là bạn
            if (fFriendModel.haveRequestFromUser(you.uid)) continue;   //mình đã gửi lời mời rồi
            if (fFriendModel.haveBlock(you.uid)) continue;     //mình đã bị họ block

            UserModel friend = extension.getUserManager().getUserModel(friendID);
            if (friend == null) continue;
            BlockedFriendVO blockedFriendVO = new BlockedFriendVO(friend.userID, friend.avatar, BagManager.getInstance().getLevelUser(friend.userID, user.getZone()), friend.displayName, HeroManager.getInstance().getPower(friend.userID, user.getZone()), friend.avatarFrame);
            listSuggest.add(blockedFriendVO);
        }

        return listSuggest;
    }

    public void addAllFriendInSuggestList(User user, BaseExtension extension, Collection<Long> ids) {
        UserModel um = extension.getUserManager().getUserModel(user);
        for (Long fid : ids) {
            addFriend(user, extension, fid, false);
        }
        SendAddAllFriendSuggest send = new SendAddAllFriendSuggest();
        friendHandler.send(send, user);
    }

    public void addFriend(User user, BaseExtension extension, long uid, boolean sendToClient) {
        UserModel userModel = extension.getUserManager().getUserModel(user);
        FriendModel you = getFriendModel(userModel.userID, user.getZone());
        FriendModel friend = getFriendModel(uid, zone);

        //gửi cho chính mình
        if (userModel.userID == uid) {
            return;
        }

        //Check size in list friend
        if (you.listFriends.size() >= getFriendConfig().friends) {
            if (sendToClient) {
                SendAddFriend send = new SendAddFriend(ServerConstant.ErrorCode.ERR_MAX_LIST_FRIENDS);
                friendHandler.send(send, user);
            }
            return;
        }

        //Check size in list requests of player, who you wanna add
        if (friend.listRequest.size() >= getFriendConfig().requests) {
            if (sendToClient) {
                SendAddFriend send = new SendAddFriend(ServerConstant.ErrorCode.ERR_MAX_LIST_FRIENDS_OF_FRIEND);
                friendHandler.send(send, user);
            }
            return;
        }

        //Check in list block of friend
        if (friend.haveBlock(you.uid) || you.haveBlock(friend.uid)) {
            if (sendToClient) {
                SendAddFriend send = new SendAddFriend(ServerConstant.ErrorCode.ERR_USER_BLOCKED);
                friendHandler.send(send, user);
            }
            return;
        }

        //2 người đã là bạn của nhau
        if (you.haveFriend(friend.uid)) {
            if (sendToClient) {
                SendAddFriend send = new SendAddFriend(ServerConstant.ErrorCode.ERR_FRIEND_EXIST);
                friendHandler.send(send, user);
            }
            return;
        }

        //check đã gửi lời mời rồi
        if (friend.haveRequestFromUser(you.uid)) {
            if (sendToClient) {
                SendAddFriend send = new SendAddFriend(ServerConstant.ErrorCode.ERR_SENT_REQUEST);
                friendHandler.send(send, user);
            }
            return;
        }

        //2 người cùng gửi lời mời cho nhau
        if (you.haveRequestFromUser(friend.uid)) {
            FriendDataVO friendDataVO = new FriendDataVO(friend.uid, EStatusHeartPoint.UNSENT.getStatus(), EStatusHeartPoint.SENT.getStatus());
            you.listFriends.add(friendDataVO);
            you.listRequest.remove(friend.uid);

            FriendDataVO youDataVO = new FriendDataVO(you.uid, EStatusHeartPoint.UNSENT.getStatus(), EStatusHeartPoint.SENT.getStatus());
            friend.listFriends.add(youDataVO);

            you.saveToDB(zone);
            friend.saveToDB(zone);

            if (sendToClient) {
                SendAddFriend send = new SendAddFriend();
                friendHandler.send(send, user);
            }
            return;
        }

        friend.listRequest.add(userModel.userID);
        friend.saveToDB(zone);

        if (sendToClient) {
            SendAddFriend send = new SendAddFriend();
            friendHandler.send(send, user);
        }

        //Event
        Map<String, Object> eventData = new HashMap<>();
        eventData.put(Params.UIDS, Arrays.asList(uid));
        GameEventAPI.ariseGameEvent(EGameEvent.SEND_FRIEND_REQUEST, userModel.userID, eventData, zone);

        //bot sẽ tự accept
        if (((ZoneExtension) extension).getBotManager().isBot(uid)) {
            acceptOneRequest(null, zoneExtension, you.uid, uid);
        }
    }

    //===========Using for red nodes==============

    public boolean checkRedNodeNewPoint(long uid, Zone zone) {
        FriendModel friendModel = getFriendModel(uid, zone);
        for (FriendDataVO friendDataVO : friendModel.listFriends) {
            if (friendDataVO.receive == EStatusHeartPoint.UNSENT.getStatus() || friendDataVO.send == EStatusHeartPoint.UNSENT.getStatus()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkRedNodeHaveRequest(long uid, Zone zone) {
        FriendModel friendModel = getFriendModel(uid, zone);
        if (friendModel.listRequest.isEmpty()) {
            return false;
        }
        return true;
    }
    //============================================
}
