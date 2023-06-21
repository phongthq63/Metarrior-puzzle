package com.bamisu.log.gameserver.module.GameEvent.defind;

public enum EGameEvent {
    LEVEL_USER_UPDATE("0", "Cấp dộ người chơi thay đổi"),
    LEVEL_HERO_UPDATE("1", "Cấp độ tướng của người chơi thay đổi"),
    STAR_HERO_UPDATE("2", "Sao tướng của người chơi thay đổi"),
    STATION_CAMPAIGN_UPDATE("3", "Màn phó bản chơi của người chơi thay đổi"),
    CHAP_CAMPAIGN_UPDATE("30", "Chương phó bản của người chơi thay đổi"),
    FLOOR_TOWER_UPDATE("4", "Tầng chơi của người chơi thay đổi"),
    GET_HERO("5", "Người chơi nhận tướng"),
    SUMMON_TAVERN("6", "Triệu hồi tướng trong quán rượu"),
    USER_PAYMENT("7", "Người chơi thanh toán"),
    UPDATE_MONEY("8", "Tiền của người chơi thay đổi"),
    ENHANDCE_ITEM("9", "Cường hóa dồ dùng"),
    DO_MISSION("10", "Làm nhiệm vụ"),
    DO_GUILD_HUNT("11", "Tham gia săn bắt bang hội"),
    DO_HUNT("12", "Tham gia săn bắt đơn"),
    DO_TOWER("13", "Tham gia leo tháp"),
    DO_ARENA("14", "Tham gia chiến trường"),
    DO_CAMPAIGN("15", "Tham gia đánh phó bản"),
//    WIN_ARENA("16", "Chiến thắng chiến trường"),
//    WIN_MISSION("17", "Chiến thắng nhiệm vụ"),
//    WIN_HUNT("18", "Chiến thắng cuộc săn"),
    COLLECT_AFK_PACKAGE("19", "Thu thập gói offline"),
    USE_FAST_REWARD_AFK_PACKAGE("20", "Sử dụng chức năng thu thập nhanh gói ofline"),
    BUY_INGAME_STORE("21", "Mua đồ trong shop trong game"),
    BLESSING_HERO("22", "Ban phước tướng"),
    SEND_MONEY("23", "Gửi tiền cho người chơi khác"),
    FINISH_CAMPAIGN_FIGHTING("24", "Đánh xong trận đấu campaign"),
    FINISH_TOWER_FIGHTING("25", "Đánh xong trận đấu tower"),
    FINISH_HUNT_FIGHTING("26", "Đánh xong trận đấu hunt"),
    FINISH_MISSION_FIGHTING("27", "Đánh xong trận đấu mission"),
    FINISH_ARENA("37", "Đánh xong trận đấu arena"),
    LINK_ACCOUNT("28", "Người chơi tiến hành liên kết tài khoản"),
    OPEN_SLOT_BLESSING_HERO("29", "Người chơi mở rộng slot ban phước cho tướng"),
    SEND_FRIEND_REQUEST("34", "Gửi kết bạn cho người chơi khác"),
    JOIN_GUILD("35", "Tham gia vào Guild"),
    CHAT("36", "Chat"),
    CLAIM_IAP_PACKAGE("32", "Nhận 1 gói trong IAP"),
    CLAIM_IAP_CHALLENGE("33", "Nhận 1 mốc thưởng trong IAP"),
    NEW_GIFT_GUILD("34", "Có quà mới trong guild");

    String id;
    String description;

    public String getId() {
        return id;
    }

    EGameEvent(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public static EGameEvent fromID(String id){
        for(EGameEvent gameEvent : EGameEvent.values()){
            if(gameEvent.id.equals(id)){
                return gameEvent;
            }
        }
        return null;
    }
}
