package com.bamisu.log.gameserver.module.chat;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.log.gameserver.module.chat.cmd.rec.RecRemoveAllMessageUser;
import com.bamisu.log.gameserver.module.chat.cmd.send.SendRemoveAllMessageUser;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatManagerHandler extends ExtensionBaseClientRequestHandler {

    private Set<Long> userInChat = new HashSet<>();
    private boolean addUserToRoomChat(long uid){
        userInChat.add(uid);
        return true;
    }
    private boolean removeUserFromChatRoom(long uid){
        userInChat.remove(uid);
        return true;
    }
    private boolean userInChat(long uid){
        return userInChat.contains(uid);
    }
    private Set<Long> getListUserInChat(){
        return userInChat;
    }



    public ChatManagerHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_CHAT;
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_REMOVE_ALL_MESSAGE_USER:
                doRemoveAllMessageUser(user, data);
                break;
        }
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_CHAT, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_CHAT, this);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doRemoveAllMessageUser(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecRemoveAllMessageUser objGet = new RecRemoveAllMessageUser(data);
        if(!ChatManager.getInstance().deleteMessagePrivateChat(uid, objGet.uid, getParentExtension().getParentZone())){
            SendRemoveAllMessageUser objPut = new SendRemoveAllMessageUser(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendRemoveAllMessageUser objPut = new SendRemoveAllMessageUser();
        objPut.uid = objGet.uid;
        send(objPut, user);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * them user trong room chat
     * @param rec
     * @return
     */
    @WithSpan
    public ISFSObject AddUserToChat(ISFSObject rec){
        long uid = rec.getLong(Params.UID);
        //Add info user chat
        addUserToRoomChat(uid);

        return new SFSObject();
    }

    /**
     * xoa user trong room chat
     * @param rec
     * @return
     */
    @WithSpan
    public ISFSObject RemoveUserToChat(ISFSObject rec){
        long uid = rec.getLong(Params.UID);
        //Xoa info user chat
        removeUserFromChatRoom(uid);

        return new SFSObject();
    }

    /**
     * kiem tra user trong room chat
     * @param rec
     * @return
     */
    @WithSpan
    public ISFSObject IsUserInChat(ISFSObject rec){
        long uid = rec.getLong(Params.UID);
        //Xoa info user chat
        boolean check = userInChat(uid);

        //Dong goi
        ISFSObject data = new SFSObject();
        data.putBool(Params.DATA, check);

        return data;
    }

    /**
     * kiem tra user trong room chat
     * @param rec
     * @return
     */
    @WithSpan
    public ISFSObject ListUserInChat(ISFSObject rec){
        //Xoa info user chat
        List<Long> list = new ArrayList<>(getListUserInChat());

        //Dong goi
        ISFSObject data = new SFSObject();
        data.putLongArray(Params.DATA, list);

        return data;
    }
}
