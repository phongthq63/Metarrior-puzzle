package com.bamisu.log.gameserver.datamodel.hero.entities;

import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.item.entities.ItemSlotVO;
import com.bamisu.gamelib.item.entities.MoneyPackageVO;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.hero.define.EHeroType;
import com.bamisu.log.gameserver.module.hero.entities.HeroLogObj;
import com.bamisu.log.gameserver.module.hero.define.EHeroStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HeroModel{
    public String hash;
    public String id;
    public long uid;
    public short level = 1;
    public short star;
    public int type;
    public List<ItemSlotVO> equipment;
    public Map<String,Integer> resource = new HashMap<>();
    public String motherHash = "";
    public String fatherHash = "";
    public List<String> children = new ArrayList<>();
    public byte breed = -1;
    public byte maxBreed = -1;
    public boolean isBreeding = false; // có đang triệu hồi hay không
    public Long timeClaim = null; // Áp dụng cho hero đang đếm ngược thời gian


    private final Object lockLevel = new Object();
    private final Object lockRes = new Object();



    public static HeroModel createByHeroModel(HeroModel heroModel){
        HeroModel model = new HeroModel();
        model.hash = heroModel.hash;
        model.id = heroModel.id;
        model.uid = heroModel.uid;
        model.level = heroModel.level;
        model.star = heroModel.star;
        model.type = heroModel.type;
        model.equipment = heroModel.equipment.stream().
                map(ItemSlotVO::new).
                collect(Collectors.toList());
        model.resource = heroModel.resource;
        model.motherHash = heroModel.motherHash;
        model.fatherHash = heroModel.fatherHash;
        model.children = heroModel.children;
        model.isBreeding = heroModel.isBreeding;
        if (heroModel.breed == -1) {
            model.breed = 0;
        } else {
            model.breed = heroModel.breed;
        }

        if (heroModel.maxBreed == -1) {
            model.maxBreed = getDefaultBreed(heroModel.id);
        } else {
            model.maxBreed = heroModel.maxBreed;
        }

        return model;
    }

    public static HeroModel createHeroModel(long uid, String id, int star, EHeroType type) {
        HeroModel heroModel = new HeroModel();
        heroModel.hash = Utils.genHeroHash();
        heroModel.id = id;
        heroModel.uid = uid;
        heroModel.star = (short) star;
        heroModel.type = type.getId();
        heroModel.equipment = ItemManager.getInstance().getItemSlotConfig();
        heroModel.breed = 0;
        heroModel.maxBreed = getDefaultBreed(id);

        return heroModel;
    }

    /**
     * Create new hero with time countdown
     * @param uid
     * @param id
     * @param star
     * @param type
     * @param timeClaim
     * @return
     */
    public static HeroModel createHeroModel(long uid, String id, int star, EHeroType type, long timeClaim) {
        HeroModel heroModel = createHeroModel(uid, id, star, type);
        heroModel.timeClaim = timeClaim;
        return heroModel;
    }

    public static HeroModel createHeroModel(long uid, HeroVO heroCf, EHeroType type) {
        if(heroCf == null) return null;

        HeroModel heroModel = new HeroModel();
        heroModel.hash = Utils.genHeroHash();
        heroModel.id = heroCf.id;
        heroModel.uid = uid;
        heroModel.star = heroCf.star;
        heroModel.type = type.getId();
        heroModel.equipment = ItemManager.getInstance().getItemSlotConfig();
        heroModel.maxBreed = heroCf.breed;
        heroModel.breed = 0;

        return heroModel;
    }

    /**
     * Create new hero with time countdown
     * @param uid
     * @param heroCf
     * @param type
     * @param timeClaim
     * @return
     */
    public static HeroModel createHeroModel(long uid, HeroVO heroCf, EHeroType type, long timeClaim) {
        HeroModel heroModel = createHeroModel(uid, heroCf, type);
        if (heroModel == null) {
            return null;
        }

        heroModel.timeClaim = timeClaim;
        return heroModel;
    }

    public static HeroModel createWithoutUser(String id, int star, int level) {
        HeroModel heroModel = new HeroModel();
        heroModel.hash = Utils.genHeroHash();
        heroModel.id = id;
        heroModel.star = (short) star;
        heroModel.level = (short) level;
        heroModel.type = EHeroType.NORMAL.getId();
        heroModel.equipment = ItemManager.getInstance().getItemSlotConfig();
        heroModel.breed = 0;
        heroModel.maxBreed = getDefaultBreed(id);

        return heroModel;
    }

    public List<ResourcePackage> readResourceHeroModel(){
        return resource.entrySet().stream().map(e -> new ResourcePackage(e.getKey(), e.getValue())).collect(Collectors.toList());
    }

    public List<ResourcePackage> readResourceResetHeroModel(){
        return resource.entrySet().stream().map(e -> new ResourcePackage(e.getKey(), e.getValue() * 1)).collect(Collectors.toList());
    }

    public List<EquipDataVO> readEquipmentHeroModel(){
        List<EquipDataVO> list = new ArrayList<>();
        for(ItemSlotVO slot : equipment){
            if (!slot.haveLock() || slot.equip == null){
                continue;
            }
            list.add(slot.equip);
        }

        return list;
    }

    private void useResource(String resourceType, int value){
        if(resource.containsKey(resourceType)){
            resource.put(resourceType, resource.get(resourceType) + value);
            return;
        }
        resource.put(resourceType, value);
    }
    public void useResource(List<MoneyPackageVO> resourceUpLevel){
        MoneyType moneyType;
        synchronized (lockRes){
            for (MoneyPackageVO money : resourceUpLevel){
                moneyType = MoneyType.fromID(money.id);
                if (moneyType != null) {
                    switch (moneyType){
                        case GOLD:
                            useResource(MoneyType.GOLD.getId(), money.amount);
                            break;
                        case MERITS:
                            useResource(MoneyType.MERITS.getId(), money.amount);
                            break;
                        case ESSENCE:
                            useResource(MoneyType.ESSENCE.getId(), money.amount);
                            break;
                    }
                }
            }
        }
    }

    public void resetHeroModel(){
        level = 1;
        resource.clear();
        equipment = ItemManager.getInstance().getItemSlotConfig().parallelStream().
                map(ItemSlotVO::new).
                collect(Collectors.toList());
    }

    public short readLevel(){
        synchronized (lockLevel){
            return level;
        }
    }

    public void upLevel(){
        synchronized (lockLevel){
            useResource(HeroManager.getInstance().getCostUpdateLevelHero(level + 1));
            level += 1;
        }
    }

    /**
     * Get default breed
     * @param heroId
     * @return
     */
    public static byte getDefaultBreed(String heroId) {
        HeroVO heroVO = CharactersConfigManager.getInstance().getHeroConfig(heroId);
        if (heroVO != null) {
            return heroVO.breed;
        }

        return 0;
    }

    /*---------------------------------------------------------------------------------------------------------*/
    public HeroLogObj toLogObject(){
        return new HeroLogObj(id, star);
    }
}
