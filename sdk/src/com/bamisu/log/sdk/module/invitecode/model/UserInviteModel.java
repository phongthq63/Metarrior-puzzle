package com.bamisu.log.sdk.module.invitecode.model;

import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.log.sdk.module.invitecode.model.defind.EBonusInvite;
import com.bamisu.log.sdk.module.invitecode.model.entities.InviteBonusDetail;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInviteModel extends DataModel {

    public String accountID;        //acc cua minh
    public String accountIDInvite;      //account nguoi khac
    public boolean haveInput;       //nhap chua
    public String inviteCode;       //code cua minh
    public Map<String, Map<EBonusInvite, Boolean>> mapInvited = new HashMap<>();      //   Uid ---- Link Account
    public List<InviteBonusDetail> inviteBonus = new ArrayList<>();

    private final Object lockCode = new Object();
    private final Object lockInvited = new Object();
    private final Object lockBonusInvite = new Object();


    public static UserInviteModel createUserInviteModel(String accountID, String accountIDInvite, String inviteCode, SDKDatacontroler sdkDatacontroler) {
        UserInviteModel userInviteModel = new UserInviteModel();
        userInviteModel.accountID = accountID;
        userInviteModel.accountIDInvite = accountIDInvite;
        userInviteModel.haveInput = (accountIDInvite != null && !accountIDInvite.isEmpty());
        //Model invite code
        userInviteModel.inviteCode = inviteCode;
        InviteCodeModel.create(userInviteModel, sdkDatacontroler);
        //Save
        userInviteModel.saveToDB(sdkDatacontroler);

        return userInviteModel;
    }

    public final boolean saveToDB(SDKDatacontroler sdkDatacontroler) {
        try {
            this.saveModel(this.accountID, sdkDatacontroler.getController());
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static UserInviteModel copyFromDBtoObject(String accountID, SDKDatacontroler sdkDatacontroler) {
        UserInviteModel pInfo = null;
        try {
            String str = (String) getModel(accountID, UserInviteModel.class, sdkDatacontroler.getController());
            if (str != null) {
                pInfo = Utils.fromJson(str, UserInviteModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }


    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    public boolean haveInputCode() {
        return haveInput;
    }

    public boolean updateCode(String newCode, SDKDatacontroler sdkDatacontroler){
        synchronized (lockCode){
            this.inviteCode = newCode;
            return saveToDB(sdkDatacontroler);
        }
    }

    public boolean inputCode(String accountIDInvite, SDKDatacontroler sdkDatacontroler) {
        synchronized (lockCode){
            if (haveInput) return false;
            haveInput = true;
            this.accountIDInvite = accountIDInvite;
            return saveToDB(sdkDatacontroler);
        }
    }

    public void addInvited(String accountID, boolean linked) {
        synchronized (lockInvited) {
            updateRewardInvite(accountID, EBonusInvite.INVITE.getId(), 1);
            if (linked) updateRewardInvite(accountID, EBonusInvite.INVITE_LINK.getId(), 1);
        }
    }

    public void updateRewardInvite(String accountID, String id, int count) {
        synchronized (lockInvited) {
            switch (EBonusInvite.fromID(id)) {
                case INVITE:
                    if (mapInvited.containsKey(accountID)) return;
                    mapInvited.put(accountID, new HashMap<>());
                    break;
                case INVITE_LINK:
                    if (mapInvited.getOrDefault(accountID, new HashMap<>()).getOrDefault(EBonusInvite.INVITE_LINK, false))
                        return;
                    mapInvited.getOrDefault(accountID, new HashMap<>()).put(EBonusInvite.INVITE_LINK, true);
                    break;
                case INVITE_LEVEL_50:
                    if (mapInvited.getOrDefault(accountID, new HashMap<>()).getOrDefault(EBonusInvite.INVITE_LEVEL_50, false))
                        return;
                    mapInvited.getOrDefault(accountID, new HashMap<>()).put(EBonusInvite.INVITE_LEVEL_50, true);
                    break;
            }
        }
        synchronized (lockBonusInvite) {
            for (InviteBonusDetail index : inviteBonus) {
                if (index.id.equals(id)) {
                    index.point += count;
                    return;
                }
            }
            inviteBonus.add(InviteBonusDetail.create(id, count));
        }
    }

    public boolean canRewardInviteBonus(String id, int point) {
        synchronized (lockBonusInvite) {
            for (InviteBonusDetail index : inviteBonus) {
                if (index.id.equals(id)) {
                    if (point > index.point) return false;
                    return !index.complete.contains((short) point);
                }
            }
        }
        return false;
    }

    /**
     * Save phan thuong da nhan vao set
     * Phai kiem tra truoc
     *
     * @param id
     * @param point
     */
    public void rewardInviteBonus(String id, int point) {
        synchronized (lockBonusInvite) {
            for (InviteBonusDetail index : inviteBonus) {
                if (index.id.equals(id)) {
                    index.complete.add((short) point);
                }
            }
        }
    }
}
