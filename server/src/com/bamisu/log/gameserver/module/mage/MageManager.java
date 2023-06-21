package com.bamisu.log.gameserver.module.mage;

import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.gamelib.item.entities.SageEquipDataVO;
import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.log.gameserver.datamodel.mage.SageSkillModel;
import com.bamisu.log.gameserver.datamodel.mage.UserMageModel;
import com.bamisu.log.gameserver.datamodel.mage.entities.StoneMageInfo;
import com.bamisu.gamelib.entities.Attr;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.mage.MageConfig;
import com.bamisu.log.gameserver.module.characters.mage.entities.StoneMageSlotVO;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.item.entities.AttributeVO;
import com.bamisu.gamelib.item.entities.SageEquipVO;
import com.bamisu.gamelib.item.entities.SageSlotVO;
import com.bamisu.log.gameserver.module.mage.cmd.send.SendSkillTree;
import com.bamisu.gamelib.skill.SkillConfigManager;
import com.bamisu.gamelib.skill.config.entities.Dependent;
import com.bamisu.gamelib.skill.config.entities.SageSkillVO;
import com.bamisu.log.gameserver.module.skill.exception.SkillNotFoundException;
import com.bamisu.gamelib.base.model.UserModel;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;

import java.util.List;

public class MageManager {
    public static MageManager instance;

    public static MageManager getInstance(){
        if(instance == null){
            instance = new MageManager();
        }
        return instance;
    }
    private MageManager() {
    }

    /**
     * Get User Mage Model
     */
    public UserMageModel getUserMageModel(Zone zone, long uid) {
        UserMageModel userMageModel = UserMageModel.copyFromDBtoObject(uid, zone);
        if (userMageModel == null) {
            return createUserMageModel(zone, uid);
        }
        return userMageModel;
    }

    /**
     * Create User Mage Model
     */
    public UserMageModel createUserMageModel(Zone zone, long uid) {
        return UserMageModel.createUserMageModel(uid, zone);
    }



    /*-------------------------------------------------------LIST_ITEM MAGE------------------------------------------------------*/

    /**
     * Get Equip Mage
     */
    public SageEquipDataVO getEquipmentMageModel(Zone zone, long uid, String hash) {
        UserMageModel userMageModel = getUserMageModel(zone, uid);
        return getEquipmentMageModel(userMageModel, hash);
    }

    public SageEquipDataVO getEquipmentMageModel(UserMageModel userMageModel, String hash) {
        for (SageSlotVO slot : userMageModel.equipment) {
            if (slot.equip == null || !slot.haveLock()) {
                continue;
            }
            if (slot.equip.hash.equals(hash)) {
                return slot.equip;
            }
        }
        return null;
    }

    public SageEquipDataVO getEquipmentMageModel(Zone zone, long uid, int position) {
        UserMageModel userMageModel = getUserMageModel(zone, uid);
        return getEquipmentMageModel(userMageModel, position);
    }

    public SageEquipDataVO getEquipmentMageModel(UserMageModel userMageModel, int position) {
        if (position < 0 || position > userMageModel.equipment.size() - 1) {
            return null;
        }
        return userMageModel.equipment.get(position).equip;
    }

    public boolean haveEquipItemMage(String hashItem, UserMageModel userMageModel){
        for(SageSlotVO slotVO : userMageModel.equipment){
            if(slotVO == null || !slotVO.haveLock()){
                continue;
            }
            if(slotVO.equip.hash.equals(hashItem)){
                return true;
            }
        }
        return false;
    }

    /**
     * Equip Mage Item
     */
    public boolean equipEquipmentMageModel(Zone zone, long uid, SageEquipDataVO equipVO, int position) {
        UserMageModel userMageModel = getUserMageModel(zone, uid);
        return equipEquipmentMageModel(zone, userMageModel, equipVO, position);
    }

    public boolean equipEquipmentMageModel(Zone zone, UserMageModel userMageModel, SageEquipDataVO equipVO, int position) {
        if (position < 0 || position > userMageModel.equipment.size() - 1) {
            return false;
        }
        if (userMageModel.equipment.get(position).equip != null &&
                userMageModel.equipment.get(position).haveLock()) {
            return false;
        }
        userMageModel.equipment.get(position).equip = equipVO;
        return userMageModel.saveToDB(zone);
    }

    /**
     * Unequip Mage Item
     */
    public boolean unequipEquipmentMageModel(Zone zone, long uid, int position) {
        UserMageModel userMageModel = getUserMageModel(zone, uid);
        return unequipEquipmentMageModel(zone, userMageModel, position);
    }

    public boolean unequipEquipmentMageModel(Zone zone, UserMageModel userMageModel, int position) {
        if (position < 0 || position > userMageModel.equipment.size() - 1) {
            return false;
        }
        userMageModel.equipment.get(position).equip = null;
        userMageModel.equipment.get(position).unlock();
        return userMageModel.saveToDB(zone);
    }

    /**
     * Update Mage Item
     */
    public boolean updateEquipmentMageModel(Zone zone, long uid, SageEquipDataVO equipVO, int position) {
        UserMageModel userMageModel = getUserMageModel(zone, uid);
        return updateEquipmentMageModel(zone, userMageModel, equipVO, position);
    }

    public boolean updateEquipmentMageModel(Zone zone, UserMageModel userMageModel, SageEquipDataVO equipVO, int position) {
        userMageModel.equipment.get(position).equip = equipVO;
        userMageModel.equipment.get(position).lock();
        return userMageModel.saveToDB(zone);
    }

    /**
     * Equip Skin Mage
     */
    public boolean equipSkinMageModel(Zone zone, long uid, String idSkin) {
        UserMageModel userMageModel = getUserMageModel(zone, uid);
        return userMageModel.equipSkin(idSkin, zone);
    }

    /**
     * Unequip Skin Mage
     */
    public boolean unequipSkinMageModel(Zone zone, long uid) {
        UserMageModel userMageModel = getUserMageModel(zone, uid);
        userMageModel.idSkin = null;
        return userMageModel.saveToDB(zone);
    }



    /*--------------------------------------------------- STATS MAGE --------------------------------------------------*/

    /**
     * Luc chien
     */
    public int getPower(Stats stats){

        return (int) (stats.readHp() / 2 +
                                stats.readStrength() + stats.readIntelligence() +
                                stats.readDexterity() * 2 +
                                stats.readArmor() * 2 + stats.readMagicResistance() * 2 +
                                stats.readAgility() * 2 +
                                stats.readCrit() * 2 + stats.readCritDmg() * 2 +
                                stats.readArmorPenetration() * 2 + stats.readMagicPenetration() * 2 +
                                stats.readTenacity() * 2 +
                                stats.readElusiveness() * 2);
    }

    /**
     * Tinh chi so
     */
    public Stats getStatsMage(long uid, Zone zone){
        UserMageModel userMageModel = getUserMageModel(zone, uid);
        return getStatsMage(userMageModel, zone);
    }
    public Stats getStatsMage(UserMageModel userMageModel, Zone zone){
        //Stat chua co do
        Stats stats = getStatsMageModel(userMageModel, zone);
        //Cong chi so do tren nguoi
        stats = Stats.readSumStats(stats, getStatsItem(userMageModel));
        //Cong chi so cua stone
        stats = Stats.readSumStats(stats, getStatsItem(getUserStoneMage(userMageModel, userMageModel.stoneUse)));

        return stats;
    }
    private Stats getStatsMageModel(UserMageModel userMageModel, Zone zone){
        MageConfig statsBasic = CharactersConfigManager.getInstance().getMageConfig();
        Stats statsGrowConfig = CharactersConfigManager.getInstance().getMageStatsGrowConfig();
        int level = userMageModel.readLevel(zone);

        //Chi so co ban trong config
        Stats stats = new Stats(statsBasic);
        stats.attack = calculationStatsMage(statsGrowConfig, stats.readAttack(), Attr.ATTACK, level - 1);
        stats.defensePenetration = calculationStatsMage(statsGrowConfig, stats.readDefensePenetration(), Attr.DEFENSE_PENETRATION, level - 1);
        stats.crit = calculationStatsMage(statsGrowConfig, stats.readCrit(), Attr.CRITICAL_CHANCE, level - 1);
        stats.critDmg = calculationStatsMage(statsGrowConfig, stats.readCritDmg(), Attr.CRITICAL_BONUS_DAMAGE, level - 1);

        return stats;
    }
    private final float calculationStatsMage(Stats growCf, float baseCf, Attr attr, int level){
        switch (attr){
            case ATTACK:
                return baseCf + (baseCf * growCf.readAttack() * level);
            case DEFENSE_PENETRATION:
                return baseCf + (baseCf * growCf.readDefensePenetration() * level);
            case CRITICAL_CHANCE:
                return baseCf + (baseCf * growCf.readCrit() * level);
            case CRITICAL_BONUS_DAMAGE:
                return baseCf + (baseCf * growCf.readCritDmg() * level);
        }
        return 0;
    }

    public Stats getStatsItem(UserMageModel mageModel){
        Stats stats = new Stats();
        for(SageSlotVO slotVO : mageModel.equipment){
            if(!slotVO.status || slotVO.equip == null){
                continue;
            }

            stats = Stats.readSumStats(stats, getStatsItem(slotVO.equip));
        }

        return stats;
    }
    public Stats getStatsItem(SageEquipDataVO sageEquip){
        if(sageEquip == null){
            return new Stats();
        }
        SageEquipVO equipCf = ItemManager.getInstance().getSageEquipConfig(sageEquip.id);
        if(equipCf == null){
            return new Stats();
        }
        Stats stats = getStatsItem(equipCf.listAttr);
        //Cong them chi so khi cuong hoa, nang cap


        return stats;
    }
    public Stats getStatsItem(StoneMageInfo stoneMageModel){
        if(stoneMageModel == null){
            return new Stats();
        }
        com.bamisu.log.gameserver.module.characters.mage.entities.StoneMageVO stoneCf = CharactersConfigManager.getInstance().getStoneMageConfig(stoneMageModel.id);
        if(stoneCf == null){
            return new Stats();
        }
        Stats stats = getStatsItem(stoneCf.listAttr);
        //Cong them chi so khi cuong hoa, nang cap


        return stats;
    }
    public Stats getStatsItem(List<AttributeVO> listAttr){
        Stats stats = new Stats();
        for(AttributeVO attr : listAttr){
            switch (Attr.fromValue(attr.attr)){
                case ATTACK:
                    stats.attack = attr.param;
                    break;
                case CRITICAL_CHANCE:
                    stats.crit = attr.param;
                    break;
                case CRITICAL_BONUS_DAMAGE:
                    stats.critDmg = attr.param;
                    break;
                case DEFENSE_PENETRATION:
                    stats.defensePenetration = attr.param;
                    break;
            }
        }

        return stats;
    }




    /*---------------------------------------------------- STONE MAGE -------------------------------------------------------*/

    /**
     * Get stone Equip
     */
    public StoneMageInfo getUserStoneMage(Zone zone, long uid, String idStone) {
        UserMageModel userMageModel = getUserMageModel(zone, uid);
        return getUserStoneMage(userMageModel, idStone);
    }

    public StoneMageInfo getUserStoneMage(UserMageModel userMageModel, String idStone) {
        for (StoneMageSlotVO slot : userMageModel.stoneSlot) {
            if (slot.haveLock()) {
                continue;
            }
            if (slot.stoneMageModel.id.equals(idStone)) {
                return slot.stoneMageModel;
            }
        }
        return null;
    }

    /**
     * Update stone Equip
     */
    public boolean updateUserStoneMage(Zone zone, long uid, String idStone) {
        UserMageModel userMageModel = getUserMageModel(zone, uid);
        return updateUserStoneMage(zone, userMageModel, idStone);
    }

    public boolean updateUserStoneMage(Zone zone, UserMageModel userMageModel, String idStone) {
        if (userMageModel.haveStone(idStone)) {
            userMageModel.stoneUse = idStone;
            return userMageModel.saveToDB(zone);
        }
        return false;
    }



    /*------------------------------------------------  SKILL -------------------------------------------------------*/
    /**
     * lấy thông tin cây kỹ năng
     *
     * @param uid
     */
    public SendSkillTree getSkillTree(Zone zone, long uid) {
        SageSkillModel sageSkillModel = SageSkillModel.copyFromDBtoObject(uid, zone);
        SendSkillTree sendSkillTree = new SendSkillTree();
        sendSkillTree.sageSkillModel = sageSkillModel;
        sendSkillTree.maxPoint = getMaxPoint(uid, zone);
        return sendSkillTree;
    }

    public boolean haveSkillPointMage(long uid, Zone zone){
        return SageSkillModel.copyFromDBtoObject(uid, zone).readUsedSkillPoint() < getMaxPoint(uid, zone);
    }

    /**
     * học (tăng cấp) skill
     *
     * @param uid
     * @param skillID
     */
    public int studySkill(Zone zone, long uid, String skillID, boolean isMax) throws SkillNotFoundException {
//        UserModel userModel = ((ZoneExtension) zone.getExtension()).getUserManager().getUserModel(uid);
        SageSkillModel sageSkillModel = SageSkillModel.copyFromDBtoObject(uid, zone);
        //check skill tồn tại
        SageSkillVO currentSkillVO = SkillConfigManager.getInstance().getSageSkill(skillID);
        if (currentSkillVO == null) {
            throw new SkillNotFoundException();
        }

        //ko đủ điểm
        doStudi:
        while(sageSkillModel.readUsedSkillPoint() < getMaxPoint(uid, zone)){
            //điều kiện điểm đã cộng
            if (sageSkillModel.readUsedSkillPoint() < currentSkillVO.msps) {
                break;
            }

            SkillInfo sageSkillInfo = sageSkillModel.readLearnedSkill(skillID);
            if (sageSkillInfo == null) {  //chưa học
                //skill tùy chọn
                for (SkillInfo skillInfo : sageSkillModel.skills) {
                    SageSkillVO vo = SkillConfigManager.getInstance().getSageSkill(skillInfo.id);
                    for (String tag1 : currentSkillVO.tag) {
                        for (String tag2 : vo.tag) {
                            if (tag1.equalsIgnoreCase(tag2)) {
                                break doStudi;
                            }
                        }
                    }
                }

                //phụ thuộc vào skill khác
                for (Dependent dependent : currentSkillVO.dependent) {
                    SkillInfo tmpSkillInfo = sageSkillModel.readLearnedSkill(dependent.skill);
                    if (tmpSkillInfo == null) {
                        break doStudi;
                    }

                    if (tmpSkillInfo.level < dependent.level) {
                        break doStudi;
                    }
                }
            }

            //học
            if (sageSkillInfo == null) {
                sageSkillModel.skills.add(new SkillInfo(skillID, 1));

            } else {
                if (sageSkillInfo.level == currentSkillVO.desc.size()) {
                    //đã max cấp
                    break;
                }
                sageSkillInfo.level++;
            }

            if(!isMax) break;
        }

        sageSkillModel.saveToDB(zone);
        return -1;
    }

    private int getMaxPoint(long uid, Zone zone) {
        return BagManager.getInstance().getLevelUser(uid, zone);
    }

    public void resetLastColumSkill(MageHandler handler, User user) {
        Zone zone = handler.getParentExtension().getParentZone();
        UserModel userModel = ((ZoneExtension) zone.getExtension()).getUserManager().getUserModel(user);
        SageSkillModel sageSkillModel = SageSkillModel.copyFromDBtoObject(userModel.userID, zone);

//        if(BagManager.getInstance().changeMoney(userModel.userID, Arrays.asList(new MoneyPackageVO(MoneyType.DIAMOND, -20)), UserUtils.TransactionType.RESET_SAGE_SKILL, zone).isSuccess()){
            sageSkillModel.resetColum(zone);
//        }else {
//            handler.send(new SendResetLastColum(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_MONEY_MAGE), user);
//        }
    }

    public void resetAllSkill(MageHandler handler, User user) {
        Zone zone = handler.getParentExtension().getParentZone();
        UserModel userModel = ((ZoneExtension) zone.getExtension()).getUserManager().getUserModel(user);
        SageSkillModel sageSkillModel = SageSkillModel.copyFromDBtoObject(userModel.userID, zone);

//        if(BagManager.getInstance().changeMoney(userModel.userID, Arrays.asList(new MoneyPackageVO(MoneyType.DIAMOND, -100)), UserUtils.TransactionType.RESET_SAGE_SKILL, zone).isSuccess()){
            sageSkillModel.initSkill();
            sageSkillModel.saveToDB(zone);
//        }else {
//            handler.send(new SendResetLastColum(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_MONEY_MAGE), user);
//        }
    }

    public SageSkillModel getSageSkillModel(Zone zone, long uid) {
        return SageSkillModel.copyFromDBtoObject(uid, zone);
    }
}
