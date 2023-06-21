package com.bamisu.log.gameserver.module.friends;

import com.bamisu.log.gameserver.module.friends.cmd.receive.*;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.friends.cmd.send.SendSuggestAddFriend;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

public class FriendHandler extends ExtensionBaseClientRequestHandler {
    FriendManager friendManager;

    public FriendHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_FRIEND;
        this.friendManager = new FriendManager(this);
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_SHOW_INFO_LIST_FRIENDS:
                handleShowInfoListFriends(user, data);
                break;
            case CMD.CMD_SEND_POINT_TO_ONE_USER:
                handleSendPointToOneUser(user, data);
                break;
            case CMD.CMD_RECEIVE_POINT_FROM_ONE_USER:
                handleReceivePointFromOneUser(user, data);
                break;
            case CMD.CMD_RECEIVE_AND_SEND_ALL_USER:
                handleReceiveAndSendAllUser(user, data);
                break;
            case CMD.CMD_LIST_FRIENDS_BLOCKED:
                handleListFriendsBlocked(user, data);
                break;
            case CMD.CMD_RESTORE_BLOCKED_FRIEND:
                handleRestoreBlockedFriend(user, data);
                break;
            case CMD.CMD_BLOCK_FRIEND:
                handleBlockFriend(user, data);
                break;
            case CMD.CMD_DELETE_FRIEND:
                handleDeleteFriend(user, data);
                break;
            case CMD.CMD_SHOW_LIST_REQUEST_ADD_FRIEND:
                handleShowListRequestAddFriend(user, data);
                break;
            case CMD.CMD_DELETE_ALL_REQUEST:
                handleDeleteAllRequest(user, data);
                break;
            case CMD.CMD_DELETE_ONE_REQUEST:
                handleDeleteOneRequest(user, data);
                break;
            case CMD.CMD_ACCEPT_ONE_REQUEST:
                handleAcceptOneRequest(user, data);
                break;
            case CMD.CMD_ACCEPT_ALL_REQUEST:
                handleAcceptAllRequest(user, data);
                break;
            case CMD.CMD_SEARCHING_USER:
                handleSearchingUser(user, data);
                break;
            case CMD.CMD_ADD_FRIEND:
                handleAddFriend(user, data);
                break;
            case CMD.CMD_SHOW_INFO_DETAIL_FRIEND:
                handleShowInfoDetailFriend(user, data);
                break;
            case CMD.CMD_SUGGEST_ADD_FRIEND:
                handleSuggestAddFriend(user, data);
                break;
            case CMD.CMD_ADD_ALL_FRIEND_IN_SUGGEST:
                handleAddAllFriendInSuggest(user, data);
                break;
        }
    }

    @WithSpan
    private void handleAddAllFriendInSuggest(User user, ISFSObject data) {
        RecAddAllFriendInSuggest rec = new RecAddAllFriendInSuggest(data);
        rec.unpackData();
        friendManager.addAllFriendInSuggestList(user, extension, rec.ids);
    }

    @WithSpan
    private void handleSuggestAddFriend(User user, ISFSObject data) {
        RecSuggestAddFriend rec = new RecSuggestAddFriend(data);
        rec.unpackData();
        friendManager.suggestAddFriend(user, extension);
    }

    @WithSpan
    private void handleShowInfoDetailFriend(User user, ISFSObject data) {
        RecShowInfoDetailFriend rec = new RecShowInfoDetailFriend(data);
        rec.unpackData();
        friendManager.showInfoDetailFriend(user, extension, rec.uid);
    }

    @WithSpan
    private void handleAddFriend(User user, ISFSObject data) {
        RecAddFriend rec = new RecAddFriend(data);
        rec.unpackData();
        friendManager.addFriend(user, extension, rec.uid, true);
    }

    @WithSpan
    private void handleSearchingUser(User user, ISFSObject data) {
        RecSearchingUser rec = new RecSearchingUser(data);
        rec.unpackData();
        friendManager.searchingUser(user, extension, rec.key);
    }

    @WithSpan
    private void handleAcceptAllRequest(User user, ISFSObject data) {
        RecAcceptAllRequest rec = new RecAcceptAllRequest(data);
        rec.unpackData();
        friendManager.acceptAllRequest(user, extension);
    }

    @WithSpan
    private void handleAcceptOneRequest(User user, ISFSObject data) {
        RecAcceptOneRequest rec = new RecAcceptOneRequest(data);
        rec.unpackData();
        friendManager.acceptOneRequest(user, extension, rec.uid, -1);
    }

    @WithSpan
    private void handleDeleteOneRequest(User user, ISFSObject data) {
        RecDeleteOneRequest rec = new RecDeleteOneRequest(data);
        rec.unpackData();
        friendManager.deleteOneRequest(user, extension, rec.uid);
    }

    @WithSpan
    private void handleDeleteAllRequest(User user, ISFSObject data) {
        RecDeleteAllRequest rec = new RecDeleteAllRequest(data);
        rec.unpackData();
        friendManager.deleteAllRequest(user, extension);
    }

    @WithSpan
    private void handleShowListRequestAddFriend(User user, ISFSObject data) {
        RecShowListRequestAddFriend rec = new RecShowListRequestAddFriend(data);
        rec.unpackData();
        friendManager.listRequestAddFriend(user, extension);
    }

    @WithSpan
    private void handleDeleteFriend(User user, ISFSObject data) {
        RecDeleteFriend rec = new RecDeleteFriend(data);
        rec.unpackData();
        friendManager.deleteFriend(user, extension, rec.uid);
    }

    @WithSpan
    private void handleBlockFriend(User user, ISFSObject data) {
        RecBlockFriend rec = new RecBlockFriend(data);
        rec.unpackData();
        friendManager.blockFriend(user, extension, rec.uid);

    }

    @WithSpan
    private void handleRestoreBlockedFriend(User user, ISFSObject data) {
        RecRestoreBlockedFriend rec = new RecRestoreBlockedFriend(data);
        rec.unpackData();
        friendManager.restoreBlockedFriend(user, extension, rec.uid);
    }

    @WithSpan
    private void handleListFriendsBlocked(User user, ISFSObject data) {
        RecListFriendsBlocked rec = new RecListFriendsBlocked(data);
        rec.unpackData();
        friendManager.listFriendsBlocked(user, extension);
    }

    @WithSpan
    private void handleReceiveAndSendAllUser(User user, ISFSObject data) {
        RecReceiveAndSendAllUser rec = new RecReceiveAndSendAllUser(data);
        rec.unpackData();
        friendManager.receiveAndSendAllUser(user, extension);
    }

    @WithSpan
    private void handleReceivePointFromOneUser(User user, ISFSObject data) {
        RecReceivePointFromOneUser rec = new RecReceivePointFromOneUser(data);
        rec.unpackData();
        friendManager.receivePointFromOneUser(user, extension, rec.uid);
    }

    @WithSpan
    private void handleSendPointToOneUser(User user, ISFSObject data) {
        RecSendPointToOneUser rec = new RecSendPointToOneUser(data);
        rec.unpackData();
        friendManager.sendPointToOneUser(user, extension, rec.uid);
    }

    @WithSpan
    private void handleShowInfoListFriends(User user, ISFSObject data) {
        RecShowInfoListFriends rec = new RecShowInfoListFriends(data);
        rec.unpackData();
        long uid = extension.getUserManager().getUserModel(user).userID;
        friendManager.getListFriends(user, uid, extension);
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_FRIEND, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_FRIEND, this);
    }
}
