package com.bamisu.gamelib.utils;

import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.TransactionDetail;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * Created by Popeye on 6/23/2017.
 */
public class UserUtils {

    /**
     * thông báo thay đổi tiền cho player
     * @param uid
     * @param arrayCurrent
     * @param reason
     * @param zone
     */
    public static void changeMoney(long uid, SFSArray arrayCurrent, TransactionDetail reason, Zone zone) {
        User user = ExtensionUtility.getInstance().getUserById(uid);
        if (user == null) {
            return;
        }
        SFSObject data = new SFSObject();
        data.putSFSArray(Params.DATA, arrayCurrent);
        zone.getExtension().handleInternalMessage(CMD.InternalMessage.CHANGE_USER_MONEY, data);
    }

    /**
     * thông báo thay đổi resource cho player
     * @param uid
     * @param arrayCurrent
     * @param reason
     * @param zone
     */
    public static void changeResource(long uid, SFSArray arrayCurrent, TransactionDetail reason, Zone zone) {
        User user = ExtensionUtility.getInstance().getUserById(uid);
        if (user == null) {
            return;
        }
        SFSObject data = new SFSObject();
        data.putSFSArray(Params.DATA, arrayCurrent);
        zone.getExtension().handleInternalMessage(CMD.InternalMessage.CHANGE_USER_RESOURCE, data);
    }

    /**
     * thông báo thay đổi token cho player
     * @param uid
     * @param arrayCurrent
     * @param reason
     * @param zone
     */
    public static void changeToken(long uid, SFSArray arrayCurrent, TransactionDetail reason, Zone zone) {
        User user = ExtensionUtility.getInstance().getUserById(uid);
        if (user == null) {
            return;
        }
        SFSObject data = new SFSObject();
        data.putSFSArray(Params.DATA, arrayCurrent);
        zone.getExtension().handleInternalMessage(CMD.InternalMessage.CHANGE_USER_TOKEN, data);
    }

    public static class TransactionType {
        public static final TransactionDetail GIFT_CODE = new TransactionDetail((short) 100, "Gift code ");
        public static final TransactionDetail UP_LEVEL_EQUIP_HERO = new TransactionDetail((short) 101, "UP_LEVEL_EQUIP_HERO");
        public static final TransactionDetail GET_BONUS_STORY = new TransactionDetail((short) 102, "GET_BONUS_STORY");
        public static final TransactionDetail LEVEL_UP_HERO = new TransactionDetail((short) 103, "LEVEL_UP_HERO");
        public static final TransactionDetail BANNER_SUMMON_HERO = new TransactionDetail((short) 104, "BANNER_SUMMON_HERO");
        public static final TransactionDetail SUMMON_HERO_BY_FRAGMENT = new TransactionDetail((short) 105, "SUMMON_HERO_BY_FRAGMENT");
        public static final TransactionDetail UPDATE_KINGDOM_SUMMON_BANNER = new TransactionDetail((short) 106, "UPDATE_KINGDOM_SUMMON_BANNER");
        public static final TransactionDetail UPDATE_ELEMENT_SUMMON_BANNER = new TransactionDetail((short) 107, "UPDATE_ELEMENT_SUMMON_BANNER");
        public static final TransactionDetail GET_FAST_REWARD = new TransactionDetail((short) 108, "GET_FAST_REWARD");
        public static final TransactionDetail CREATE_GUILD = new TransactionDetail((short) 109, "CREATE_GUILD");
        public static final TransactionDetail FUSION_EQUIP_HERO = new TransactionDetail((short) 110, "FUSION_EQUIP_HERO");
        public static final TransactionDetail GET_SPECIAL_ITEM = new TransactionDetail((short) 111, "GET_SPECIAL_ITEM");
        public static final TransactionDetail RESET_SAGE_SKILL = new TransactionDetail((short) 112, "RESET_SAGE_SKILL");
        public static final TransactionDetail SELECT_USE_CELESTIAL = new TransactionDetail((short) 113, "SELECT_USE_CELESTIAL");
        public static final TransactionDetail CHANGE_DISPLAY_NAME = new TransactionDetail((short) 114, "CHANGE_DISPLAY_NAME");
        public static final TransactionDetail REFRESH_MISSION_BOARD = new TransactionDetail((short) 115, "REFRESH_MISSION_BOARD");
        public static final TransactionDetail REFRESH_HUNT = new TransactionDetail((short) 115, "REFRESH_HUNT");
        public static final TransactionDetail COMPLETE_CAMPAIGN = new TransactionDetail((short) 116, "COMPLETE_CAMPAIGN");
        public static final TransactionDetail BUY_CAMPAIGN_STORE = new TransactionDetail((short) 117, "BUY_CAMPAIGN_STORE");
        public static final TransactionDetail RESET_HERO = new TransactionDetail((short) 118, "RESET_HERO");
        public static final TransactionDetail CHANGE_RESOURCE = new TransactionDetail((short) 119, "CHANGE_RESOURCE");
        public static final TransactionDetail DO_IAP_PACKAGE = new TransactionDetail((short) 120, "DO_IAP_PACKAGE");
        public static final TransactionDetail WOL_REWARD = new TransactionDetail((short) 121, "WOL_REWARD");
        public static final TransactionDetail GET_AFK_REWARD = new TransactionDetail((short) 122, "GET_AFK_REWARD");
        public static final TransactionDetail FUSION_EQUIP_SMART = new TransactionDetail((short) 123, "FUSION_EQUIP_SMART");
        public static final TransactionDetail LIST_FUSION_GEM = new TransactionDetail((short) 124, "LIST_FUSION_GEM");
        public static final TransactionDetail LIST_FUSION_EQUIP = new TransactionDetail((short) 125, "LIST_FUSION_EQUIP");
        public static final TransactionDetail FUSION_STONE = new TransactionDetail((short) 127, "FUSION_STONE");
        public static final TransactionDetail USE_SPECIAL_ITEM_DIAMOND_CHEST = new TransactionDetail((short) 128, "USE_SPECIAL_ITEM_DIAMOND_CHEST");
        public static final TransactionDetail USE_SPECIAL_ITEM_GOLD = new TransactionDetail((short) 129, "USE_SPECIAL_ITEM_GOLD");
        public static final TransactionDetail USE_SPECIAL_ITEM_MERIT = new TransactionDetail((short) 130, "USE_SPECIAL_ITEM_MERIT");
        public static final TransactionDetail USE_SPECIAL_ITEM_VIP = new TransactionDetail((short) 131, "USE_SPECIAL_ITEM_VIP");
        public static final TransactionDetail USE_SPECIAL_ITEM_EQUIPMENT_CHEST = new TransactionDetail((short) 132, "USE_SPECIAL_ITEM_EQUIPMENT_CHEST");
        public static final TransactionDetail USE_SPECIAL_ITEM_GEM_CHEST = new TransactionDetail((short) 133, "USE_SPECIAL_ITEM_GEM_CHEST");
        public static final TransactionDetail USE_SPECIAL_ITEM_ESSENCE = new TransactionDetail((short) 134, "USE_SPECIAL_ITEM_ESSENCE");
        public static final TransactionDetail USE_SPECIAL_ITEM_SUMMON_HERO = new TransactionDetail((short) 135, "USE_SPECIAL_ITEM_SUMMON_HERO");
        public static final TransactionDetail GIFT_30_DAYS = new TransactionDetail((short) 136, "GIFT_30_DAYS");
        public static final TransactionDetail RECEIVE_FRIEND_POINT = new TransactionDetail((short) 137, "RECEIVE_FRIEND_POINT");
        public static final TransactionDetail GUILD = new TransactionDetail((short) 138, "GUILD");
        public static final TransactionDetail UPSIZE_HERO_BAG = new TransactionDetail((short) 139, "UPSIZE_HERO_BAG");
        public static final TransactionDetail UPGRADE_HERO = new TransactionDetail((short) 140, "UPGRADE_HERO");
        public static final TransactionDetail GIFT_MAIL = new TransactionDetail((short) 141, "GIFT_MAIL");
        public static final TransactionDetail BONUS_SUMMON_HERO = new TransactionDetail((short) 142, "BONUS_SUMMON_HERO");
        public static final TransactionDetail COUNT_DOWN_BLESSING = new TransactionDetail((short) 143, "COUNT_DOWN_BLESSING");
        public static final TransactionDetail OPEN_SLOT_BLESSING = new TransactionDetail((short) 144, "OPEN_SLOT_BLESSING");
        public static final TransactionDetail RETIRE_HERO = new TransactionDetail((short) 145, "RETIRE_HERO");
        public static final TransactionDetail ACTIVE_GIFT_CODE = new TransactionDetail((short) 146, "ACTIVE_GIFT_CODE");
        public static final TransactionDetail COMPLETE_HUNT = new TransactionDetail((short) 147, "COMPLETE_HUNT");
        public static final TransactionDetail COMPLETE_MISSION = new TransactionDetail((short) 148, "COMPLETE_MISSION");
        public static final TransactionDetail COMPLETE_TOWER = new TransactionDetail((short) 149, "COMPLETE_TOWER");
        public static final TransactionDetail COMPLETE_QUEST = new TransactionDetail((short) 150, "COMPLETE_QUEST");
        public static final TransactionDetail GET_REWARD_QUEST = new TransactionDetail((short) 151, "GET_REWARD_QUEST");
        public static final TransactionDetail INVITE_CODE = new TransactionDetail((short) 152, "INVITE_CODE");
        public static final TransactionDetail VIP = new TransactionDetail((short) 153, "VIP");
        public static final TransactionDetail SUMMON_HERO = new TransactionDetail((short) 154, "SUMMON_HERO");
        public static final TransactionDetail STORE_IN_GAME = new TransactionDetail((short) 155, "REFRESH_TURN_IN_STORE");
        public static final TransactionDetail BUY_IN_STORE =new TransactionDetail((short) 156, "BUY_IN_STORE") ;
        public static final TransactionDetail FIGHT_ARENA = new TransactionDetail((short) 157, "FIGHT_ARENA");
        public static final TransactionDetail COMPLETE_ARENA = new TransactionDetail((short) 158, "COMPLETE_ARENA");
        public static final TransactionDetail FIX_BUG = new TransactionDetail((short) 120, "Đền bù lỗi AFK nhận 1 tỉ vàng");
        public static final TransactionDetail DARK_REALM_REWARD = new TransactionDetail((short) 160, "đánh boss dark realm");
        public static final TransactionDetail DARK_REALM_REWARD_END_EVENT = new TransactionDetail((short) 161, "kết thúc dark realm");
        public static final TransactionDetail ENDLESS_NIGHT_REWARD = new TransactionDetail((short) 162, "đánh boss endless night");
        public static final TransactionDetail ENDLESS_NIGHT_REWARD_END_EVENT = new TransactionDetail((short) 163, "kết thúc endless night");
        public static final TransactionDetail BUY_ARENA_TICKET = new TransactionDetail((short) 164, "BUY_ARENA_TICKET");
        public static final TransactionDetail EXCHANGE_IN_EVENT = new TransactionDetail((short) 165, "EXCHANGE_IN_EVENT");
        public static final TransactionDetail GMT_CHANGE_MONEY = new TransactionDetail((short) 166, "GMT_MONEY_CHANGE");
        public static final TransactionDetail DO_MISSION = new TransactionDetail((short) 166, "DO_MISSION");
        public static final TransactionDetail DO_HUNT = new TransactionDetail((short) 167, "DO_HUNT");
        public static final TransactionDetail CHARGE_ENERGY = new TransactionDetail((short) 168, "CHARGE_ENERGY");
        public static final TransactionDetail BET_PVP_ONLINE = new TransactionDetail((short) 170, "BET_PVP_ONLINE");;
        public static final TransactionDetail CHECK_IN_GUILD = new TransactionDetail((short) 1689, "CHECK_IN_GUILD");
        public static final TransactionDetail CLAIM_GIFT_GUILD = new TransactionDetail((short) 1690, "CLAIM_GIFT_GUILD");
        public static final TransactionDetail RETURN_MINT_HERO = new TransactionDetail((short) 1691, "RETURN_MINT_HERO");
        public static final TransactionDetail BUY_TOKEN_BC = new TransactionDetail((short) 1692, "BUY_TOKEN_BC");
        public static final TransactionDetail CLAIM_TOKEN_BC = new TransactionDetail((short) 1693, "CLAIM_TOKEN_BC");
        public static final TransactionDetail RETURN_CLAIM_TOKEN_BC = new TransactionDetail((short) 1694, "RETURN_CLAIM_TOKEN_BC");

        public static final TransactionDetail BUY_LUCKY_JACK = new TransactionDetail((short) 1695, "BUY_LUCKY_JACK");
        public static final TransactionDetail REWARD_LUCK_JACK = new TransactionDetail((short) 1696, "REWARD_LUCK_JACK");

        public static final TransactionDetail REWARD_LUCKY_DRAW = new TransactionDetail((short) 1697, "REWARD_LUCKY_DRAW");
        public static final TransactionDetail UPDATE_TICKET_LUCKY_DRAW = new TransactionDetail((short) 1698, "UPDATE_TICKET_LUCKY_DRAW");
        public static final TransactionDetail UPDATE_BUSD_LUCKY_DRAW = new TransactionDetail((short) 1699, "UPDATE_BUSD_LUCKY_DRAW");
        public static final TransactionDetail COLLECT_GIFT_LOGIN = new TransactionDetail((short) 1699, "COLLECT_GIFT_LOGIN");
    }
}
