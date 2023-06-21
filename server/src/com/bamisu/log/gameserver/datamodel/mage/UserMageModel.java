package com.bamisu.log.gameserver.datamodel.mage;

import com.bamisu.log.gameserver.datamodel.mage.entities.StoneMageInfo;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.mage.entities.StoneMageSlotVO;
import com.bamisu.log.gameserver.module.characters.mage.entities.StoneMageVO;
import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.item.entities.SageSlotVO;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserMageModel extends DataModel {
    public long uid;
    public String idSkin;
    public Set<String> skinOwner = new HashSet<>();
    public short level = 1;
    public long exp;
    public List<StoneMageSlotVO> stoneSlot;
    public String stoneUse;
    public List<SageSlotVO> equipment;

    public UserMageModel() {
    }

    private UserMageModel(long uid) {
        this.uid = uid;
        init();
    }

    private void init(){

    }

    private void initStone(){
        List<StoneMageVO> listStone = CharactersConfigManager.getInstance().getListStoneMageConfig();
        if(listStone.size() != stoneSlot.size()){
            return;
        }
        for(int i = 0; i < stoneSlot.size(); i++){
            stoneSlot.get(i).stoneMageModel = StoneMageInfo.createStoneMageModel(listStone.get(i).id);
        }
    }

    public static UserMageModel createUserMageModel(long uid, Zone zone){
        UserMageModel userMageModel = new UserMageModel(uid);
        userMageModel.stoneSlot = CharactersConfigManager.getInstance().getStoneMageSlotConfig();
        userMageModel.equipment = ItemManager.getInstance().getSageSlotConfig();
        userMageModel.initStone();

        userMageModel.saveToDB(zone);

        return userMageModel;
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

    public static UserMageModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    private static UserMageModel copyFromDBtoObject(String uId, Zone zone) {
        UserMageModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserMageModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserMageModel.class);
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
    /**
     * Check have stone
     */
    public boolean haveStone(String idStone){
        for(StoneMageSlotVO slot : stoneSlot){
            if(!slot.haveLock() && slot.stoneMageModel.id.equals(idStone)){
                return true;
            }
        }
        return false;
    }

    /**
     * Get star Skin
     */
    public String readSkin(){
        return (idSkin != null) ? idSkin : "default";
    }

    /**
     * Equip Skin
     */
    public boolean equipSkin(String idSkin, Zone zone){
        if(skinOwner.contains(idSkin)){
            this.idSkin = idSkin;
            return saveToDB(zone);
        }
        return false;
    }

    /**
     * Get stone using
     */
    public String readStoneUse(){
        return (stoneUse != null) ? stoneUse : "null";
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    public short readLevel(Zone zone){
        int levelUser = BagManager.getInstance().getLevelUser(uid, zone);
        if(levelUser != level){
            level = (short) levelUser;
            saveToDB(zone);
        }
        return level;
    }
    public long readExp(Zone zone){
        long expUser = BagManager.getInstance().getExpSurplusUser(uid, zone);
        if(expUser != exp){
            exp = expUser;
            saveToDB(zone);
        }
        return exp;
    }
}
