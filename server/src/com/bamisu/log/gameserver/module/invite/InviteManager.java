package com.bamisu.log.gameserver.module.invite;

import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.module.invite.entities.InviteBonusDetail;
import com.bamisu.log.gameserver.module.invite.entities.UpdateRewardDetail;
import com.bamisu.log.gameserver.module.invite.entities.UserInvite;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EConditionType;
import com.bamisu.log.gameserver.module.invite.config.InviteBonusConfig;
import com.bamisu.log.gameserver.module.invite.config.InviteConditionConfig;
import com.bamisu.log.gameserver.module.invite.config.InviteRewardConfig;
import com.bamisu.log.gameserver.module.invite.config.entities.InviteBonusVO;
import com.bamisu.log.gameserver.module.invite.config.entities.InviteConditionVO;
import com.bamisu.log.gameserver.module.invite.config.entities.InviteRewardVO;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateInvitecode;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;
import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InviteManager {

    private InviteBonusConfig inviteBonusConfig;
    private InviteConditionConfig inviteConditionConfig;
    private InviteRewardConfig inviteRewardConfig;




    private static InviteManager ourInstance = new InviteManager();

    public static InviteManager getInstance() {
        return ourInstance;
    }

    private InviteManager() {
        //Load config
        loadConfig();
    }

    private void loadConfig(){
        inviteBonusConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Invite.FILE_PATH_CONFIG_INVITE_BONUS), InviteBonusConfig.class);
        inviteConditionConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Invite.FILE_PATH_CONFIG_INVITE_CONDITION), InviteConditionConfig.class);
        inviteRewardConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Invite.FILE_PATH_CONFIG_INVITE_REWARD), InviteRewardConfig.class);
    }




    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Tao model ------- TH xay ra khi bat dau nhap code
     * @param uid
     * @param inviteCode
     * @param zone
     * @return
     */
    public UserInvite inputCodeUserInviteModel(Zone zone, long uid, String inviteCode) throws TException {
        UserModel userModel = ((InviteHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_INVITE)).getUserModel(uid);
        return SDKGateInvitecode.inputCodeUserInviteModel(userModel.accountID, inviteCode);
    }

    /**
     * Kiem tra da tung nhap code chua
     * @param zone
     * @param uid
     * @return
     */
    public boolean haveInputInviteCode(Zone zone, long uid){
        UserModel userModel = ((InviteHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_INVITE)).getUserModel(uid);
        try {
            return SDKGateInvitecode.haveInputInviteCode(userModel.accountID);
        } catch (TException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Kiem tra code co ton tai khong
     * @param code
     * @return
     */
    public boolean haveExsistInviteCode(String code){
        try {
            return SDKGateInvitecode.haveExsistInviteCode(code);
        } catch (TException e) {
            e.printStackTrace();
        }
        return false;
    }



    /*--------------------------------------------------   USER  -----------------------------------------------------*/
    /**
     * User Invite Mode
     * @param zone
     * @param uid
     * @return
     */
    public UserInvite getUserInviteModel(Zone zone, long uid){
        UserModel userModel = ((InviteHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_INVITE)).getUserModel(uid);
        try {
            return SDKGateInvitecode.getUserInviteModel(userModel.accountID);
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Kiem tra co nhan dc phan thuong khong
     * @param id
     * @param point
     * @return
     */
    public boolean canRewardInviteBonus(Zone zone, long uid, String id, int point){
        UserModel userModel = ((InviteHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_INVITE)).getUserModel(uid);
        boolean result = false;
        try {
            //Kiem tra co the nhan tren model khong
            result = SDKGateInvitecode.canRewardInviteBonus(userModel.accountID, id, point);
        } catch (TException e) {
            e.printStackTrace();
        }
        if(!result)return false;

        //Kiem tra xem co khop config khong
        result = getInviteConditionConfig(getInviteBonusConfig(id).rewardCondition).containCondition(point);

        return result;
    }
    public boolean rewardInviteBonus(Zone zone, long uid, String id, int point){
        UserModel userModel = ((InviteHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_INVITE)).getUserModel(uid);
        try {
            return SDKGateInvitecode.rewardInviteBonus(userModel.accountID, id, point);
        } catch (TException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiem tra nhan het qua chua
     * @param uid
     * @param zone
     * @return
     */
    public boolean haveGiftCanReceive(long uid, Zone zone){
        return haveGiftCanReceive(getUserInviteModel(zone, uid));
    }
    public boolean haveGiftCanReceive(UserInvite userInvite){
        for(InviteBonusVO inviteCf : getListInviteBonusConfig()){

            for(InviteBonusDetail dataSave : userInvite.inviteBonus){
                if(!inviteCf.id.equals(dataSave.id))continue;

                for(int i = dataSave.point; i > 0 ; i--){
                    if(!getInviteConditionConfig(getInviteBonusConfig(dataSave.id).rewardCondition).containCondition(i)) continue;
                    if(userInvite.canRewardInviteBonus(dataSave.id, i)) return true;
                }
            }
        }
        return false;
    }

    /**
     * Xay ra event
     * @param zone
     * @param condition
     */
    public void triggerUpdateRewardInviteModel(Zone zone, long uid, EConditionType condition){
        UserInvite userInvite = getUserInviteModel(zone, uid);
        if(userInvite.accountIDInvite.isEmpty())return;

        //Lay cac dieu kien config chua cung type dieu kien
        List<InviteConditionVO> conditionCf = getInviteConditionConfigDependType(condition);
        List<InviteBonusVO> bonusCf = getListInviteBonusConfig().parallelStream().
                filter(bonus -> conditionCf.parallelStream().anyMatch(con -> con.id.equals(bonus.rewardCondition))).
                collect(Collectors.toList());

        List<UpdateRewardDetail> listUpdate = new ArrayList<>();
        for(InviteBonusVO bonus : bonusCf){
            listUpdate.add(UpdateRewardDetail.create(userInvite.accountID, bonus.id, 1));
        }
        if(listUpdate.isEmpty())return;

        try {
            SDKGateInvitecode.updateRewardInviteDetail(userInvite.accountIDInvite, listUpdate);
        } catch (TException e) {
            e.printStackTrace();
        }
    }




    /*--------------------------------------------------   CONFIG  ---------------------------------------------------*/
    public InviteBonusConfig getInviteBonusConfig(){
        return inviteBonusConfig;
    }
    public List<InviteBonusVO> getListInviteBonusConfig(){
        return getInviteBonusConfig().list;
    }
    public InviteBonusVO getInviteBonusConfig(String id){
        for(InviteBonusVO index : getListInviteBonusConfig()){
            if(index.id.equals(id)){
                return index;
            }
        }
        return null;
    }

    /**
     * Phan thuong khi nhap invite code cua nguoi choi khac
     * @return
     */
    public List<ResourcePackage> getRewardInvitedConfig(){
        return getInviteBonusConfig().invited;
    }

    /**
     * Config dieu kien cua invite bonus
     * @return
     */
    public List<InviteConditionVO> getInviteConditionConfig(){
        return inviteConditionConfig.list;
    }
    public InviteConditionVO getInviteConditionConfig(String id){
        for(InviteConditionVO index : getInviteConditionConfig()){
            if(index.id.equals(id)){
                return index;
            }
        }
        return null;
    }
    public List<InviteConditionVO> getInviteConditionConfigDependType(EConditionType condition){
        List<InviteConditionVO> get = new ArrayList<>();
        for(InviteConditionVO index : getInviteConditionConfig()){
            if(index.condition.type.equals(condition.getId())){
                get.add(index);
            }
        }
        return get;
    }

    /**
     * Config phan thuong cua invite bonus
     * @return
     */
    public List<InviteRewardVO> getInviteRewardConfig(){
        return inviteRewardConfig.list;
    }
    public InviteRewardVO getInviteRewardConfig(String id){
        for(InviteRewardVO index : getInviteRewardConfig()){
            if(index.id.equals(id)){
                return index;
            }
        }
        return null;
    }

    public List<ResourcePackage> getRewardBonusInvite(String id, int point){
        InviteBonusVO inviteBonusCf = InviteManager.getInstance().getInviteBonusConfig(id);
        return getInviteRewardConfig(inviteBonusCf.reward).getRewardBonusInvite(
                getInviteConditionConfig(inviteBonusCf.rewardCondition).indexRewardBonus(point));
    }
}
