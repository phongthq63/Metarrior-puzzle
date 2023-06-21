package com.bamisu.gamelib.entities;

import com.bamisu.gamelib.utils.Utils;

/**
 * Create by Popeye on 6:01 PM, 10/31/2019
 */
public enum Attr {
    HP(0, "HP"),                      // mau
    STRENGTH(1, "STR"),         // dame vat ly
    INTELLIGENCE(2, "INT"),            // dame phep
    ATTACK(3, "ATK"),                // sát thương
    ARMOR(4, "ARM"),                   // Giap
    MAGIC_RESISTANCE(5,"MR"),        // Khang phep
    DEFENSE(6, "DEF"),                   // phòng ngự
    DEXTERITY(7, "DEX"),                    // chính xác
    AGILITY(8, "AGI"),             // tốc độ
    ELUSIVENESS(9, "ELU"),       // né tránh
    ARMOR_PENETRATION(10, "APEN"),      // xuyên giáp
    MAGIC_PENETRATION(11, "MPEN"),             // xuyên kháng phép
    DEFENSE_PENETRATION(12, "DPEN"),                  // xuyên phòng ngự
    CRITICAL_CHANCE(13, "CRIT"),                  // may mắn
    CRITICAL_BONUS_DAMAGE(14, "CRITBONUS"),                  // sức bền
    TENACITY(15, "TEN");

    int value;
    String shortName;

    Attr(int value, String shortName) {
        this.value = value;
        this.shortName = shortName;
    }


    public int getValue() {
        return value;
    }

    public String shortName(){
        return shortName;
    }

    public static Attr fromValue(int value) {
        for (Attr Attr : Attr.values()) {
            if (Attr.getValue() == value) {
                return Attr;
            }
        }

        return null;
    }

    public static Attr fromStrValue(String name) {
        for (Attr Attr : Attr.values()) {
            if (Attr.shortName().equals(name) ) {
                return Attr;
            }
        }

        return null;
    }

    public static Attr getRandom(){
        return values()[Utils.randomInRange(0, values().length - 1)];
    }
}
