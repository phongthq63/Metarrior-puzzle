package com.bamisu.gamelib.entities;

/**
 * Created by Popeye on 9/26/2017.
 */
public class CMD {
    // user 1000 - 1999
    public static final int CMD_REGISTER = 1001;
    public static final int CMD_LOGIN = 1002;
    public static final int CMD_GET_PROFILE = 1003;
    public static final int CMD_UPDATE_DNAME = 1004;
    public static final int CMD_UPDATE_GENDER = 1005;
    public static final int CMD_UPDATE_LANGUGE = 1006;
    public static final int CMD_UPDATE_STATUS_TEXT = 1007;
    public static final int CMD_UPDATE_AVATAR_FRAME = 1008;
    public static final int CMD_UPDATE_AVATAR = 1009;
    public static final int CMD_UPDATE_PUSH_NOTIFICATION_SETTING = 1010;
    public static final int CMD_GET_SETTING = 1013;
    public static final int CMD_GET_SERVER_LIST = 1012;
    public static final int CMD_CHANGE_SERVER = 1014;
    public static final int CMD_ACTIVE_GIFTCODE = 1015;
    public static final int CMD_GET_CHANNEL_LIST = 1016;
    public static final int CMD_CHANGE_CHANNEL = 1017;
    public static final int CMD_SWITCH_ACCOUNT = 1018;
    public static final int CMD_LINK_ACCOUNT = 1019;
    public static final int CMD_SEND_SUPORT = 1020;
    public static final int CMD_UPDATE_STAGE = 1021;
    public static final int CMD_GET_STAGE = 1022;
    public static final int CMD_UPDATE_PUSH_NOTIFICATION_ID = 1023;
    public static final int CMD_UPDATE_USERNAME = 1024;



    public static final int NOTIFY_USER_BALANCE_CHANGE = 1011;

    //social
    public static final int CMD_GET_OTHERS_PROFILE = 3200;
    public static final int CMD_SOCIAL_BATTLE = 3201;

    // zone 4000 - 4999
    public static final int CMD_PING = 4005;

    //Hero
    public static final int CMD_LOAD_SCENE_GET_LIST_HERO = 16000;
    public static final int CMD_GET_USER_HERO_COLLECTION = 16001;
    public static final int CMD_GET_USER_HERO_INFO = 16002;
    public static final int CMD_UP_LEVEL_USER_HERO = 16003;
    public static final int CMD_SHOW_ITEM_HERO_CAN_EQUIP = 16004;
    public static final int CMD_EQUIP_ITEM_HERO = 16005;
    public static final int CMD_EQUIP_ITEM_HERO_QUICK = 16006;
    public static final int CMD_UNEQUIP_ITEM_HERO = 16007;
    public static final int CMD_UNEQUIP_ITEM_HERO_QUICK = 16008;
    public static final int CMD_UP_SIZE_HERO_MODEL = 16009;
    public static final int CMD_UNEQUIP_ALL_ITEM_ALL_HERO = 16010;
    public static final int CMD_GET_BONUS_STORY = 16011;
    public static final int CMD_LOAD_SCENE_UP_STAR_HERO = 16030;
    public static final int CMD_UP_STAR_USER_HERO = 16012;
    public static final int CMD_UP_STAR_LIST_USER_HERO = 16033;
    public static final int CMD_LOAD_SCENE_SUMMON_HERO = 16013;
    public static final int CMD_SUMMON_USER_HERO = 16014;
    public static final int CMD_GET_BONUS_SUMMON_HERO = 16015;
    public static final int CMD_UPDATE_DAY_SUMMON_HERO = 16016;
    public static final int CMD_SUMMON_BONUS_USER_HERO = 16017;
    public static final int CMD_LOAD_SCENE_TEAM_HERO = 16018;
    public static final int CMD_UPDATE_TEAM_HERO = 16019;
    public static final int CMD_LOAD_SCENE_HERO_BLESSING = 16020;
    public static final int CMD_UPDATE_HERO_BLESSING = 16021;
    public static final int CMD_REMOVE_HERO_BLESSING = 16022;
    public static final int CMD_REDUCE_COUNTDOWN_BLESSING = 16028;
    public static final int CMD_OPEN_SLOT_BLESSING = 16029;
    public static final int CMD_LOAD_SCENE_RESET_HERO = 16023;
    public static final int CMD_RESET_HERO = 16024;
    public static final int CMD_LOAD_SCENE_RETIRE_HERO = 16025;
    public static final int CMD_RETIRE_HERO = 16026;
    public static final int CMD_SWITCH_AUTO_RETIRE_HERO = 16027;
    public static final int CMD_GET_HERO_FRIEND_BORROW = 16032;
    public static final int CMD_GET_IDLE_HERO_DATA = 16034;

    //Mage
    public static final int CMD_LOAD_SCENE_MAGE = 24000;
    public static final int CMD_EQUIP_STONE_MAGE = 24001;
    public static final int CMD_GET_BAG_MAGE_EQUIPMENT = 24002;
    public static final int CMD_EQUIP_MAGE_ITEM = 24003;
    public static final int CMD_UNEQUIP_MAGE_ITEM = 24004;
    public static final int CMD_GET_USER_MAGE_SKIN = 24005;
    public static final int CMD_EQUIP_MAGE_SKIN = 24006;
    public static final int CMD_GET_SKILL_TREE = 24007;
    public static final int CMD_STUDY_SKILL = 24008;
    public static final int CMD_RESET_ALL_SKILL = 24009;
    public static final int CMD_RESET_LAST_COLUM_SKILL = 24010;
    public static final int CMD_STUDY_SKILL_MAX = 24011;

    public static final String TEST = "t";

    //EQUIP WEAPON 20000 - 21000
    public static final int CMD_SHOW_INFO_WEAPON = 20000;
    public static final int CMD_GET_WEAPON_TO_UPGRADE = 20001;
    public static final int CMD_GET_LIST_STONE = 20002;
    public static final int CMD_UP_LEVEL_WEAPON = 20003;
    public static final int CMD_ADD_STONE_TO_EQUIP = 20004;
    public static final int CMD_REMOVE_STONE_FROM_EQUIP = 20005;
    public static final int CMD_GET_MONEY = 20009;
    public static final int CMD_FUSION_WEAPON = 20011;
    public static final int CMD_FUSION_STONE = 20013;
    public static final int CMD_GET_LIST_SPECIAL_ITEM = 20015;
    public static final int CMD_GET_LIST_FRAGMENT_HERO = 20016;
    public static final int CMD_USING_ITEM = 20018;
    public static final int CMD_USING_FRAGMENT_HERO = 20019;
    public static final int CMD_GET_ALL_MONEY = 20020;
    public static final int CMD_LIST_FUSION_STONE = 20023;
    public static final int CMD_LIST_FUSION_EQUIP = 20024;
    public static final int CMD_GET_LIST_EQUIP_HERO = 20025;
    public static final int CMD_GET_ENERGY_BAR = 20027;
    public static final int CMD_CHANGE_ENERGY_BAR = 20028;
    public static final int CMD_UPDATE_ENERGY_BAR = 20029;
    public static final int CMD_GET_HUNT_ENERGY_BAR = 20030;
    public static final int CMD_CHANGE_HUNT_ENERGY_BAR = 20031;
    public static final int CMD_UPDATE_HUNT_ENERGY_BAR = 20032;

    //CAMPAIGN 22000
    public static final int FIGHT_MAIN_CAMPAIGN = 22000;
    public static final int GET_CURRENT_CAMPAIGN_STATE = 22001;
    public static final int CMD_COMPLETE_STATION_CAMPAIGN = 22004;
    public static final int CMD_UPDATE_AREA_CAMPAIGN = 22002;
    public static final int CMD_GET_STORE_CAMPAIGN = 22005;
    public static final int CMD_BUY_STORE_CAMPAIGN = 22006;
    public static final int CMD_GET_CAMPAIGN_RANK = 22007;

    //ingame 23000
    public static final int CMD_FIGHTING_JOIN_ROOM = 23000;
    public static final int CMD_FIGHTING_MOVE = 23001;
    public static final int CMD_FIGHTING_FLEE = 23002;
    public static final int CMD_SAGE_SKILL = 23003;
    public static final int CMD_HERO_SKILL = 23004;
    public static final int CMD_CELESTIAL_SKILL = 23005;
    public static final int CMD_UPDATE_PUZZLE_BOARD = 23006;
    public static final int CMD_SELECT_TARGET = 23007;
    public static final int CMD_START_MISSION = 23008;

    //Adventure 26000
    public static final int CMD_LOOT_ITEM = 26000;
    public static final int CMD_GO_TO_ADVENTURE = 26001;
    public static final int CMD_GET_FAST_REWARD = 26002;
    public static final int CMD_CLICK_ON_FAST_REWARD = 26003;
    public static final int CMD_CLICK_ON_CHEST_LOOT_ITEM = 26004;

    //Guild
    public static final int CMD_GET_LIST_GUILD_INFO = 25000;
    public static final int CMD_GET_LIST_GIFT_GUILD = 25001;
    public static final int CMD_CREATE_GUILD = 25002;
    public static final int CMD_GET_GUILD_INFO = 25003;
    public static final int CMD_LEAVE_GUILD = 25004;
    public static final int CMD_CHECK_IN_GUILD = 25006;
    public static final int CMD_REQUEST_JOIN_GUILD = 25007;
    public static final int CMD_GET_REQUEST_JOIN_GUILD = 25014;
    public static final int CMD_EXECUTION_REQUEST_JOIN_GUILD = 25008;
    public static final int CMD_JOIN_GUILD = 25009;
    public static final int CMD_LOAD_SCENE_GUILD_MAIN = 25010;
    public static final int CMD_SETTING_GUILD = 25011;
    public static final int CMD_CHANGE_OFFICE_GUILD = 25015;
    public static final int CMD_CLAIM_GIFT_GUILD = 25012;
    public static final int CMD_REMOVE_GIFT_GUILD = 25013;
    public static final int CMD_REMOVE_ALL_GIFT_GUILD = 25016;


    //Celestial
    public static final int CMD_GET_CELESTIAL_INFO = 27000;
    public static final int CMD_LOAD_SCENE_GET_LIST_CELESTIAL = 27001;
    public static final int CMD_CHANGE_CELESTIAL = 27002;
    public static final int CMD_UNLOCK_CELESTIAL = 27007;

    //Mission
//    public static final int CMD_GET_MISSION_BOARD = 29000;
    public static final int CMD_GET_MISSION_CONFIG = 29001;
//    public static final int CMD_REFRESH_MISSION_BOARD = 29002;
//    public static final int CMD_INCREATE_MISSION_BOARD = 29003;
//    public static final int CMD_RECEIVE_MISSION = 29004;
//    public static final int CMD_REVOKE_MISSION = 29005;
    public static final int CMD_DO_MISSION = 29006;
    public static final int CMD_COMPLETE_MISSION = 29007;
    public static final int CMD_GET_MISSION_RANK = 29008;

    //Mail
    public static final int CMD_SEND_MAIL = 40000;
    public static final int CMD_READ_MAIL = 40001;
    public static final int CMD_CONFIRM_MAIL = 40002;
    public static final int CMD_COLLECT_ALL_MAIL = 40003;
    public static final int CMD_DELETE_ALL_MAIL = 40004;
    public static final int CMD_GET_LIST_MAIL = 40008;
    public static final int CMD_NEW_MAIL = 40009;
    //IAP store
    public static final int CMD_GET_INFO_IAP_TAB = 30000;
    public static final int CMD_CLAIM_IAP_PACKAGE_ITEM = 30001;
    public static final int CMD_CLAIM_IAP_REWARD_CHALLENGE = 30002;
    public static final int CMD_GET_LIST_IAP_TAB_SPECIAL = 30003;

    //Chat
    public static final int CMD_LOAD_SCENE_CHAT = 31000;
    public static final int CMD_LEAVE_SCENE_CHAT = 31001;
    public static final int CMD_LOAD_SCENE_CHAT_GUILD = 31002;
    public static final int CMD_SEND_MESSAGE = 31003;
    public static final int CMD_SEND_MESSAGE_TEXT = 31004;
    public static final int CMD_GET_MESSAGE = 31005;
    public static final int CMD_SEND_LOG = 31006;
    public static final int CMD_REMOVE_ALL_MESSAGE_USER = 31007;

    //Monster Hunt
    public static final int CMD_LOAD_SCENE_HUNT = 32000;
    public static final int CMD_GET_INFO_HUNT = 32002;
    public static final int CMD_REFRESH_HUNT = 32003;
    public static final int CMD_HUNT = 32004;
    public static final int CMD_REWARD_HUNT = 32005;

    //Tower
    public static final int CMD_LOAD_SCENE_TOWER = 33000;
    public static final int CMD_GET_RANK_TOWER = 33001;
    public static final int CMD_FIGHT_TOWER = 33002;

    //Quest
    public static final int CMD_GET_TABLE_QUEST = 34000;
    public static final int CMD_GET_REWARD_QUEST = 34001;
    public static final int CMD_GET_REWARD_CHEST_QUEST = 34002;
    public static final int CMD_ADD_PROGRESS_QUEST = 34003;

    //Invite
    public static final int CMD_INPUT_INVITE_CODE = 35000;
    public static final int CMD_LOAD_SCENE_INVITE_CODE = 35001;
    public static final int CMD_GET_REWARD_INVITE_CODE = 35002;

    //Event
    public static final int CMD_GET_LIST_EVENT = 36000;
    public static final int CMD_GET_INFO_EVENT = 36001;
    public static final int CMD_ACTION_IN_EVENT = 36002;
    public static final int CMD_GET_EVENT_LOGIN_CONFIG = 36003;
    public static final int CMD_COLLECT_GIFT_LOGIN = 36004;

    //Notify
    public static final int CMD_GET_ALL_NOTIFY = 37000;
    public static final int CMD_REMOVE_NOTIFY = 37001;
    public static final int CMD_NOTIFY = 37002;
    public static final int CMD_NOTIFY_MODEL = 37003;

    //Arena
    public static final int CMD_LOAD_SCENE_ARENA = 38000;
    public static final int CMD_GET_LIST_FIGHT_ARENA = 38001;
    public static final int CMD_REFRESH_LIST_FIGHT_ARENA = 38002;
    public static final int CMD_BUY_TICKET_ARENA = 38003;
    public static final int CMD_FIGHT_ARENA = 38004;
    public static final int CMD_LOAD_SCENE_TEAM_HERO_ARENA = 38005;
    public static final int CMD_GET_LIST_RECORD_ARENA = 38006;
    public static final int CMD_CHALLENGE_PvP_ONLINE = 38008;

    //NFT
    public static final int CMD_GET_NFT_INFO = 39000;
    public static final int CMD_VERIFY_MINT_HERO = 39001;
    public static final int CMD_CLAIM_TOKEN_MINE = 39002;
    public static final int CMD_GET_TOKEN_BLOCKCHAIN_INFO = 39003;
    public static final int CMD_REMOVE_MINT_NFT_HERO = 39004;
    public static final int CMD_VERIFY_CLAIM_TOKEN = 39005;
    public static final int CMD_VERIFY_BUY_TOKEN = 39006;
    public static final int CMD_RETURN_NFT_HERO_UP_STAR = 39007;
    public static final int CMD_VERIFY_UP_STAR_NFT_HERO = 39008;
    public static final int CMD_GET_LIST_HERO_BREED = 39009;
    public static final int CMD_GET_LIST_HERO_COUNTDOWN = 39010;
    public static final int CMD_NOTIFY_MINT_HERO = 39011;
    public static final int CMD_RETURN_CLAIM_TOKEN = 39012;
    public static final int CMD_BREED = 39013;
    public static final int CMD_GET_ASCEND_COUNTDOWN = 39014;

    //Store in game
    public static final int CMD_SHOW_STORE_IN_GAME = 50000;
    public static final int CMD_BUY_IN_STORE = 50001;
    public static final int CMD_REFRESH_STORE = 50002;

    //Special Item

    //Friend
    public static final int CMD_SHOW_INFO_LIST_FRIENDS = 61000;
    public static final int CMD_SEND_POINT_TO_ONE_USER = 61001;
    public static final int CMD_RECEIVE_POINT_FROM_ONE_USER = 61002;
    public static final int CMD_RECEIVE_AND_SEND_ALL_USER = 61003;
    public static final int CMD_LIST_FRIENDS_BLOCKED = 61004;
    public static final int CMD_RESTORE_BLOCKED_FRIEND = 61005;
    public static final int CMD_BLOCK_FRIEND = 61006;
    public static final int CMD_DELETE_FRIEND = 61007;
    public static final int CMD_SHOW_LIST_REQUEST_ADD_FRIEND = 61008;
    public static final int CMD_DELETE_ALL_REQUEST = 61009;
    public static final int CMD_DELETE_ONE_REQUEST = 61010;
    public static final int CMD_ACCEPT_ONE_REQUEST = 61011;
    public static final int CMD_ACCEPT_ALL_REQUEST = 61012;
    public static final int CMD_SEARCHING_USER = 61013;
    public static final int CMD_ADD_FRIEND = 61014;
    public static final int CMD_SHOW_INFO_DETAIL_FRIEND = 61015;
    public static final int CMD_SUGGEST_ADD_FRIEND = 61016;
    public static final int CMD_ADD_ALL_FRIEND_IN_SUGGEST = 61017;


    //Vip
    public static final int CMD_CLAIM_HONOR_GIFT = 62000;
    public static final int CMD_SHOW_LIST_HONOR = 62001;
    public static final int CMD_SHOW_INFO_VIP_IAP = 62002;
    public static final int CMD_NOTIFY_HONOR_LEVEL_UP = 62003;

    //WoL
    public static final int CMD_WOL_GET_RANK = 63000;
    public static final int CMD_WOL_USER_ACHIEVEMENT = 63001;
    public static final int CMD_WOL_RECEIVE_REWARD = 63002;

    //Event 1
    public static final int CMD_SHOW_GRAND_OPENING_CHECK_IN = 64000;
    public static final int CMD_COLLECT_GIFT_GRAND_OPENING = 64001;
    public static final int CMD_CLAIM_QUICKLY = 64002;

    //Dark gate
    public static final int GET_DARK_GATE_SCENE_INFO = 65000;
    public static final int UPDATE_DARK_GATE_EVENT_STATUS = 65001;
    public static final int GET_DARK_REALM_SCENE_INFO = 65002;
    public static final int GET_DARK_REALM_RANK = 65003;
    public static final int CHALLENGE_DARK_REALM = 65004;
    public static final int GET_DARK_REALM_MY_RANK = 65005;

    public static final int GET_ENDLESS_NIGHT_SCENE_INFO = 65006;
    public static final int GET_ENDLESS_NIGHT_RANK = 65007;
    public static final int CHALLENGE_ENDLESS_NIGHT = 65008;
    public static final int GET_ENDLESS_NIGHT_MY_RANK = 65009;

    public static final int GET_DARK_REALM_LOGS = 65010;
    public static final int GET_ENDLESS_NIGHT_LOGS = 65011;

    public static final int CMD_GET_LUCKY_PRICE = 66001;
    public static final int CMD_BUY_LUCKY = 66002;

    public static final int CMD_HIST_WINNER = 66003;
    public static final int CMD_REWARD_LUCKY = 66004;
    public static final int CMD_AMOUNT_TO_BUY_TICKETS = 66005;
    public static final int CMD_HIST_LUCKY = 66007;

    //Lucky draw
    public static final int CMD_GET_LUCKY_DRAW_ITEMS = 67000;
    public static final int CMD_GET_RESULT_LUCKY_DRAW = 67002;

    public static class HttpCMD{
        public static final String GET_SERVER_LIST = "get_server_list";
        public static final String GET_LIST_NFT_HERO_INFO = "get_nft_hero_list";
        public static final String MINT_NFT_HERO = "mint_nft_hero";
        public static final String VERIFY_NFT_HERO = "verify_nft_hero";
        public static final String SET_STATUS_NFT_HERO = "set_status_nft_hero";
        public static final String VERIFY_TRANFER_NFT_HERO = "verify_tranfer_nft_hero";
        public static final String ASCEND_HERO = "ascend";
        public static final String CANCEL_ASCEND_HERO = "cancel_ascend";
        public static final String CONFIRM_ASCEND_HERO = "confirm_ascend";
        public static final String GET_HERO_BREEDING = "hero_breeding";
        public static final String CLAIM_HERO_BREEDING = "claim_hero_breeding";
        public static final String LIST_HERO_ASCEND = "list_hero_ascend";
        public static final String GET_HERO_ASCEND_STATS = "hero_ascend_stats";
        public static final String CHECK_HERO_FOR_SALE = "check_hero_for_sale";
        public static final String LIST_HERO_OPEN_BOX = "list_hero_open_box";
        public static final String RESET_RMQ = "reset_rmq";
    }


    public class InternalMessage {

        public static final String GET_SERVER_INFO = "get_server_info";
        public static final String GET_LOGIN_INFO = "get_login_info";
        public static final String CHANGE_USER_MONEY = "change_user_money";
        public static final String CHANGE_USER_RESOURCE = "change_user_resource";
        public static final String CHANGE_USER_TOKEN = "change_user_token";
        public static final String GET_SOG_OF_USER = "get_sog";
        public static final String UPDATE_USERNAME_PASSWORD = "update_username_password";
        public static final String CHANGE_PASSWORD = "change_password";

        public static final String REMOVE_USER_ROOM_CHAT = "remove_user_chat";
        public static final String ADD_USER_ROOM_CHAT = "add_user_chat";
        public static final String CHECK_USER_ROOM_CHAT = "check_user_chat";
        public static final String LIST_USER_ROOM_CHAT = "list_user_chat";
        public static final String REMOVE_INFO_USER_CHAT = "remove_info_chat";
        public static final String GET_ALL_MESSAGE_CHAT = "get_all_message_chat";
        public static final String GET_LOG_GUILD = "get_log_guild";
        public static final String SEND_LOG_GUILD = "send_log_guild";

        public static final String ARISE_GAME_EVENT = "arise_game_event";

        public static final String FIGHT_CAMPAIGN_RESULT = "fight_campaign_result";
        public static final String FIGHT_MISSION_RESULT = "fight_mission_result";

        public static final String FIGHT_TOWER_RESULT = "FIGHT_TOWER_RESULT";
        public static final String FIGHT_HUNT_RESULT = "FIGHT_HUNT_RESULT";
        public static final String FIGHT_ARENA_OFFLINE_RESULT = "FIGHT_ARENA_OFFLINE_RESULT";
        public static final String FIGHT_DARK_REALM_RESULT = "FIGHT_DARK_REALM_RESULT";

        public static final String GET_CACHE_USER_MODEL = "get_cache_user_model";
        public static final String GET_CACHE_USER_GUILD_MODEL = "get_cache_user_guild_model";

        public static final String SEND_MAIL_TO_PLAYER = "send_mail";
        public static final String ADD_SALE_IAP = "add_sale_iap";
        public static final String REMOVE_SALE_IAP = "remove_sale_iap";
        public static final String GET_LEVEL_USER = "get_level_user";
        public static final String FIGHT_ENDLESS_NIGHT_RESULT = "FENR";
        public static final String SEND_ALL_PLAYER_NOTIFY_MODEL = "send_notify_model";
        public static final String UPDATE_CONFIG_MODULE_SERVER = "update_config_module_server";

        public static final String ARENA_END_DAY = "arena_end_day";
        public static final String CLOSE_SEASON_ARENA = "close_season_arena";
        public static final String OPEN_SEASON_ARENA = "open_season_arena";
        public static final String SEND_NOTIFY = "send_notify";
        public static final String ON_PRIVATE_CHAT = "opc";
        public static final String ON_ALLIANCE_CHAT = "oac";
        public static final String ON_GLOBAL_CHAT = "ogc";
        public static final String ON_CHANEL_CHAT = "occ";

        public static final String GET_NFT_HERO_LIST = "get_nft_hero_list";
        public static final String MINT_NFT_HERO = "mint_nft_hero";
        public static final String VERIFY_MINT_NFT_HERO = "claim_nft_hero";
        public static final String LOCK_HERO = "lock_hero";
        public static final String UNLOCK_HERO = "unlock_hero";
        public static final String TRANFER_NFT_HERO = "tranfer_nft_hero";
        public static final String DEPOSIT_TOKEN = "deposit_token";
        public static final String WITHDRAW_TOKEN = "withdraw_token";
        public static final String REJECT_WITHDRAW_TOKEN = "reject_withdraw_token";
        public static final String REQUEST_WITHDRAW_TOKEN = "request_withdraw_token";
        public static final String HTTP_SUM_HERO = "http_sum_hero";
        public static final String HTTP_CLAIM_HERO = "http_claim_hero";
        public static final String HTTP_REGISTER = "http_register";
        public static final String HTTP_SEND_CODE = "req_send_code";
        public static final String HTTP_REQUEST_RESET_PASSWORD = "request_reset_password";
        public static final String HTTP_RESET_PASSWORD = "http_reset_password";
        public static final String HTTP_LINK_WALLET = "http_link_wallet";
        public static final String TRANSFER_HERO_FROM_WALLET = "transfer_wallet_hero";
    }
}
