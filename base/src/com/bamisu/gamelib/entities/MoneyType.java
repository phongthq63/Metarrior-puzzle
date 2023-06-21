package com.bamisu.gamelib.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Random;

/**
 * Create by Popeye on 6:01 PM, 10/31/2019
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MoneyType {

    DIAMOND("MON1000"),                                  //Kim cương
    GOLD("MON1001"),                                        //Vàng
    MERITS("MON1002"),                        //Danh vọng
    ESSENCE("MON1003"),      //Nguyên liệu đột phá
    ELEMENT_BANNER("MON1006"),                    //Triệu hồi theo elementIndex
    KINGDOM_BANNER("MON1007"),                    //Triệu hồi theo kingdom
    HERO_BANNER("MON1008"),                        //Triệu hồi hero ngẫu nhiên
    FRIENDSHIP_BANNER("MON1009"),              //Triệu hồi theo điểm friendship
    RANDOM_KINGDOM_LEGENDARY_HERO_CARD("MON1010"),                       // Thẻ triệu hồi đặc biệt (được thưởng)
    STAR_CAMPAIGN("MON1012"),                                 //sao campaign
    HONOR("MON1013"),                                     //Điểm danh dự
    ALLIANCE_COIN("MON1014"),
    RETIRE_COIN("MON1015"),
    HUNTER_COIN("MON1016"),
    BLESSING_TICKET("MON1017"),
    SAGE_EXP("MON1018"),
    MIRAGE_ESSENCE("MON1019"),
    ARENA_TICKET("MON1020"),
    ARENA_COIN("MON1021"),
    CANDY_CANE("MON1022"),
    MIXED("MON1023"),
    VOUCHER_LOTO_SOG("MON1024");
    String id;

    MoneyType(String id) {
        this.id = id;
    }

    public static MoneyType fromID(String id){
        for (MoneyType value : MoneyType.values()) {
            if(value.getId().equals(id)) return value;
        }

        return null;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    public String toString() {
        return String.valueOf(id);
    }

    public static String getRandom(){
        Random random = new Random();
        return String.valueOf(values()[random.nextInt(values().length)]);
    }

}
