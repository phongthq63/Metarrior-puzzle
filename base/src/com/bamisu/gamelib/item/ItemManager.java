package com.bamisu.gamelib.item;

import com.bamisu.gamelib.entities.*;
import com.bamisu.gamelib.item.entities.PayEquipVO;
import com.bamisu.gamelib.item.entities.*;
import com.bamisu.gamelib.item.id.WeaponDefine;
import com.bamisu.gamelib.item.id.WeaponId;
import com.bamisu.gamelib.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ItemManager {
    private static ItemManager ourInstance = null;
    public static ItemManager getInstance(){
        if (ourInstance == null){
            ourInstance = new ItemManager();
        }
        return ourInstance;
    }

    private WeaponDefine weaponDefine;
    private WeaponDefine colorDefine;
    private WeaponDefine gemDefine;
    private WeaponDefine slotDefine;
    private WeaponDefine moneyDefine;
    private EquipConfig equipConfig;
    private ItemSlotConfig itemSlotConfig;
    private StoneConfig stoneConfig;
    private StoneSlotConfig stoneSlotConfig;
    private EquipLevelConfig equipLevelConfig;
    private StoneLevelConfig stoneLevelConfig;
    private FusionItemConfig fusionItemConfig;
    private FusionStoneConfig fusionStoneConfig;
    private FragmentConfig fragmentConfig;
    private SageSlotConfig sageSlotConfig;
    private SageConfig sageConfig;
    private CelestialItemConfig celestialConfig;
    private LevelUserConfig levelUserConfig;
    private PayConfig payConfig;
    private HeroChoiceChestConfig heroChoiceChestConfig;
    private HammerConfig hammerConfig;
    private WithdrawFeeConfig feeConfig;

    private ItemManager(){
        hammerConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Bag.FILE_PATH_CONFIG_HAMMER), HammerConfig.class);
        heroChoiceChestConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Chest.FILE_PATH_CONFIG_HERO_CHEST), HeroChoiceChestConfig.class);
        payConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Bag.FILE_PATH_CONFIG_PAY), PayConfig.class);
        levelUserConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.User.FILE_PATH_CONFIG_LEVEL_USER), LevelUserConfig.class);
        feeConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.User.FILE_PATH_CONFIG_WITHDRAW_FEE), WithdrawFeeConfig.class);
        moneyDefine = Utils.fromJson(Utils.loadConfig(ServerConstant.Item.FILE_PATH_CONFIG_ID_MONEY), WeaponDefine.class);
        slotDefine = Utils.fromJson(Utils.loadConfig(ServerConstant.Item.FILE_PATH_CONFIG_ID_SLOT), WeaponDefine.class);
        colorDefine = Utils.fromJson(Utils.loadConfig(ServerConstant.Item.FILE_PATH_CONFIG_ID_COLOR), WeaponDefine.class);
        gemDefine = Utils.fromJson(Utils.loadConfig(ServerConstant.Item.FILE_PATH_CONFIG_ID_GEM), WeaponDefine.class);
        weaponDefine = Utils.fromJson(Utils.loadConfig(ServerConstant.Item.FILE_PATH_CONFIG_ID_WEAPON), WeaponDefine.class);
        celestialConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Item.FILE_PATH_CONFIG_CELESTIAL_EQUIP), CelestialItemConfig.class);
        stoneSlotConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Item.FILE_PATH_CONFIG_STONE_SLOT), StoneSlotConfig.class);
        equipConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Item.FILE_PATH_CONFIG_EQUIP), EquipConfig.class);
        itemSlotConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Item.FILE_PATH_CONFIG_ITEM_SLOT), ItemSlotConfig.class);
        stoneConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Item.FILE_PATH_CONFIG_STONE), StoneConfig.class);
        equipLevelConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Item.FILE_PATH_CONFIG_LEVEL_EQUIP), EquipLevelConfig.class);
        stoneLevelConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Item.FILE_PATH_CONFIG_LEVEL_STONE), StoneLevelConfig.class);
        fusionItemConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Item.FILE_PATH_CONFIG_FUSION_EQUIP), FusionItemConfig.class);
        fusionStoneConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Item.FILE_PATH_CONFIG_FUSION_STONE), FusionStoneConfig.class);
        fragmentConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Item.FILE_PATH_CONFIG_FRAGMENT_HERO), FragmentConfig.class);
        sageSlotConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Item.FILE_PATH_CONFIG_SAGE_SLOT_CONFIG), SageSlotConfig.class);
        sageConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Item.FILE_PATH_CONFIG_SAGE_ITEM), SageConfig.class);
    }

    public List<ResourcePackage> getHammerConfig(){
        return hammerConfig.list;
    }
    public ResourcePackage getHammerConfig(String idHammer){
        for(ResourcePackage hammer : getHammerConfig()){
            if(hammer.id.equals(idHammer)) return hammer;
        }
        return null;
    }



    /*-------------------------- ID MONEY-------------------------------*/
    public List<WeaponId> getMoneyConfig(){
        return moneyDefine.listId;
    }

    public String getMoneyIdDependOnName(String name){
        for (WeaponId weaponId: getMoneyConfig()){
            if (name.equals(weaponId.name)){
                return weaponId.id;
            }
        }
        return null;
    }

    /*-------------------------- ID SLOT -------------------------------*/
    public List<WeaponId> getSlotConfig(){
        return slotDefine.listId;
    }

    public String getSlotIdDependOnName(String name){
        for (WeaponId weaponId: getSlotConfig()){
            if (name.equals(weaponId.name)){
                return weaponId.id;
            }
        }
        return null;
    }

    /*-------------------------- ID WEAPON -----------------------------*/
    public List<WeaponId> getWeaponConfig(){
        return weaponDefine.listId;
    }

    public String getWeaponIdDependOnName(String name){
        for (WeaponId weaponId: getWeaponConfig()){
            if (name.equals(weaponId.name)){
                return weaponId.id;
            }
        }
        return null;
    }

    /*------------------------- ID COLOR--------------------------------*/
    public List<WeaponId> getColorConfig(){
        return colorDefine.listId;
    }

    public List<WeaponId> getGemConfig(){
        return gemDefine.listId;
    }

    public String getColorIdDependOnName(String name){
        for(WeaponId weaponId: getColorConfig()){
            if (name.equals(weaponId.name)){
                return weaponId.id;
            }
        }
        return null;
    }

    public String getGemTypeDependOnName(String name){
        for(WeaponId weaponId: getGemConfig()){
            if (name.equals(weaponId.name)){
                return weaponId.id;
            }
        }
        return null;
    }
    public SageEquipVO getSageEquipConfig(String idEquip){
        for(SageEquipVO index : sageConfig.listItem){
            if(index.id.equals(idEquip)){
                return index;
            }
        }
        return null;
    }

    public List<SageSlotVO> getSageSlotConfig(){
        return sageSlotConfig.listSageSlot;
    }

    public List<FusionVO> getFusionItemConfig(){
        return fusionItemConfig.listFusion;
    }


    public FusionVO getFusionByStar(int star){
        for (FusionVO fusionCf : getFusionItemConfig()){
            for (StarVO starCf : fusionCf.listStar){
                if (starCf.star == star) return fusionCf;
            }
        }
        return null;
    }

    public int getSizeInFusionWeapon(){
        return fusionItemConfig.size;
    }

    public int getSizeInFusionStone(){
        return fusionStoneConfig.size;
    }

    public List<StoneSlotVO> getStoneSlotConfig(){
        return stoneSlotConfig.listStoneSlot.stream().map(StoneSlotVO::create).collect(Collectors.toList());
    }

    public List<StoneLevelConfigVO> getStoneLevelConfig(){
        return stoneLevelConfig.listAttrStone;
    }

    /**
     * Get Stone Config
     */
    public List<StoneConfigVO> getStoneConfig(){
        return stoneConfig.listStone;
    }

    /**
     * Get Stone depend on ID
     */
    public StoneConfigVO getStoneById(String id){
        for (StoneConfigVO vo: stoneConfig.listStone){
            if (vo.id.equals(id)){
                return vo;
            }
        }
        return null;
    }

    public StoneVO getStoneVO(String id, int level){
        StoneConfigVO stoneConfigVO = getStoneById(id);
        StoneLevelConfigVO stoneLevelConfigVO = getStoneLevel(id);
        StoneLevelVO stoneLevelVO = getStoneLevelVO(stoneLevelConfigVO, level);
        StoneVO stoneVO = new StoneVO(stoneConfigVO, stoneLevelConfigVO, stoneLevelVO);
        return stoneVO;
    }

    public StoneLevelVO getStoneLevelVO(StoneLevelConfigVO stoneLevelConfigVO, int level){
        for (int i =0 ; i< stoneLevelConfigVO.listLevel.size(); i++){
            if (stoneLevelConfigVO.listLevel.get(i).level == level){
                return stoneLevelConfigVO.listLevel.get(i);
            }
        }
        return null;
    }

    /**
     * Get Item Slots Config
     */
    public List<ItemSlotVO> getItemSlotConfig(){
        return itemSlotConfig.listItemSlot.stream().
                map(ItemSlotVO::new).
                collect(Collectors.toList());
    }

    /* -------------------------EQUIPMENTS----------------------------- */
    /**
     *  Get All Equips
     */
    public List<EquipConfigVO> getEquipConfig(){
        return equipConfig.listEquip;
    }
    public EquipConfigVO getEquipConfig(String id){
        return equipConfig.readEquip(id);
    }

    public EquipLevelConfig getEquipLevelConfig(){
        return equipLevelConfig;
    }

    /**
     * Depend on ID
     * @param id
     * @return
     */
    public EquipLevelConfigVO getEquipLevelConfigVO(String id){
        return getEquipLevelConfig().read(id);
    }

    public EquipConfig getConfigEquip(){
        return equipConfig;
    }

    /**
     * Get equipment depend on ID
     */
    public EquipConfigVO getEquipByID(String id){
        EquipConfig equipConfig = getConfigEquip();
        return equipConfig.readEquip(id);
    }


    public EquipVO getEquipVO(String id, int level, int count){
        EquipConfigVO equipConfigVO = getEquipByID(id);
        if(equipConfigVO == null) return null;

        EquipDataVO equipDataVO = new EquipDataVO();
        equipDataVO.count = count;
        equipDataVO.hash = Utils.genItemHash();
        equipDataVO.expFis = getEquipConfig(id).expFis;
        EquipLevelConfigVO equipLevelConfigVO = getEquipLevelConfigVO(id);
        EquipLevelVO equipLevelVO = getAttrFollowLevelEquip(equipLevelConfigVO, level);
        if(equipLevelVO == null) return null;

        EquipVO equipVO = new EquipVO(equipDataVO, equipConfigVO, equipLevelConfigVO, equipLevelVO);
        return equipVO;
    }

    /**
     * Get equipment HAS LEVEL HIGHER
     */
    public EquipLevelVO getEquipLevelHigher(String id, int exp, int level){
        EquipLevelConfigVO equipLevelConfigVO = getEquipLevelConfigVO(id);
        int expNeed = 0;
        for (EquipLevelVO vo: equipLevelConfigVO.listLevel){
            if (level <= vo.level){
                expNeed = expNeed + vo.expNeed;
                if (expNeed > exp){
                    return vo;
                }
            }

            //If level MAX
            if (vo.level == equipLevelConfigVO.maxLevel){
                return vo;
            }
        }


        return null;
    }

    public EquipLevelVO getAttrFollowLevelEquip(EquipLevelConfigVO equipLevelConfigVO, int level) {
        if(equipLevelConfigVO == null) return null;
        return equipLevelConfigVO.readLevel(level);
    }

    public StoneLevelConfigVO getStoneLevel(String id) {
        for (StoneLevelConfigVO stoneLevelCf : getStoneLevelConfig()){
            if (stoneLevelCf.id.equals(id)){
                return stoneLevelCf;
            }
        }
        return null;
    }

    public StoneLevelVO getStoneLevelHigher(StoneDataVO stoneData) {
        StoneLevelConfigVO stoneLevelCf = getStoneLevel(stoneData.id);
        for (StoneLevelVO stoneLv : stoneLevelCf.listLevel){
            if (stoneLv.level == stoneData.level + 1){
                return stoneLv;
            }
        }
        return null;
    }

    public List<FragmentConfigVO> getFragmentConfig(){
        return fragmentConfig.listFragment;
    }

    public FragmentConfigVO getFragmentConfig(String idFragment){
        for(FragmentConfigVO configVO : fragmentConfig.listFragment){
            if(configVO.id.equals(idFragment)){
                return configVO;
            }
        }
        return null;
    }

    public List<CelestialEquipVO> getCelestialEquipConfig() {
        return celestialConfig.listItem;
    }

    public int getSizeEquip() {
        return equipConfig.sizeBag;
    }

    public int getSizeStone() {
        return stoneConfig.sizeBag;
    }

    public List<LevelSageVO> getMethodLevel(){
        return levelUserConfig.listLevel;
    }

    public StoneDataVO getRandomStoneDependOnStar(int type, int level){
        List<StoneConfigVO> listStone = getStoneConfig();
        List<StoneConfigVO> listStar = new ArrayList<>();
        for (StoneConfigVO stoneConfigVO: listStone){
            if (stoneConfigVO.type == type){
                listStar.add(stoneConfigVO);
            }
        }
        Random random = new Random();
        int rand = random.nextInt(listStar.size());
        StoneConfigVO newStone = listStar.get(rand);
        StoneDataVO stoneDataVO = convertStoneConfigToData(newStone, level);
        return stoneDataVO;
    }

    public EquipDataVO getRandomEquipDependOnStar(int star, int level){
        List<EquipConfigVO> listEquip = getEquipConfig();
        List<EquipConfigVO> listStar = new ArrayList<>();
        for (EquipConfigVO equipConfigVO: listEquip){
            if (equipConfigVO.star == star){
                listStar.add(equipConfigVO);
            }
        }
        Random random = new Random();
        int rand = random.nextInt(listStar.size());
        EquipConfigVO newEquip = new EquipConfigVO(listStar.get(rand));
        EquipDataVO equipDataVO = convertEquipConfigToData(newEquip);
        equipDataVO.level = level;
        return equipDataVO;
    }

    public PayConfig getPayConfig() {
        return payConfig;
    }
    public PayEquipVO getCostFusionHeroEquipConfig(int star) {
        return getPayConfig().readPayFusionHeroEquip(star);
    }
    public PayGemVO getCostFusionStoneConfig(int level) {
        return getPayConfig().readPayFusionStone(level);
    }

    public ResourcePackage getHeroInChest(int position){
        return heroChoiceChestConfig.listFra.get(position);
    }

    public StoneDataVO convertStoneConfigToData(StoneConfigVO stoneConfigVO, int level) {
        StoneDataVO stoneDataVO = new StoneDataVO();
        stoneDataVO.count = 1;
        stoneDataVO.level = level;
        stoneDataVO.hash = Utils.genStoneHash();
        stoneDataVO.id = stoneConfigVO.id;
        return stoneDataVO;
    }

    public EquipDataVO convertEquipConfigToData(EquipConfigVO equipConfigVO) {
        if(equipConfigVO == null) return null;

        EquipDataVO equipDataVO = new EquipDataVO();
        equipDataVO.position = equipConfigVO.position;
        equipDataVO.listSlotStone = ItemManager.getInstance().getStoneSlotConfig();
        equipDataVO.id = equipConfigVO.id;
        equipDataVO.hash = Utils.genItemHash();
        equipDataVO.star = equipConfigVO.star;
        equipDataVO.level = 0;
        equipDataVO.hashHero = null;
        equipDataVO.exp = 0;
        equipDataVO.expFis = equipConfigVO.expFis;
        equipDataVO.count = 1;
        return equipDataVO;
    }

    public WithdrawFeeConfig getWithdrawFeeConfig() {
        return this.feeConfig;
    }
}
