package com.bamisu.log.gameserver.module.characters;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.item.define.HeroResource;
import com.bamisu.gamelib.item.entities.MoneyPackageVO;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.entities.ColorHero;
import com.bamisu.log.gameserver.entities.EStatus;
import com.bamisu.log.gameserver.module.characters.blessing.BlessingConfig;
import com.bamisu.log.gameserver.module.characters.blessing.UnlockBlessingConfig;
import com.bamisu.log.gameserver.module.characters.blessing.entities.ReduceTimeVO;
import com.bamisu.log.gameserver.module.characters.celestial.CelestialConfig;
import com.bamisu.log.gameserver.module.characters.celestial.entities.CelestialVO;
import com.bamisu.log.gameserver.module.characters.clas.ClassConfig;
import com.bamisu.log.gameserver.module.characters.clas.entities.ClassVO;
import com.bamisu.log.gameserver.module.characters.creep.CreepConfig;
import com.bamisu.log.gameserver.module.characters.creep.entities.CreepVO;
import com.bamisu.log.gameserver.module.characters.element.ElementConfig;
import com.bamisu.log.gameserver.module.characters.element.entities.ElementVO;
import com.bamisu.log.gameserver.module.characters.hero.*;
import com.bamisu.log.gameserver.module.characters.hero.entities.CharacterStatsGrowVO;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroSlotVO;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;
import com.bamisu.log.gameserver.module.characters.hero.entities.UpSizeBagHeroVO;
import com.bamisu.log.gameserver.module.characters.hut.ResetConfig;
import com.bamisu.log.gameserver.module.characters.hut.RetireConfig;
import com.bamisu.log.gameserver.module.characters.kingdom.KingdomConfig;
import com.bamisu.log.gameserver.module.characters.kingdom.entities.KingdomVO;
import com.bamisu.log.gameserver.module.characters.level.LevelHeroConfig;
import com.bamisu.log.gameserver.module.characters.mage.*;
import com.bamisu.log.gameserver.module.characters.mage.entities.StoneMageSlotVO;
import com.bamisu.log.gameserver.module.characters.mage.entities.StoneMageVO;
import com.bamisu.log.gameserver.module.characters.mboss.MbossConfig;
import com.bamisu.log.gameserver.module.characters.mboss.entities.MbossVO;
import com.bamisu.log.gameserver.module.characters.role.RoleConfig;
import com.bamisu.log.gameserver.module.characters.star.GraftHeroConfig;
import com.bamisu.log.gameserver.module.characters.star.StarHeroConfig;
import com.bamisu.log.gameserver.module.characters.star.entities.GraftHeroVO;
import com.bamisu.log.gameserver.module.characters.story.StoryConfig;
import com.bamisu.log.gameserver.module.characters.summon.KingdomSummonConfig;
import com.bamisu.log.gameserver.module.characters.summon.RateSummonConfig;
import com.bamisu.log.gameserver.module.characters.summon.SummonConfig;
import com.bamisu.log.gameserver.module.characters.summon.entities.BonusSummonVO;
import com.bamisu.log.gameserver.module.characters.summon.entities.KingdomSummonVO;
import com.bamisu.log.gameserver.module.characters.summon.entities.SummonVO;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.google.common.collect.Lists;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by Popeye on 10:10 AM, 10/9/2019
 */
public class CharactersConfigManager {
    private static Logger logger = Logger.getLogger(CharactersConfigManager.class);
    private ClassConfig classConfig;
    private ElementConfig elementConfig;
    private KingdomConfig kingdomConfig;
    private RoleConfig roleConfig;

    private PowerConfig powerConfig;
    private CharacterStatsGrowConfig heroStatsGrowConfig;
    private CharacterStatsGrowConfig heroStatsGrowMaxConfig;
    private CharacterStatsGrowConfig heroStatsGrowMinConfig;

    private BagHeroConfig bagHeroConfig;

    private HeroConfig heroConfig;
    private HeroNFTConfig heroNFTConfig;
    private HeroNFTConfig heroNFTMaxConfig;
    private HeroNFTConfig heroNFTMinConfig;

    private HeroSlotConfig heroSlotConfig;
    private LevelHeroConfig levelHeroConfig;
    private StarHeroConfig starHeroConfig;
    private StoryConfig storyConfig;

    private SummonConfig summonConfig;
    private RateSummonConfig rateSummonConfig;
    private KingdomSummonConfig kingdomSummonConfig;

    private BlessingConfig blessingConfig;
    private UnlockBlessingConfig unlockBlessingConfig;
    private GraftHeroConfig graftHeroConfig;

    private CreepConfig creepConfig;
    private CharacterStatsGrowConfig creepStatsGrowConfig;

    private MbossConfig mbossConfig;
    private CharacterStatsGrowConfig mBossStatsGrowConfig;

    private MageConfig mageConfig;
    private MageStatsGrowConfig mageStatsGrowConfig;
    private StoneMageConfig stoneMageConfig;
    private StoneMageSlotConfig stoneMageSlotConfig;
    private SkinMageConfig skinMageConfig;

    private CelestialConfig celestialConfig;
    private CharacterStatsGrowConfig celestialStatsGrowConfig;

    private RetireConfig retireConfig;
    private ResetConfig resetConfig;
    private String emailRegisterTemplate;
    private String emailForgotPasswordTemplate;

    private static CharactersConfigManager ourInstance = new CharactersConfigManager();

    public static CharactersConfigManager getInstance() {
        return ourInstance;
    }

    private CharactersConfigManager() {
        loadConfig();
    }

    private void loadConfig() {
        long startTime = System.currentTimeMillis() / 1000;
        classConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_CLASS), ClassConfig.class);
        elementConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_ELEMENT), ElementConfig.class);
        kingdomConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_KINGDOM), KingdomConfig.class);
        roleConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_ROLE), RoleConfig.class);

        powerConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_POWER), PowerConfig.class);
        heroStatsGrowConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_HERO_STATS_GROW), CharacterStatsGrowConfig.class);
        heroStatsGrowMaxConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_HERO_STATS_GROW_MAX), CharacterStatsGrowConfig.class);
        heroStatsGrowMinConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_HERO_STATS_GROW_MIN), CharacterStatsGrowConfig.class);

        bagHeroConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_NAG_HERO), BagHeroConfig.class);

        heroConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_HERO), HeroConfig.class);
        heroNFTConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_HERO_NFT), HeroNFTConfig.class);
        heroNFTMaxConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_HERO_NFT_MAX), HeroNFTConfig.class);
        heroNFTMinConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_HERO_NFT_MIN), HeroNFTConfig.class);

        heroSlotConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_HERO_SLOT), HeroSlotConfig.class);
        levelHeroConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_LEVEL_HERO), LevelHeroConfig.class);
        starHeroConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_STAR_HERO), StarHeroConfig.class);
        storyConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_STORY_HERO), StoryConfig.class);

        summonConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_SUMMON), SummonConfig.class);
        rateSummonConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_RATE_SUMMON), RateSummonConfig.class);
        kingdomSummonConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_KINGDOM_SUMMON), KingdomSummonConfig.class);

        graftHeroConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_GRAFT_HERO), GraftHeroConfig.class);
        blessingConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_BLESSING), BlessingConfig.class);
        unlockBlessingConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_UNLOCK_SLOT_BLESSING), UnlockBlessingConfig.class);

        creepConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_CREEP), CreepConfig.class);
        creepStatsGrowConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_CREEP_STATS_GROW), CharacterStatsGrowConfig.class);

        mbossConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_MBOSS), MbossConfig.class);
        mBossStatsGrowConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_MBOSS_STATS_GROW), CharacterStatsGrowConfig.class);

        mageConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_MAGE), MageConfig.class);
        mageStatsGrowConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_MAGE_STATS_GROW), MageStatsGrowConfig.class);
        stoneMageConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_STONE_MAGE), StoneMageConfig.class);
        stoneMageSlotConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_STONE_MAGE_SLOT), StoneMageSlotConfig.class);
        skinMageConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_SKIN_MAGE), SkinMageConfig.class);

        celestialConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_CELESTIAL), CelestialConfig.class);
        celestialStatsGrowConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_CELESTIAL_STATS_GROW), CharacterStatsGrowConfig.class);

        retireConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_RETIRE_HERO), RetireConfig.class);
        resetConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_RESET_HERO), ResetConfig.class);
        this.emailRegisterTemplate = Utils.loadConfig(ServerConstant.User.FILE_PATH_EMAIL_REGISTER);
        this.emailForgotPasswordTemplate = Utils.loadConfig(ServerConstant.User.FILE_PATH_EMAIL_FORGOT_PASSWORD);
        long endTime = System.currentTimeMillis() / 1000;
        logger.info(endTime - startTime);
    }


    private List<HeroVO> list3StarHero = null;




    /* --------------------------------------------------------------------------------------------------------------*/
    /* --------------------------------------------------------------------------------------------------------------*/

    /**
     * Lay tai nguyen update ngay user summon model
     *
     * @param id
     * @return
     */
    public List<MoneyPackageVO> getCostUpdateDayUserSummonModel(String id) {
        switch (HeroResource.fromID(id)) {
            case KINGDOM:
                return getKingdomSummonConfig(id).getCostUpdateKingdomDay();
            case ELEMENT:
                return getElementConfig(id).getCostUpdateElementDay();
        }
        return null;
    }

    public List<Integer> getBreakThoughtConfig(){
        return heroStatsGrowConfig.breakThoughtList;
    }

    public CharacterStatsGrowVO getHeroStatsGrowConfig(String idHero) {
        return heroStatsGrowConfig.readHeroStatsGrowConfig(idHero);
    }
    public CharacterStatsGrowVO getHeroNFTStatsGrowConfig(String idHero) {
        CharacterStatsGrowVO characterStatsGrowMaxCf = getHeroStatsGrowMaxConfig(idHero);
        CharacterStatsGrowVO characterStatsGrowMinCf = getHeroStatsGrowMinConfig(idHero);

        CharacterStatsGrowVO characterStatsGrowCf = CharacterStatsGrowVO.create(getHeroStatsGrowConfig(idHero));
        characterStatsGrowCf.enhanceLevel.hp = (float) Utils.randomInRange(characterStatsGrowMaxCf.enhanceLevel.hp, characterStatsGrowMinCf.enhanceLevel.hp);
        characterStatsGrowCf.enhanceLevel.strength = (float) Utils.randomInRange(characterStatsGrowMaxCf.enhanceLevel.strength, characterStatsGrowMinCf.enhanceLevel.strength);
        characterStatsGrowCf.enhanceLevel.intelligence = (float) Utils.randomInRange(characterStatsGrowMaxCf.enhanceLevel.intelligence, characterStatsGrowMinCf.enhanceLevel.intelligence);
        characterStatsGrowCf.enhanceLevel.armor = (float) Utils.randomInRange(characterStatsGrowMaxCf.enhanceLevel.armor, characterStatsGrowMinCf.enhanceLevel.armor);
        characterStatsGrowCf.enhanceLevel.magicResistance = (float) Utils.randomInRange(characterStatsGrowMaxCf.enhanceLevel.magicResistance, characterStatsGrowMinCf.enhanceLevel.magicResistance);
        characterStatsGrowCf.enhanceLevel.dexterity = (float) Utils.randomInRange(characterStatsGrowMaxCf.enhanceLevel.dexterity, characterStatsGrowMinCf.enhanceLevel.dexterity);
        characterStatsGrowCf.enhanceLevel.agility = (float) Utils.randomInRange(characterStatsGrowMaxCf.enhanceLevel.agility, characterStatsGrowMinCf.enhanceLevel.agility);
        characterStatsGrowCf.enhanceLevel.elusiveness = (float) Utils.randomInRange(characterStatsGrowMaxCf.enhanceLevel.elusiveness, characterStatsGrowMinCf.enhanceLevel.elusiveness);
        characterStatsGrowCf.enhanceLevel.armorPenetration = (float) Utils.randomInRange(characterStatsGrowMaxCf.enhanceLevel.armorPenetration, characterStatsGrowMinCf.enhanceLevel.armorPenetration);
        characterStatsGrowCf.enhanceLevel.magicPenetration = (float) Utils.randomInRange(characterStatsGrowMaxCf.enhanceLevel.magicPenetration, characterStatsGrowMinCf.enhanceLevel.magicPenetration);
        characterStatsGrowCf.enhanceLevel.crit = (float) Utils.randomInRange(characterStatsGrowMaxCf.enhanceLevel.crit, characterStatsGrowMinCf.enhanceLevel.crit);
        characterStatsGrowCf.enhanceLevel.critDmg = (float) Utils.randomInRange(characterStatsGrowMaxCf.enhanceLevel.critDmg, characterStatsGrowMinCf.enhanceLevel.critDmg);
        characterStatsGrowCf.enhanceLevel.tenacity = (float) Utils.randomInRange(characterStatsGrowMaxCf.enhanceLevel.tenacity, characterStatsGrowMinCf.enhanceLevel.tenacity);

        return characterStatsGrowCf;
    }
    private CharacterStatsGrowVO getHeroStatsGrowMaxConfig(String idHero) {
        return heroStatsGrowMaxConfig.readHeroStatsGrowConfig(idHero);
    }
    private CharacterStatsGrowVO getHeroStatsGrowMinConfig(String idHero) {
        return heroStatsGrowMinConfig.readHeroStatsGrowConfig(idHero);
    }

    public CharacterStatsGrowVO getMBossStatsGrowConfig(String idHero) {
        return mBossStatsGrowConfig.readHeroStatsGrowConfig(idHero);
    }

    public CharacterStatsGrowVO getCreepStatsGrowConfig(String idHero) {
        return creepStatsGrowConfig.readHeroStatsGrowConfig(idHero);
    }

    public CharacterStatsGrowVO getCelestialStatsGrowConfig(String idCelestial) {
        return celestialStatsGrowConfig.readHeroStatsGrowConfig(idCelestial);
    }



    /* --------------------------------------------------------------------------------------------------------------------*/
    /* ------------------------------------------------------ CHANGE CONFIG -----------------------------------------------*/

    /* ------------------------------------------------------- KINGDOM ----------------------------------------------------*/
    /**
     * Get List Kingdom Config
     */
    public List<KingdomVO> getKingdomConfig() {
        return kingdomConfig.listKingdom.parallelStream().filter(obj -> EStatus.COMMING_SOON.getId() != obj.status).collect(Collectors.toList());
    }



    /* ------------------------------------------------------ CLASS -------------------------------------------------------*/

    public ClassVO getClassConfigDependName(String nameClass) {
        for (ClassVO classVO : classConfig.listClass) {
            if (classVO.name.toLowerCase().equals(nameClass.toLowerCase())) {
                return classVO;
            }
        }
        return null;
    }

    /* ----------------------------------------------------- ELEMENT -------------------------------------------------------*/


    /**
     * Get Element depend ID
     */
    public ElementVO getElementConfig(String id) {
        for (ElementVO element : elementConfig.listElement) {
            if (element.id.equals(id)) {
                return element;
            }
        }
        return null;
    }

    /**
     * Get List Element
     */
    public List<ElementVO> getElementConfig() {
        return elementConfig.listElement;
    }


    /* ------------------------------------------------------ HERO --------------------------------------------------------*/

    /**
     * Get HeroVO Config depend ID
     */
    public HeroVO getHeroConfig(String id) {
        return heroConfig.readHero(id);
    }
    public HeroVO getHeroNFTConfig(String id) {
        return heroNFTConfig.readHero(id);
    }
    public HeroVO getHeroNFTMaxConfig(String id) {
        return heroNFTMaxConfig.readHero(id);
    }
    public HeroVO getHeroNFTMinConfig(String id) {
        return heroNFTMinConfig.readHero(id);
    }

    public HeroVO getHeroNFTStatsConfig(String id) {
        HeroVO heroMaxCf = getHeroNFTMaxConfig(id);
        HeroVO heroMinCf = getHeroNFTMinConfig(id);

        HeroVO heroCf = HeroVO.createHero(getHeroConfig(id));
        heroCf.health = (float) Utils.randomInRange(heroMaxCf.health, heroMinCf.health);
        heroCf.strength = (float) Utils.randomInRange(heroMaxCf.strength, heroMinCf.strength);
        heroCf.intelligence = (float) Utils.randomInRange(heroMaxCf.intelligence, heroMinCf.intelligence);
        heroCf.armor = (float) Utils.randomInRange(heroMaxCf.armor, heroMinCf.armor);
        heroCf.magicResistance = (float) Utils.randomInRange(heroMaxCf.magicResistance, heroMinCf.magicResistance);
        heroCf.agility = (float) Utils.randomInRange(heroMaxCf.agility, heroMinCf.agility);
        heroCf.crit = (float) Utils.randomInRange(heroMaxCf.crit, heroMinCf.crit);
        heroCf.armorPenetration = (float) Utils.randomInRange(heroMaxCf.armorPenetration, heroMinCf.armorPenetration);
        heroCf.magicPenetration = (float) Utils.randomInRange(heroMaxCf.magicPenetration, heroMinCf.magicPenetration);

        return heroCf;
    }


    public List<HeroVO> getHeroConfig(List<String> listId) {
        List<HeroVO> list = new ArrayList<>();
        HeroVO heroVO;
        for (String id : listId) {
            heroVO = getHeroConfig(id);
            if (heroVO == null) continue;
            list.add(heroVO);
        }
        return list;
    }

    /**
     * Get RANDOM HeroVO config
     */
    public HeroVO getHeroConfig(String idHero, int startStar) {
        HeroVO heroVO = getHeroConfig(idHero);
        if (heroVO == null || heroVO.star > startStar) {
            return null;
        }
        heroVO.star = (byte) startStar;
        return HeroVO.createHero(heroVO);
    }

    /**
     *  lấy ngẫu nhiên hero tím, gán luôn star chuyền vào
     * @param star
     * @return
     */
    public HeroVO getRandom3StarHeroConfig(int star) {
        if(list3StarHero == null){
            list3StarHero = new ArrayList<>();
            for (HeroVO heroVO : getHeroConfig()) {
                if (heroVO.star == ColorHero.PURPLE.getStar()) {
                    list3StarHero.add(heroVO);
                }
            }
        }
        HeroVO heroVO = HeroVO.createHero(list3StarHero.get(new Random().nextInt(list3StarHero.size())));
        heroVO.star = (byte) star;
        return heroVO;
    }

    public HeroVO getRandomHeroConfig(int star, String kingdom, String clas, String element) {
        List<HeroVO> choice = new ArrayList<>();

        for (HeroVO hero : getHeroConfig()) {
            if (hero.star != star) {
                continue;
            }
            if ((kingdom != null && !hero.kingdom.equals(kingdom) || (star == 4 && kingdom == null && hero.kingdom.equalsIgnoreCase("K6")))) {
                continue;
            }
            if (clas != null && !hero.clas.equals(clas)) {
                continue;
            }
            if (element != null && !hero.element.equals(element)) {
                continue;
            }
            choice.add(hero);
        }
        int rd = Utils.randomInRange(0, choice.size() - 1);
        if (rd < 0) {
            return null;
        }

        //        hero.star = (byte) star;
        return HeroVO.createHero(choice.get(rd));
    }


    /**
     * Get List HeroVO Config
     */
    public List<HeroVO> getHeroConfig() {
        return heroConfig.readListHero();
    }


    /**
     * Get List HeroVO depend DEPEND_KINGDOM + CLASS + DEPEND_ELEMENT
     */
    public List<HeroVO> getHeroConfigBy(String kingdom, String clas, String element) {
        List<HeroVO> choose = new ArrayList<>();
        HeroVO heroCf;
        for (HeroVO hero : getHeroConfig()) {
            heroCf = hero;
            if (kingdom != null && !hero.kingdom.equals(kingdom)) {
                heroCf = null;
            }
            if (clas != null && !hero.clas.equals(clas)) {
                heroCf = null;
            }
            if (element != null && !hero.element.equals(element)) {
                heroCf = null;
            }
            if (heroCf != null) {
                choose.add(heroCf);
            }
        }
        return choose;
    }

    /**
     * Get Config Slot
     */
    public List<HeroSlotVO> getHeroSlotConfig() {
        return heroSlotConfig.listHeroSlot.parallelStream().
                map(HeroSlotVO::new).
                collect(Collectors.toList());
    }



    /*---------------------------------------------------- STORY ---------------------------------------------------------*/

    /**
     * Get bonus story
     */
    public short getBonusStoryHero(String idHero) {
        for (HeroVO heroVO : getHeroConfig()) {
            if (heroVO.id.equals(idHero)) {
                return heroVO.bonusDiamond;
            }
        }
        return 0;
    }




    /*------------------------------------------------------ LEVEL HERO ---------------------------------------------------*/

    /**
     * Chech max level
     */
    public boolean isMaxLevelHeroConfig(int star, int level) {
        if (level <= 0) {
            return false;
        }
        if (getMaxLevelHeroConfig(star) <= level) {
            return true;
        }
        return false;
    }

    /**
     * Get level max config
     *
     * @param star
     * @return
     */
    public int getMaxLevelHeroConfig(int star) {
        return getStarHeroConfig().maxLevel.getOrDefault((short) star, 0);
    }

    public int getMaxLevelBlessingHeroConfig(String idHero) {
        HeroVO heroCf = getHeroConfig(idHero);
        return getMaxLevelHeroConfig(heroCf.maxStar);
    }


    /**
     * Lay config tai nguyen nang cap level hero
     */
    public List<MoneyPackageVO> getCostUpLevelHeroConfig(int level) {
        return levelHeroConfig.levelup.getOrDefault((short) level, new ArrayList<>()).stream().
                map(MoneyPackageVO::new).
                collect(Collectors.toList());
    }

    /**
     * Lay list level config dot pha da sap xep
     */
    public List<Short> getListLevelBreakThoughtConfig() {
        return levelHeroConfig.readLevelBreakThought();
    }

    /**
     * Get Config BreakLimit HeroVO
     */
    public Map<Short, List<MoneyPackageVO>> getBreakLimitHeroConfig() {
        return levelHeroConfig.breakLimit;
    }

    /**
     * Get tai nguyen config dot pha tuong
     */
    public List<MoneyPackageVO> getCostBreakLimitLevelHero(int level) {
        return getBreakLimitHeroConfig().getOrDefault((short) level, new ArrayList<>()).stream().
                map(obj -> new MoneyPackageVO(obj.id, obj.amount)).
                collect(Collectors.toList());
    }


    /*------------------------------------------------- STAR HERO ---------------------------------------------------------*/
    public int getMaxStarHeroConfig(String idHero) {
        return getHeroConfig(idHero).maxStar;
    }

    /**
     * Get Star HeroVO Config
     */
    public StarHeroConfig getStarHeroConfig() {
        return starHeroConfig;
    }


    /**
     * Get Cost Up Star HeroVO (Graft Hero Config)
     */
    public List<GraftHeroVO> getGraftHeroConfig() {
        return graftHeroConfig.condition;
    }

    public GraftHeroVO getGraftHeroConfig(int starHero) {
        for (GraftHeroVO cf : getGraftHeroConfig()) {
            if (cf.star == starHero) {
                return cf;
            }
        }
        return null;
    }



    /*---------------------------------------------------- SUMMON ----------------------------------------------------*/

    /**
     * Get Summon Banner Config
     */
    public SummonConfig getSummonConfig() {
        return summonConfig;
    }

    public RateSummonConfig getRateSummonConfig(){
        return rateSummonConfig;
    }

    public List<KingdomSummonVO> getKingdomSummonConfig() {
        return kingdomSummonConfig.listKingdom.parallelStream().filter(obj -> EStatus.ENABLE.getId() == obj.status).collect(Collectors.toList());
    }

    public KingdomSummonVO getKingdomSummonConfig(String id) {
        for (KingdomSummonVO kingdom : kingdomSummonConfig.listKingdom) {
            if (kingdom.id.equals(id) && kingdom.status != EStatus.COMMING_SOON.getId()) {
                return kingdom;
            }
        }
        return null;
    }

    /**
     * Get Summon Config depend ID
     */
    public List<SummonVO> getSummonBannerConfig() {
        return getSummonConfig().listSummonBanner;
    }
    public SummonVO getSummonBannerConfig(String idSummon) {
        for (SummonVO summonCf : getSummonBannerConfig()) {
            if (summonCf.id.equals(idSummon)) {
                return summonCf;
            }
        }
        return null;
    }
    public SummonVO getSummonConfig(String idSummon) {
        SummonVO summonCf = getSummonBannerConfig(idSummon);
        if(summonCf != null) return summonCf;
        for (SummonVO summon : getRateSummonConfig().listSummonRate) {
            if (summon.id.equals(idSummon)) {
                return summon;
            }
        }
        return null;
    }

    /**
     * Get Bonus Point Config
     */
    public short getBonusPointSummonConfig(String idSummon) {
        SummonVO summonCf = getSummonConfig(idSummon);
        if (summonCf == null) {
            return 0;
        }
        return summonCf.bonusPoint;
    }

    /**
     * Get Time (second) to Free Summon
     */
    public int getDistanceTimeSummonFreeConfig(String idSummon) {
        SummonVO summonCf = getSummonConfig(idSummon);
        if (summonCf == null) {
            return -1;
        }
        return summonCf.timeFree;
    }

    /**
     * Get bonus summon cofnig
     */
    public List<BonusSummonVO> getBonusSummonConfig() {
        return summonConfig.bonus;
    }

    public BonusSummonVO getBonusSummonConfig(String id) {
        for (BonusSummonVO bonus : summonConfig.bonus) {
            if (bonus.idChest.equals(id)) {
                return bonus;
            }
        }
        return null;
    }


    /*--------------------------------------------------- BLESSING ---------------------------------------------------*/
    public int getTimeReblessingConfig() {
        return blessingConfig.time;
    }

    public ReduceTimeVO getReduceTimeBlessingConfig() {
        return blessingConfig.reduceTime;
    }


    public UnlockBlessingConfig getUnlockBlessingConfig() {
        return unlockBlessingConfig;
    }


    /* ----------------------------------------------------  MBOSS  -----------------------------------------------------*/
    public List<MbossVO> getMbossConfig() {
        return mbossConfig.listMBoss;
    }

    public MbossVO getMbossConfig(String id) {
        return mbossConfig.readMbossVO(id);
    }

    public MbossVO getRandomMbossConfig() {
        List<MbossVO> choice = getMbossConfig();
        MbossVO boss;
        do {
            boss = MbossVO.createMboss(choice.get(Utils.randomInRange(0, choice.size() - 1)));
        } while (boss.tag != 0);

        return boss;
    }


    /* ----------------------------------------------------- CREEP ------------------------------------------------------*/
    // chỉ lấy creep tag = 0
    public CreepVO getRandomCreepConfig() {
        List<CreepVO> choice = getCreepConfig();
        CreepVO creep;
        do {
            creep = CreepVO.createCreep(choice.get(Utils.randomInRange(0, choice.size() - 1)));
        } while (creep.tag != 0);

        return creep;
    }

    /**
     * Get CreepVO Config depend ID
     */
    public CreepVO getCreepConfig(String id) {
        return creepConfig.readCreepConfig(id);
    }

    /**
     * Get List CreepVO Config
     */
    public List<CreepVO> getCreepConfig() {
        return creepConfig.listCreep;
    }




    /* ------------------------------------------------------ MAGE --------------------------------------------------------*/

    /**
     * Get Mage Config
     */
    public MageConfig getMageConfig() {
        return mageConfig;
    }

    /**
     * Get Config Slot
     */
    public List<StoneMageSlotVO> getStoneMageSlotConfig() {
        return stoneMageSlotConfig.listStoneSlot;
    }

    public Stats getMageStatsGrowConfig() {
        return mageStatsGrowConfig.enhanceLevel;
    }


    /*----------------------------------------------------- STONE MAGE -----------------------------------------------------*/
    public StoneMageVO getStoneMageConfig(String idStone) {
        for (StoneMageVO index : stoneMageConfig.listStone) {
            if (index.id.equals(idStone)) {
                return index;
            }
        }
        return null;
    }

    public List<StoneMageVO> getListStoneMageConfig() {
        return stoneMageConfig.listStone;
    }



    /*--------------------------------------------------- CELESTIAL -------------------------------------------------------*/

    /**
     * Get linh thu config
     */
    public List<CelestialVO> getCelestialConfig() {
        return celestialConfig.list;
    }

    public CelestialVO getCelestialConfig(String id) {
        for (CelestialVO index : getCelestialConfig()) {
            if (index.id.equals(id)) {
                return index;
            }
        }
        return null;
    }


    /*--------------------------------------------------  RETIRE HERO  -----------------------------------------------*/

    /**
     * Kiem tra co the phan giai tuong khong
     *
     * @param star
     * @return
     */
    public boolean canRetireHero(int star) {
        return retireConfig.star.contains((short) star);
    }

    public List<ResourcePackage> getResourceRetireHeroConfig(int star) {
        return retireConfig.resource.getOrDefault((short) star, new ArrayList<>());
    }

    public List<MoneyPackageVO> getCostResetConfig() {
        return Lists.newArrayList(resetConfig.cost);
    }




    /*------------------------------------------------- BAG HERO -----------------------------------------------------*/

    /**
     * Config tui tuong
     *
     * @return
     */
    public BagHeroConfig getBagHeroConfig() {
        return bagHeroConfig;
    }

    public List<ResourcePackage> getCostUpSizeBagHeroConfig(int countIncreate) {
        List<UpSizeBagHeroVO> cf = getBagHeroConfig().list;
        for (int i = cf.size() - 1; i >= 0; i--) {
            if (cf.get(i).count <= countIncreate) {
                return new ArrayList<>(Collections.singleton(cf.get(i).cost));
            }
        }
        return new ArrayList<>(Collections.singleton(cf.get(0).cost));
    }

    public ISFSObject getAllCharacter() {
        ISFSObject data = new SFSObject();
        ISFSObject object = new SFSObject();
        ISFSArray array = new SFSArray();
        data.putSFSArray("list", array);
        for(MbossVO mbossVO : mbossConfig.listMBoss){
            object = new SFSObject();
            object.putUtfString("id", mbossVO.id);
            object.putUtfString("kingdom", mbossVO.readkingdom());
            object.putInt("type", ECharacterType.IdToType(mbossVO.id));
            array.addSFSObject(object);
        }
        for(CreepVO creepVO : creepConfig.listCreep){
            object = new SFSObject();
            object.putUtfString("id", creepVO.id);
            object.putUtfString("kingdom", creepVO.readkingdom());
            object.putInt("type", ECharacterType.IdToType(creepVO.id));
            array.addSFSObject(object);
        }
        for(HeroVO heroVO : heroConfig.listHero){
            object = new SFSObject();
            object.putUtfString("id", heroVO.id);
            object.putUtfString("kingdom", heroVO.readkingdom());
            object.putInt("type", ECharacterType.IdToType(heroVO.id));
            array.addSFSObject(object);
        }
        return data;
    }

    public String getEmailRegisterTemplate() {
        return this.emailRegisterTemplate;
    }

    public String getEmailForgotPasswordTemplate() {
        return this.emailForgotPasswordTemplate;
    }
}