package com.bamisu.log.sdk.module.invitecode;

import com.bamisu.gamelib.sql.sdk.dao.AccountDAO;
import com.bamisu.gamelib.sql.sdk.dbo.AccountDBO;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.sdk.module.invitecode.model.InviteCodeModel;
import com.bamisu.log.sdk.module.sql.SDKsqlManager;
import com.bamisu.log.sdk.module.account.model.AccountLinkedModel;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.log.sdk.module.invitecode.entities.UpdateRewardDetail;
import com.bamisu.log.sdk.module.invitecode.model.UserInviteModel;
import com.bamisu.gamelib.task.LizThreadManager;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class InviteManager {
    private ExecutorService executor;
    private final Object lockCode = new Object();

    private static InviteManager ourInstance = new InviteManager();

    public static InviteManager getInstance() {
        return ourInstance;
    }

    private InviteManager() {
        executor = LizThreadManager.getInstance().getFixExecutorServiceByName("invite", 1);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /*------------------------------------------------   MANAGER  ----------------------------------------------------*/
    /**
     * Tao model ------- TH xay ra khi bat dau nhap code
     * //
     * @param accountID
     * @param inviteCode
     * @return
     */
    public synchronized UserInviteModel createUserInviteModel(String accountID, String inviteCode) {
        //Gen code
        String code = genInviteCode();
        //Nguoi moi
        UserInviteModel userInvite = getUserInviteModelByCode(inviteCode);
        String accountIDInvite = (userInvite != null) ? userInvite.accountID : "";
        //Tao model
        UserInviteModel userInvited = UserInviteModel.createUserInviteModel(accountID, accountIDInvite, code, SDKDatacontroler.getInstance());
        //Add vao model khac neu co
        if(!accountIDInvite.isEmpty()) executor.execute(() -> addInvitedUserInviteModel(accountID, inviteCode));

        return userInvited;
    }

    private String genInviteCode(){
        String code = Utils.genInviteCode();
        synchronized (lockCode){
            while (haveExsistInviteCode(code)){
                code = Utils.genInviteCode();
            }
        }
        return code;
    }

    public boolean haveExsistInviteCode(String code) {
        InviteCodeModel inviteCodeModel = getInviteCodeModel(code);
        return inviteCodeModel != null && !inviteCodeModel.accountID.isEmpty();
    }

    public boolean haveExsistAccountID(String accountID) {
        return getUserInviteModelByID(accountID) != null;
    }

    public InviteCodeModel getInviteCodeModel(String inviteCode){
        return InviteCodeModel.copyFromDBtoObject(inviteCode, SDKDatacontroler.getInstance());
    }

    public synchronized boolean updateInviteCodeUser(String codeOld, String codeNew){
        if(haveExsistInviteCode(codeNew)) return false;

        InviteCodeModel inviteCodeModel = getInviteCodeModel(codeOld);
        if(inviteCodeModel == null) return false;
        UserInviteModel userInviteModel = getUserInviteModelByID(inviteCodeModel.accountID);

        return AccountDAO.updateUserRefrralCode(SDKsqlManager.getInstance().getSqlController(), codeOld, codeNew) &&
                inviteCodeModel.updateCode(codeNew, SDKDatacontroler.getInstance()) &&
                userInviteModel.updateCode(codeNew, SDKDatacontroler.getInstance());
    }


    /*--------------------------------------------------   USER  -----------------------------------------------------*/

    /**
     * User Invite Mode --- Neu bo qua nhap code ---> Coi nhu da nhap """(rong)
     *
     * @param accountID
     * @return
     */
    public UserInviteModel getUserInviteModelByID(String accountID) {
        UserInviteModel userInviteModel = UserInviteModel.copyFromDBtoObject(accountID, SDKDatacontroler.getInstance());
        if (userInviteModel == null) {
            userInviteModel = createUserInviteModel(accountID, "");
        }
        return userInviteModel;
    }

    public UserInviteModel getUserInviteModelByCode(String code) {
        InviteCodeModel inviteCodeModel = getInviteCodeModel(code);
        if(inviteCodeModel == null || inviteCodeModel.accountID.isEmpty()){
            return null;
        }
        return getUserInviteModelByID(inviteCodeModel.accountID);
    }

    public boolean haveInputInviteCode(String accountID) {
        return getUserInviteModelByID(accountID).haveInputCode();
    }

    /**
     * nhap invite code
     *
     * @param accountID
     * @param code
     * @return
     */
    public UserInviteModel inputCodeUserInviteModel(String accountID, String code) {
        UserInviteModel invited = getUserInviteModelByID(accountID);
        UserInviteModel invite = getUserInviteModelByCode(code);
        if (invite != null) {
            //Luu nguoi moi
            invited.inputCode(invite.accountID, SDKDatacontroler.getInstance());
            //Add vao model khac neu co nguoi moi
            executor.execute(() -> addInvitedUserInviteModel(accountID, code));
            //luu sql
            AccountDBO accountDBO = AccountDAO.get(SDKsqlManager.getInstance().getSqlController(), invite.accountID);
            if (accountDBO != null) {
                accountDBO.presenter = code;
                AccountDAO.save(SDKsqlManager.getInstance().getSqlController(), accountDBO);
            }
        }

        return invited;
    }

    /**
     * Luu nguoi dc moi vao model cua minh
     *
     * @param invited
     * @param codeInvite
     */
    private void addInvitedUserInviteModel(String invited, String codeInvite) {
        AccountLinkedModel accountLinkedModel = AccountLinkedModel.copyFromDB(invited, SDKDatacontroler.getInstance());
        boolean linked = !accountLinkedModel.linkedAsList().isEmpty();

        UserInviteModel userInviteModel = getUserInviteModelByCode(codeInvite);
        //Khong co TH nay -> check cho chac
        if (userInviteModel == null) return;

        userInviteModel.addInvited(accountLinkedModel.accountID, linked);
        userInviteModel.saveToDB(SDKDatacontroler.getInstance());
    }

    /**
     * update phan thuong invite
     *
     * @param accountID
     * @param update
     * @return
     */
    public boolean updateRewardInviteDetail(String accountID, List<UpdateRewardDetail> update) {
        UserInviteModel userInviteModel = getUserInviteModelByID(accountID);
        update.parallelStream().forEach(obj -> userInviteModel.updateRewardInvite(obj.accountID, obj.id, obj.point));
        return userInviteModel.saveToDB(SDKDatacontroler.getInstance());
    }

    public boolean canRewardBonusInvite(String accountID, String id, int point) {
        UserInviteModel userInviteModel = getUserInviteModelByID(accountID);
        return userInviteModel.canRewardInviteBonus(id, point);
    }

    public boolean rewardBonusInvite(String accountID, String id, int point) {
        UserInviteModel userInviteModel = getUserInviteModelByID(accountID);
        userInviteModel.rewardInviteBonus(id, point);
        return userInviteModel.saveToDB(SDKDatacontroler.getInstance());
    }
}
