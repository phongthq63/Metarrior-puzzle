package com.bamisu.log.sdk.module.sdkthriftserver.handler.invitecode;

import com.bamisu.log.sdk.module.invitecode.InviteManager;
import com.bamisu.log.sdk.module.invitecode.entities.UpdateRewardDetail;
import com.bamisu.log.sdk.module.invitecode.model.UserInviteModel;
import com.bamisu.log.sdkthrift.exception.SDKThriftError;
import com.bamisu.log.sdkthrift.exception.ThriftSVException;
import com.bamisu.log.sdkthrift.service.invitecode.InviteService;
import com.bamisu.gamelib.utils.Utils;
import org.apache.thrift.TException;

import java.util.List;

public class TInviteHandler implements InviteService.Iface {

    @Override
    public boolean haveInputInviteCode(String accountID) throws ThriftSVException, TException {
        return InviteManager.getInstance().haveExsistAccountID(accountID) && InviteManager.getInstance().haveInputInviteCode(accountID);
    }

    @Override
    public boolean haveExsistInviteCode(String inviteCode) throws ThriftSVException, TException {
        return InviteManager.getInstance().haveExsistInviteCode(inviteCode);
    }

    @Override
    public String inputInviteCode(String accountID, String inviteCode) throws ThriftSVException, TException {
        if(haveInputInviteCode(accountID)){
            throw SDKThriftError.HAVE_INPUT_INVITE_CODE;
        }
        UserInviteModel userInviteModel = InviteManager.getInstance().inputCodeUserInviteModel(accountID, inviteCode);
        return Utils.toJson(userInviteModel);
    }

    @Override
    public String getUserInviteModel(String accountID) throws ThriftSVException, TException {
        UserInviteModel userInviteModel = InviteManager.getInstance().getUserInviteModelByID(accountID);
        return Utils.toJson(userInviteModel);
    }

    @Override
    public boolean canRewardInviteBonus(String accountID, String idBonus, int point) throws ThriftSVException, TException {
        return InviteManager.getInstance().canRewardBonusInvite(accountID, idBonus, point);
    }

    @Override
    public boolean rewardInviteBonus(String accountID, String idBonus, int point) throws ThriftSVException, TException {
        return InviteManager.getInstance().rewardBonusInvite(accountID, idBonus, point);
    }

    @Override
    public boolean updateRewardInviteDetail(String accountID, String jsonUpdate) throws ThriftSVException, TException {
        List<UpdateRewardDetail> update = Utils.fromJsonList(jsonUpdate, UpdateRewardDetail.class);
        return InviteManager.getInstance().updateRewardInviteDetail(accountID, update);
    }
}
