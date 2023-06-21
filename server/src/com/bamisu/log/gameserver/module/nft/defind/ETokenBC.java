package com.bamisu.log.gameserver.module.nft.defind;

/**
 * Created by Quach Thanh Phong
 * On 3/2/2022 - 11:42 PM
 */
public enum ETokenBC {
    MEWA("MEWA", 2),
    SOG("SOG", 1),
    CARD("CARD", 3),
    TICKET_SPIN("TICKET_SPIN",4),
    BUSD("BUSD",5),
    TICKET_SPIN_SUPER("TICKET_SPIN_SUPER",6),
    TICKET("TICKET",7),
    TURN_TICKET("TURN_TICKET",8);

    private String id;
    private int idSign;

    public String getId() {
        return id;
    }

    public int getIdSign() {
        return idSign;
    }

    ETokenBC(String id, int idSign) {
        this.id = id;
        this.idSign = idSign;
    }

    public static ETokenBC fromId(String id) {
        for (ETokenBC token : ETokenBC.values()) {
            if (token.id.equals(id)) return token;
        }
        return null;
    }
}
