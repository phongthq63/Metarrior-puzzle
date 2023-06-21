package com.bamisu.gamelib.entities;

import com.bamisu.gamelib.base.config.ConfigHandle;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

public class ServerConstant {

    public static final String TIME_ZONE = ConfigHandle.instance().get("time_zone");
    public static final ZoneId TIME_ZONE_ID = ZoneId.of(ServerConstant.TIME_ZONE);
    public static final String VERSION = "1.0.1 190420";

    public static boolean PRE_MAINTENANCE = Boolean.parseBoolean(ConfigHandle.instance().get("update_code_time"));
    public static List<String> white_list_ip = Arrays.asList(ConfigHandle.instance().get("white_list_ip").split(","));
    public static List<String> white_list_account = Arrays.asList(ConfigHandle.instance().get("white_list_account").split(","));
    public static List<String> white_list_device = Arrays.asList(ConfigHandle.instance().get("white_list_device").split(","));

    public static final String ZONE_NAME = "s1";
    public static final String ID = "id";
    public static String rest_secret = "admin";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SEPARATER = "_";

    public class ErrorCode {
        public static final short NONE = 0;

        // sys 1-100
        public static final short ERR_SYS = 100;
        public static final short ERR_SERVER_PRE_MAINTENANCE = 101;

        public static final short ERR_BAD_EXTENSION = 102;

        // main
        // user 101-200
        // user
        public static final short ERR_USER_NOT_FOUND = 201;
        public static final short ERR_PASS_LONG = 208;
        public static final short ERR_EMAIL_INVALID = 219;
        public static final short ERR_INVALID_GENDER = 220;
        public static final short ERR_INVALID_DNAME = 221;
        public static final short ERR_INVALID_LANGUAGE_ID = 222;
        public static final short ERR_INVALID_STATUS_TEXT = 223;
        public static final short ERR_NOT_EXSIST_CHANNEL = 224;
        public static final short ERR_FULL_CHANNEL = 226;
        public static final short ERR_INVALID_CHANNEL = 227;
        public static final short ERRSendingEmailTooFastException = 228;
        public static final short ERR_Send_Email_FAIL = 229;
        public static final short ERR_ALREADY_IN_CHANNEL = 230;
        public static final short ERR_DNAME_ALREADY_EXIST = 231;
        public static final short ERR_INVALID_TOKEN_LOGIN = 232;
        public static final short ERR_INVALID_USERNAME_OR_PASSWORD = 233;
        public static final short ERR_USERNAME_ALREADY_EXIST = 234;
        public static final short ERR_EMAIL_ALREADY_EXIST = 235;
        public static final short ERR_CODE_INVALID = 236;
        public static final short ERR_ACCOUNT_NOT_ACTIVATED = 237;
        public static final short ERR_WALLET_ADDRESS_EXISTED = 238;
        public static final short ERR_WALLET_ADDRESS_INVALID = 239;
        public static final short ERR_ACCOUNT_LINKED = 240;
        public static final short ERR_ACCOUNT_NOT_LINK_SOCIAL = 241;


        //mail 301-400
        public static final short ERR_MAIL_TITLE_INVALID = 301;
        public static final short ERR_MAIL_CONTENT_INVALID = 302;

        //Character
        public static final short ERR_CHAR = 1300;
        public static final short ERR_MAX_SIZE_BAG_HERO = 1301;
        public static final short ERR_INVALID_UP_LEVEL_HERO = 1302;
        public static final short ERR_NOT_EXSIST_CHARACTER = 1311;
        public static final short ERR_LIMIT_LEVEL_CHARACTER = 1312;
        public static final short ERR_NOT_ENOUGH_RESOURCE = 1313;
        public static final short ERR_NOT_EXSIST_ITEM = 1314;
        public static final short ERR_NOT_EQUIP_ITEM = 1315;
        public static final short ERR_INVALID_LIST_EQUIP = 1316;
        public static final short ERR_ALREADY_GET_BONUS_STORY = 1317;
        public static final short ERR_CHARACTER_EQUIP_ITEM_THEMSELF = 1318;
        public static final short ERR_NOT_HAVE_TO_UNEQUIP_QUICK = 1320;
        public static final short ERR_NOT_EXSIST_SUMMON_BANNER = 1321;
        public static final short ERR_INVALID_TEAM_TYPE = 1322;
        public static final short ERR_INVALID_TEAM = 1332;
        public static final short ERR_INVALID_SUMMON_TYPE = 1323;
        public static final short ERR_INVALID_HERO_BLESSING = 1324;
        public static final short ERR_HERO_BEING_BLESSING = 1327;
        public static final short ERR_DONT_HAVE_COUNTDOWN_BLESSING = 1328;
        public static final short ERR_INVALID_SLOT_BLESSING = 1329;
        public static final short ERR_MAX_BUY_SLOT_BLESSING = 1330;
        public static final short ERR_INVALID_HERO_RESET = 1325;
        public static final short ERR_HERO_MAX_STAR = 1331;
        public static final short ERR_INVALID_HERO_FISSION = 1332;
        public static final short ERR_LIMIT_BORROW_HERO_FRIEND = 1333;
        public static final short ERR_BORROW_MORE_THAN_EXPECT_HERO_FRIEND = 1334;
        public static final short ERR_LIMIT_UP_SIZE_BAG_HERO = 1335;
        public static final short ERR_INVALID_HERO_UP_STAR = 1336;
        public static final short ERR_NOT_ENOUGHT_CONDITION_SUMMON = 1337;
        public static final short ERR_COUNTDOWN = 1338;
        public static final short ERR_HERO_ON_SALE = 1339;
        public static final short ERR_HERO_USING_CHANGE_ENERY = 1340;

        //Item
        public static final short ERR_EXSIST_AVATAR = 1401;
        public static final short ERR_NOT_EXSIST_AVATAR = 1402;
        public static final short ERR_INVALID_MONEY_TYPE = 1403;

        //WEAPON 1600 -> 1700
        public static final short ERR_WRONG_WEAPON = 1600;
        public static final short ERR_NOT_EXSIST_STONE = 1601;
        public static final short ERR_NOT_ENOUGH_MONEY = 1602;
        public static final short ERR_STONE_IN_EQUIP = 1603;
        public static final short ERR_NOT_HAVE_EQUIP = 1604;
        public static final short ERR_MAX_LEVEL = 1606;
        public static final short ERR_WRONG_SIZE_TO_FUSION_WEAPON = 1607;
        public static final short ERR_DIFFERNT_STAR_WHEN_FUSION = 1608;
        public static final short ERR_MAX_STAR = 1610;
        public static final short ERR_NOT_ENOUGH_FRAGMENT = 1613;
        public static final short ERR_MAX_STAR_FUSION = 1614;


        //MAGE
        public static final short ERR_NOT_EXSIST_STONE_MAGE = 1700;
        public static final short ERR_NOT_EXSIST_ITEM_MAGE = 1701;
        public static final short ERR_NOT_ENOUGH_MONEY_MAGE = 1702;
        public static final short ERR_NOT_EXSIST_SKIN_MAGE = 1702;

        //REWARD
        public static final short ERR_NOT_ENOUGH_TURN_TO_GET_REWARD = 1704;
        public static final short ERR_CLAIM_REWARD = 1705;

        // BUY LUCKY
        public static final short ERR_TICKET_DUPLICATE = 1706;
        public static final short ERR_OVER_TIME_TO_BUY_TICKET = 1707;

        //GUILD
        public static final short ERR_EXSIST_NAME_GUILD = 1800;
        public static final short ERR_NOT_EXSIST_GUILD = 1801;
        public static final short ERR_USER_NOT_IN_GUILD = 1802;
        public static final short ERR_NOT_HAVE_PERMISSION = 1803;
        public static final short ERR_INVALID_NAME = 1804;
        public static final short ERR_INVALID_AVATAR = 1805;
        public static final short ERR_ALREADY_CHECK_IN_GUILD = 1806;
        public static final short ERR_NOT_EXSIST_VERIFY_GUILD = 1807;
        public static final short ERR_USER_IN_GUILD = 1808;
        public static final short ERR_GUILD_MAX_MEMBER = 1809;
        public static final short ERR_NOT_EXSIST_REQUEST_POWER_GUILD = 1810;
        public static final short ERR_CURRENT_CAN_NOT_LEAVE_GUILD = 1811;
        public static final short ERR_CURRENT_CAN_NOT_JOIN_GUILD = 1812;
        public static final short ERR_NOT_ENOUGHT_REQUEST_GUILD = 1813;
        public static final short ERR_NOT_EXSIST_GIFT_GUILD = 1814;
        public static final short ERR_SEARCH_TOO_FAST = 1815;
        public static final short ERR_ALREADY_CLAIM_GIFT_GUILD = 1816;

        //CELESTIAL
        public static final short ERR_CHOOSE_INVALID_CELESTIAL = 1900;
        public static final short ERR_UNLOCKED_CELESTIAL = 1901;
        public static final short ERR_CAN_NOT_UNLOCK_CELESTIAL = 1902;
        public static final short ERR_NOT_EXSIST_CELESTIAL = 1903;
        public static final short ERR_ALREADY_USE_CELESTIAL = 1904;

        //MISSION
        public static final short ERR_INVALID_MISSION = 2000;

        //CHAT
        public static final short ERR_CHAT_TOO_FAST = 2100;
        public static final short ERR_CHAT_TO_YOURSEFT = 2101;

        //auth
        public static final short ACCOUNT_HAVE_LINKED = 2200;
        public static final short SOCIAL_ACCOUNT_HAVE_LINKED = 2201;

        //giftcode
        public static final short GIFTCODE_NOT_FOUND = 3000;
        public static final short GIFTCODE_EXPIRED = 3001;
        public static final short GIFTCODE_GONE = 3002;
        public static final short HAVE_USED = 3003;

        //invitecode
        public static final short ERR_INVITE_CODE_NOT_EXSIST = 3100;
        public static final short ERR_ALREADY_INPUT_INVITE_CODE = 3101;
        public static final short ERR_INVALID_REWARD_INVITE_CODE = 3102;
        public static final short ERR_INVALID_INVITE_CODE = 3103;
        public static final short ERR_INVALID_REWARD_INVITE_CODE_NOT_ENOUGHT_HERO_NFT = 3104;

        //arena
        public static final short ERR_REFRESH_ARENA_TO_FAST = 3200;
        public static final short ERR_END_SEASON_ARENA = 3201;
        public static final short ERR_WRONG_ANEMY_MATCH_ARENA = 3202;

        //IAP
        public static final short ERR_NOT_EXSIST_TAB = 2300;
        public static final short ERR_CAN_NOT_CLAIM_PACKAGE = 2301;
        public static final short ERR_NOT_EXSIST_PACKAGE = 2302;
        public static final short ERR_NOT_HAVE_PAYMENT = 2303;
        public static final short ERR_INVALID_PAYMENT = 2304;

        //Friends
        public static final short ERR_MAX_LIST_FRIENDS = 2400;
        public static final short ERR_RECEIVED_ENOUGH_POINT = 2401;
        public static final short ERR_LIST_BLOCK_FULL = 2402;
        public static final short ERR_MAX_LIST_FRIENDS_OF_FRIEND = 2403;
        public static final short ERR_USER_NOT_EXIST = 2404;
        public static final short ERR_USER_BLOCKED = 2405;
        public static final short ERR_FRIEND_EXIST = 2406;
        public static final short ERR_SENT_REQUEST = 2407;

        //Vip
        public static final short ERR_GIFT_CLAIMED = 2500;
        public static final short ERR_NOT_ENOUGH_LEVEL = 2501;

        //Hunt
        public static final short ERR_HUNT_OUT = 2600;
        public static final short ERR_INCREASE_HUNT_OUT = 2601;

        //Tower
        public static final short ERR_MAX_FLOOR_TOWER = 2700;

        //Campaign
        public static final short ERR_CURRENT_CANT_UPDATE_AREA_CAMPAIGN = 2800;
        public static final short ERR_INVALID_FIGHT_STATION_CAMPAIGN = 2801;
        public static final short ERR_INVALID_BUY_ITEM_CAMPAIGN = 2802;

        //Quest
        public static final short ERR_CANT_COMPLETE_QUEST = 2900;
        public static final short ERR_CANT_REWARD_CHEST_QUEST = 2901;
        public static final short ERR_NOT_EXSIST_QUEST = 2902;
        public static final short ERR_NOT_EXSIST_CHEST_QUEST = 2903;

        //Event
        public static final short ERR_EVENT_HAVE_ENDED = 3300;
        public static final short ERR_HAVE_LIMIT_BUY_IN_EVENT = 3301;
        public static final short ERR_EVENT_DONT_EXSIST = 3302;

        //Energy
        public static final short ERR_NOT_EXSIST_CHARGE_ENERGY = 3400;
        public static final short ERR_NOT_ENOUGHT_CONDITION_CHARGR_ENERGY = 3401;
        public static final short ERR_OUT_CHARGR_ENERGY = 3402;
        public static final short ERR_UPDATE_ENERGY_HERO_COUNT = 3403;
        public static final short ERR_UPDATE_ENERGY_HERO_NOT_EXIST = 3404;
        public static final short ERR_UPDATE_ENERGY_HERO_INVALID = 3405;

        //NFT
        public static final short ERR_CALL_NFT = 3500;
        public static final short ERR_NOT_ENOUGHT_TOKEN_MINED_TO_CLAIM = 3501;
        public static final short ERR_INVALID_TRANSACTION_MINT_HERO = 3502;
        public static final short ERR_INVALID_TOKEN_MINT_HERO = 3503;
        public static final short ERR_USED_TRANSACTION_TRANFER_TOKEN = 3504;
        public static final short ERR_INVALID_TRANSACTION_TRANFER_TOKEN = 3505;
        public static final short ERR_SIGN_DATA = 3506;
        public static final short ERR_INVALID_TOKEN = 3507;
        public static final short ERR_INVALID_TRANSACTION_BURN_HERO = 3508;
        public static final short ERR_NOT_EXSIST_TRANSACTION_TRANFER_TOKEN = 3509;

        public static final short ERR_INCOMPLETE = 4000;
        public static final short ERR_ALREADY_RECEIVED = 4001;
        public static final short ERR_COMING_SOON = 5000;
        public static final short ERR_WRONG_QUANTITY = 5001;
        public static final short ERR_INVALID_VALUE = 5002;
        public static final short ERR_INVALID_HERO_HASH = 5003;
        public static final short ERR_HERO_TYPE_NOT_THE_SAME = 5004;
        public static final short ERR_THE_BREED_NOT_ENOUGH = 5005;
        public static final short ERR_HERO_BREEDING = 5006;
        public static final short ERR_WITHDRAW_ONCE_DAY = 5007;
        //Lucky draw
        public static final short ERR_RECVICE_REWARD_LUCKY = 6001;
        public static final short ERR_NOT_ENOUGH_TICKET = 6002;
    }

    public class Auth {
        public static final int AUTH_FB = 1;
        public static final int AUTH_GOOGLE = 2;
    }

    public class Character {
        public static final String FILE_PATH_CONFIG_CLASS = "characters/class/Class.json";
        public static final String FILE_PATH_CONFIG_ELEMENT = "characters/element/Element.json";
        public static final String FILE_PATH_CONFIG_KINGDOM = "characters/kingdom/Kingdom.json";
        public static final String FILE_PATH_CONFIG_ROLE = "characters/role/Role.json";

        public static final String FILE_PATH_CONFIG_POWER = "characters/hero/PowerConfig.json";
        public static final String FILE_PATH_CONFIG_HERO_STATS_GROW = "characters/hero/HeroStatsGrowConfig.json";
        public static final String FILE_PATH_CONFIG_HERO_STATS_GROW_MAX = "characters/hero/HeroStatsGrowMaxConfig.json";
        public static final String FILE_PATH_CONFIG_HERO_STATS_GROW_MIN = "characters/hero/HeroStatsGrowMinConfig.json";

        public static final String FILE_PATH_CONFIG_NAG_HERO = "characters/hero/BagHero.json";

        public static final String FILE_PATH_CONFIG_HERO = "characters/hero/Hero.json";
        public static final String FILE_PATH_CONFIG_HERO_NFT = "characters/hero/HeroNFT.json";
        public static final String FILE_PATH_CONFIG_HERO_NFT_MAX = "characters/hero/HeroNFTMax.json";
        public static final String FILE_PATH_CONFIG_HERO_NFT_MIN = "characters/hero/HeroNFTMin.json";

        public static final String FILE_PATH_CONFIG_HERO_SLOT = "characters/hero/HeroSlot.json";
        public static final String FILE_PATH_CONFIG_LEVEL_HERO = "characters/hero/LevelHero.json";
        public static final String FILE_PATH_CONFIG_STAR_HERO = "characters/hero/StarHero.json";
        public static final String FILE_PATH_CONFIG_GRAFT_HERO = "characters/hero/GraftHero.json";
        public static final String FILE_PATH_CONFIG_STORY_HERO = "characters/hero/Story.json";

        public static final String FILE_PATH_CONFIG_SUMMON = "characters/summon/Summon.json";
        public static final String FILE_PATH_CONFIG_RATE_SUMMON = "characters/summon/RateSummon.json";
        public static final String FILE_PATH_CONFIG_KINGDOM_SUMMON = "characters/summon/KingdomSummon.json";

        public static final String FILE_PATH_CONFIG_BLESSING = "characters/blessing/BlessingConfig.json";
        public static final String FILE_PATH_CONFIG_UNLOCK_SLOT_BLESSING = "characters/blessing/UnlockBlessingConfig.json";

        public static final String FILE_PATH_CONFIG_CREEP = "characters/creep/Creep.json";
        public static final String FILE_PATH_CONFIG_CREEP_STATS_GROW = "characters/creep/CreepStatsGrowConfig.json";

        public static final String FILE_PATH_CONFIG_MBOSS = "characters/mboss/MBoss.json";
        public static final String FILE_PATH_CONFIG_MBOSS_STATS_GROW = "characters/mboss/MBossStatsGrowConfig.json";

        public static final String FILE_PATH_CONFIG_MAGE = "characters/mage/Mage.json";
        public static final String FILE_PATH_CONFIG_MAGE_STATS_GROW = "characters/mage/MageStatsGrowConfig.json";
        public static final String FILE_PATH_CONFIG_STONE_MAGE = "characters/mage/StoneMage.json";
        public static final String FILE_PATH_CONFIG_STONE_MAGE_SLOT = "characters/mage/StoneMageSlot.json";
        public static final String FILE_PATH_CONFIG_SKIN_MAGE = "characters/mage/SkinMage.json";

        public static final String FILE_PATH_CONFIG_SKILL_BASE_INFO = "skill/hero/hero-skill.json";
        public static final String FILE_PATH_CONFIG_SKILL_DESC = "skill/hero/hero-skill-info.json";

        public static final String FILE_PATH_CONFIG_SKILL_TREE_SAGE = "skill/sage/sage-skill-tree.json";
        public static final String FILE_PATH_CONFIG_SKILL_INFO_SAGE = "skill/sage/sage-skill-info.json";

        public static final String FILE_PATH_CONFIG_SKILL_CELESTIAL = "skill/celestial/celestial-skill-info.json";
        public static final String FILE_PATH_CONFIG_SKILL_DESC_CELESTIAL = "skill/celestial/celestial-skill-desc.json";

        public static final String FILE_PATH_CONFIG_SKILL_CREEP = "skill/creep/creep-skill-info.json";
        public static final String FILE_PATH_CONFIG_SKILL_DESC_CREEP = "skill/creep/creep-skill-desc.json";

        public static final String FILE_PATH_CONFIG_SKILL_OTHER = "skill/other/other-skill-info.json";
        public static final String FILE_PATH_CONFIG_SKILL_DESC_OTHER = "skill/other/other-skill-desc.json";

        public static final String FILE_PATH_CONFIG_WIN_CODITION = "campaign/win-condition.json";
        public static final String FILE_PATH_CONFIG_TEAM_EFFECT = "campaign/team-effect.json";
        public static final String FILE_PATH_BOSS_MODE_TEAM = "campaign/boss-mode-config.json";

        public static final String FILE_PATH_CONFIG_CELESTIAL = "characters/celestial/Celestial.json";
        public static final String FILE_PATH_CONFIG_CELESTIAL_STATS_GROW = "characters/celestial/CelestialStatsGrowConfig.json";

        public static final String FILE_PATH_CONFIG_RETIRE_HERO = "characters/hut/RetireConfig.json";
        public static final String FILE_PATH_CONFIG_RESET_HERO = "characters/hut/ResetConfig.json";

        public static final String FILE_PATH_CONFIG_BATTLE_BACKGROUND = "battle-bg/battle-bg.json";
    }

    public class Item{
        public static final String FILE_PATH_CONFIG_EQUIP = "items/items.json";
        public static final String FILE_PATH_CONFIG_BANNER = "items/banner.json";
        public static final String FILE_PATH_CONFIG_BORDER = "items/border.json";
        public static final String FILE_PATH_CONFIG_AVATAR = "items/avatar.json";
        public static final String FILE_PATH_CONFIG_ITEM_SLOT = "items/item_slot.json";
        public static final String FILE_PATH_CONFIG_STONE = "items/stone.json";
        public static final String FILE_PATH_CONFIG_SET_WEAPON = "items/set_weapon.json";
        public static final String FILE_PATH_CONFIG_STONE_SLOT = "items/stone_slot.json";
        public static final String FILE_PATH_CONFIG_LEVEL_EQUIP = "items/item_attribute.json";
        public static final String FILE_PATH_CONFIG_LEVEL_STONE = "items/stone_attribute.json";
        public static final String FILE_PATH_CONFIG_FUSION_EQUIP = "items/fusion_item.json";
        public static final String FILE_PATH_CONFIG_FUSION_STONE = "items/fusion_stone.json";
        public static final String FILE_PATH_CONFIG_ITEM = "items/special_item.json";
        public static final String FILE_PATH_CONFIG_FRAGMENT_HERO = "items/fragment_hero.json";
        public static final String FILE_PATH_CONFIG_RATIO = "items/ratio.json";
        public static final String FILE_PATH_CONFIG_SAGE_SLOT_CONFIG = "items/sage_slot.json";
        public static final String FILE_PATH_CONFIG_DEFINE_STONE_ATTRIBUTE = "items/attribute_stone_define.json";
        public static final String FILE_PATH_CONFIG_SAGE_ITEM = "items/sage_item.json";
        public static final String FILE_PATH_CONFIG_CELESTIAL_EQUIP = "items/celestial_item.json";
        public static final String FILE_PATH_CONFIG_CELESTIAL_SLOT_EQUIP = "items/celestial_slot.json";
        public static final String FILE_PATH_CONFIG_ID_WEAPON = "items/id/weapon.json";
        public static final String FILE_PATH_CONFIG_ID_COLOR = "items/id/color.json";
        public static final String FILE_PATH_CONFIG_ID_SLOT = "items/id/define_item_slot.json";
        public static final String FILE_PATH_CONFIG_ID_MONEY = "items/id/money.json";
        public static final String FILE_PATH_CONFIG_ID_GEM = "items/id/gem.json";
    }

    public class Adventure {
        public static final String FILE_PATH_CONFIG_REWARDS_ADVENTURE = "adventure/rewards.json";
    }

    public class Guild {
        public static final String FILE_PATH_CONFIG_GUILD = "guild/Guild.json";
        public static final String FILE_PATH_CONFIG_AVATAR_GUILD = "guild/AvatarGuild.json";
        public static final String FILE_PATH_CONFIG_REWARD_GUILD = "guild/RewardGuild.json";
        public static final String FILE_PATH_CONFIG_REQUEST_GUILD = "guild/RequestGuildConfig.json";
        public static final String FILE_PATH_CONFIG_MANAGER_GUILD = "guild/ManagerGuild.json";
        public static final String FILE_PATH_CONFIG_GIFT_GUILD = "guild/GiftGuild.json";
    }

    public class User {
        public static final String FILE_PATH_CONFIG_LEVEL_USER = "user/level.json";
        public static final String FILE_PATH_CONFIG_WITHDRAW_FEE = "user/fee.json";
        public static final String FILE_PATH_EMAIL_REGISTER = "user/register.txt";
        public static final String FILE_PATH_EMAIL_FORGOT_PASSWORD = "user/forgot.txt";
    }

    public class Mission {
        public static final String FILE_PATH_CONFIG_MISSION = "mission/MissionConfig.json";
        public static final String FILE_PATH_CONFIG_MISSION_NAME = "mission/MissionName.json";
        public static final String FILE_PATH_CONFIG_MISSION_ADD = "mission/AddMissionConfig.json";
    }

    public class Vip {
        public static final String FILE_PATH_CONFIG_HONOR = "vip/honor.json";
        public static final String FILE_PATH_CONFIG_VIP = "vip/vip.json";
    }

    public class Store {
        public static final String FILE_PATH_CONFIG_IAP_STORE = "store/iap/IAPStore.json";
        public static final String FILE_PATH_CONFIG_IAP_HOME = "store/iap/IAPHome.json";

        public static final String FILE_PATH_CONFIG_IAP_PACKAGE = "store/iap/IAPPackage.json";
        public static final String FILE_PATH_CONFIG_IAP_SPECIAL_PACKAGE = "store/iap/IAPSpecialPackage.json";
        public static final String FILE_PATH_CONFIG_IAP_CHALLENGE = "store/iap/IAPChallenge.json";

        public static final String FILE_PATH_CONFIG_IAP_CONDITION = "store/iap/IAPCondition.json";
        public static final String FILE_PATH_CONFIG_IAP_SALE = "store/iap/IAPSaleConfig.json";

        public static final String FILE_PATH_CONFIG_STORE = "store/store.json";
    }

    public class Chat{
        public static final String FILE_PATH_CONFIG_CHANNEL = "chat/ChannelConfig.json";
        public static final String FILE_PATH_CONFIG_CHAT = "chat/ChatConfig.json";
    }

    public class LanguageID {
        public static final String ENGLISH = "eng";
        public static final String TIENG_VIET = "vi";
    }

    public class Mail {
        public static final String FILE_PATH_CONFIG_TIME_MAIL = "mail/time.json";
        public static final String FILE_PATH_CONFIG_MAIL = "mail/mail_config.json";
    }

    public class Friend {
        public static final String FILE_PATH_CONFIG_FRIEND = "friends/friends.json";
        public static final String FILE_PATH_CONFIG_FRIEND_HERO = "friends/friend_heroes.json";
    }

    public class Bag {
        public static final String FILE_PATH_CONFIG_PAY = "items/pay.json";
        public static final String FILE_PATH_CONFIG_HAMMER = "items/hammer.json";
        public static final String FILE_PATH_CONFIG_ENERGY = "bag/energy.json";
        public static final String FILE_PATH_CONFIG_HUNTENERGY = "bag/energyHunt.json";
    }

    public class Hunt{
        public static final String FILE_PATH_CONFIG_HUNT = "hunt/HuntConfig.json";
        public static final String FILE_PATH_CONFIG_REWARD_HUNT = "hunt/RewardHuntConfig.json";
    }

    public class Tower{
        public static final String FILE_PATH_CONFIG_TOWER = "tower/TowerConfig.json";
        public static final String FILE_PATH_CONFIG_RANK_TOWER = "tower/RankTowerConfig.json";
    }

    public class Quest {
        public static final String FILE_PATH_CONFIG_QUEST = "quest/Quest.json";
        public static final String FILE_PATH_CONFIG_QUEST_CONDITION = "quest/QuestCondition.json";
        public static final String FILE_PATH_CONFIG_QUEST_CHEST = "quest/QuestChest.json";
    }

    public class LuckyDraw {
        public static final String FILE_PATH_CONFIG_LUCK_DRAW_SOG = "lucky/luckySOG.json";
        public static final String FILE_PATH_CONFIG_LUCK_DRAW = "lucky/lucky.json";
        public static final String FILE_PATH_CONFIG_LUCK_DRAW_TOP_USER_1 = "lucky/luckyTopUser1.json";
        public static final String FILE_PATH_CONFIG_LUCK_DRAW_TOP_USER_2 = "lucky/luckyTopUser2.json";
        public static final String FILE_PATH_CONFIG_LUCK_DRAW_TOP_USER_3 = "lucky/luckyTopUser3.json";

        public static final String FILE_PATH_CONFIG_LUCK_DRAW_SUPER_SOG = "lucky/luckySuperSOG.json";
        public static final String FILE_PATH_CONFIG_LUCK_DRAW_SUPER = "lucky/luckySuper.json";
        public static final String FILE_PATH_CONFIG_LUCK_DRAW_SUPER_TOP_USER_4 = "lucky/luckySuperTopUser4.json";
        public static final String FILE_PATH_CONFIG_LUCK_DRAW_SUPER_TOP_USER_5 = "lucky/luckySuperTopUser5.json";
        public static final String FILE_PATH_CONFIG_LUCK_DRAW_SUPER_TOP_USER_6 = "lucky/luckySuperTopUser6.json";
    }

    public class WoL {
        public static final String FILE_PATH_CONFIG_WOL = "WoL/WoL.json";
    }

    public class Event {
        public static final String FILE_PATH_CONFIG_GRAND_OPENING = "event/grand_opening_check_in.json";
        public static final String FILE_PATH_CONFIG_EVENT_IN_GAME = "event/EventConfig.json";

        public static final String FILE_PATH_CONFIG_EVENT_CHRISTMAS = "event/christmas.json";

        public static final String FILE_PATH_CONFIG_QUEST_CHEST_EVENT = "event/quest/QuestChest_event.json";
        public static final String FILE_PATH_CONFIG_IAP_PACKAGE_EVENT = "event/iap/IAPPackage_event.json";
        public static final String FILE_PATH_CONFIG_14_DAYS_LOGIN = "event/14days_login.json";
    }

    public class Invite {
        public static final String FILE_PATH_CONFIG_INVITE_BONUS = "invite/InviteConfig.json";
        public static final String FILE_PATH_CONFIG_INVITE_CONDITION = "invite/InviteCondition.json";
        public static final String FILE_PATH_CONFIG_INVITE_REWARD = "invite/InviteReward.json";
    }

    public class Chest {
        public static final String FILE_PATH_CONFIG_HERO_CHEST = "items/special_item/hero_choice_chest.json";
    }

    public class Gem {
        public static final String FILE_PATH_CONFIG_PRICE_UPGRADE_GEM = "items/gem/price.json";
    }

    public class Notification{
        public static final String FILE_PATH_CONFIG_NOTIFICATION_TIME = "notification/NotificationTime.json";
    }

    public class IAPGate {
        public static final int GOOGLE_PLAY = 0;
        public static final int APPLE_STORE = 1;
        public static final int MENA = 2;
    }

    public class Arena {
        public static final String FILE_PATH_CONFIG_ARENA = "arena/Arena.json";
        public static final String FILE_PATH_CONFIG_FIGHT_ARENA = "arena/FightArena.json";
        public static final String FILE_PATH_CONFIG_REWARD_ARENA = "arena/RewardArena.json";
        public static final String FILE_PATH_CONFIG_RANK_ARENA = "arena/RankArena.json";
        public static final String FILE_PATH_CONFIG_PvP_ONLINE = "arena/PvPOnline.json";
    }

    public class DarkGate {
        public static final String DART_GATE_MAIN = "dark-gate/dart-gate-main.json";
        public static final String DART_GATE_REWARDS = "dark-gate/dark-gate-rewards.json";
    }

    public class Bot {
        public static final String FILE_PATH_CONFIG_TEAM_BOT = "bot/TeamBot.json";
    }

    public class League {
        public static final String FILE_PATH_CONFIG_LEAGUE = "league/League.json";
    }
}

