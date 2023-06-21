package com.bamisu.log.gameserver.module.invite;

import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.EHeroType;
import com.bamisu.log.gameserver.module.invite.entities.InviteBonusDetail;
import com.bamisu.log.gameserver.module.invite.entities.UserInvite;
import com.bamisu.log.gameserver.module.invite.cmd.rec.RecInputInviteCode;
import com.bamisu.log.gameserver.module.invite.cmd.rec.RecRewardInviteCode;
import com.bamisu.log.gameserver.module.invite.cmd.send.SendInputInviteCode;
import com.bamisu.log.gameserver.module.invite.cmd.send.SendLoadSceneInvite;
import com.bamisu.log.gameserver.module.invite.cmd.send.SendRewardInviteCode;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.log.sdkthrift.exception.ThriftSVException;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.thrift.TException;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class InviteHandler extends ExtensionBaseClientRequestHandler {

    public InviteHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_INVITE;
        new InviteGameEventHandler(extension.getParentZone());
    }

    public UserModel getUserModel(long uid){
        return extension.getUserManager().getUserModel(uid);
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_INPUT_INVITE_CODE:
                doInputInviteCode(user, data);
                break;
            case CMD.CMD_LOAD_SCENE_INVITE_CODE:
                doLoadSceneInvite(user, data);
                break;
            case CMD.CMD_GET_REWARD_INVITE_CODE:
                doRewardInviteCode(user, data);
                break;
        }
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_INVITE, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_INVITE, this);
    }




    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Nguoi choi nhap code -> tien hanh tao model
     * @param user
     * @param data
     */
    @WithSpan
    private void doInputInviteCode(User user, ISFSObject data){
        UserModel userModel = extension.getUserManager().getUserModel(user);
        RecInputInviteCode objGet = new RecInputInviteCode(data);
        String code = (objGet.code != null) ? objGet.code : "";

        //Kiem tra da tung nhap code chua
        if(InviteManager.getInstance().haveInputInviteCode(getParentExtension().getParentZone(), userModel.userID)){
            SendInputInviteCode objPut = new SendInputInviteCode(ServerConstant.ErrorCode.ERR_ALREADY_INPUT_INVITE_CODE);
            send(objPut, user);
            return;
        }

        //Neu nhap code -> format code
        if(code.isEmpty()){
            SendInputInviteCode objPut = new SendInputInviteCode(ServerConstant.ErrorCode.ERR_INVALID_INVITE_CODE);
            send(objPut, user);
            return;
        }

        //Kiem tra co ton tai code khong
        if(!InviteManager.getInstance().haveExsistInviteCode(code)){
            SendInputInviteCode objPut = new SendInputInviteCode(ServerConstant.ErrorCode.ERR_INVITE_CODE_NOT_EXSIST);
            send(objPut, user);
            return;
        }

        //Nhan bonus khi nhap code
        if(!BagManager.getInstance().addItemToDB(
                InviteManager.getInstance().getRewardInvitedConfig(),
                userModel.userID,
                getParentExtension().getParentZone(), UserUtils.TransactionType.INVITE_CODE)){
            SendInputInviteCode objPut = new SendInputInviteCode(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Khong nhap code -> tao model
        try{
            InviteManager.getInstance().inputCodeUserInviteModel(getParentExtension().getParentZone(), userModel.userID, code);
        }catch (TException e){
            e.printStackTrace();
            ThriftSVException thriftSVException = (ThriftSVException) e;
            SendInputInviteCode objPut = new SendInputInviteCode((short) thriftSVException.errorCode);
            send(objPut, user);
            return;
        }

        SendInputInviteCode objPut = new SendInputInviteCode();
        send(objPut, user);
    }

    /**
     * Load scene bonus invite code
     * @param user
     * @param data
     */
    @WithSpan
    private void doLoadSceneInvite(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        UserInvite userInvite = InviteManager.getInstance().getUserInviteModel(getParentExtension().getParentZone(), uid);
        if(userInvite == null){
            SendLoadSceneInvite objPut = new SendLoadSceneInvite(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

//        List<HeroModel> heroModels = HeroManager.getInstance().getAllHeroModel(uid, getParentExtension().getParentZone());
//        long countNFT = heroModels.stream()
//                .filter(heroModel -> Objects.equals(heroModel.type, EHeroType.NFT.getId()))
//                .count();
//        for (InviteBonusDetail inviteBonusDetail : userInvite.inviteBonus) {
//            if (Objects.equals(inviteBonusDetail.id, "Ultimate")) {
//                if (countNFT < 1) {
//                    inviteBonusDetail.point = inviteBonusDetail.complete.stream().max(Comparator.naturalOrder()).orElse((short) 0);
//                }
//            }
//        }

        SendLoadSceneInvite objPut = new SendLoadSceneInvite();
        objPut.code = userInvite.inviteCode;
        objPut.list = userInvite.inviteBonus;
        send(objPut, user);
    }


    /**
     * Get reward Invite Code
     * @param user
     * @param data
     */
    @WithSpan
    private void doRewardInviteCode(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecRewardInviteCode objGet = new RecRewardInviteCode(data);

        //Kiem tra co the nhan qua khong
        if(!InviteManager.getInstance().canRewardInviteBonus(getParentExtension().getParentZone(), uid, objGet.id, objGet.point)){
            SendRewardInviteCode objPut = new SendRewardInviteCode(ServerConstant.ErrorCode.ERR_INVALID_REWARD_INVITE_CODE);
            send(objPut, user);
            return;
        }

        //Them vao bag
        if(!BagManager.getInstance().addItemToDB(
                InviteManager.getInstance().getRewardBonusInvite(objGet.id, objGet.point),
                uid,
                getParentExtension().getParentZone(), UserUtils.TransactionType.INVITE_CODE)){
            SendRewardInviteCode objPut = new SendRewardInviteCode(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        //Save data
        if(!InviteManager.getInstance().rewardInviteBonus(getParentExtension().getParentZone(), uid, objGet.id, objGet.point)){
            SendRewardInviteCode objPut = new SendRewardInviteCode(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendRewardInviteCode objPut = new SendRewardInviteCode();
        send(objPut, user);
    }
}
