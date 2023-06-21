package com.bamisu.gamelib.item.define;

import java.util.Random;

public enum SpecialItem {
    GOLD_2HOURS("SPI1003"),
    GOLD_6HOURS("SPI1004"),
    GOLD_12HOURS("SPI1005"),
    GOLD_24HOURS("SPI1006"),
    MERITS_2HOURS("SPI1009"),
    MERITS_6HOURS("SPI1010"),
    MERITS_12HOURS("SPI1011"),
    MERITS_24HOURS("SPI1012"),
    ESSENCE_2HOURS("SPI1015"),
    ESSENCE_6HOURS("SPI1016"),
    ESSENCE_12HOURS("SPI1017"),
    ESSENCE_24HOURS("SPI1018"),
    COMMON_DIAMOND_CHEST_20("SPI1020"),
    ELITE_DIAMOND_CHEST_50("SPI1021"),
    EPIC_DIAMOND_CHEST_100("SPI1022"),
    LEGENDARY_DIAMOND_CHEST_250("SPI1023"),
//    SPECIFIC_HERO_SHARDS("SPI1047"),
    CHOOSE_HERO_CHEST("SPI1048"),
    RANDOM_EPIC_HERO_SHARDS_BLUE("SPI1049"),
    RANDOM_LEGENDARY_HERO_SHARDS_PURPLE("SPI1050"),
    DWARF_KINGDOM_EPIC_HERO_CARD_BLUE("SPI1053"),
    DRUID_KINGDOM_EPIC_HERO_CARD_BLUE("SPI1054"),
    BANISHED_KINGDOM_EPIC_HERO_CARD_BLUE("SPI1055"),
//    RANDOM_KINGDOM_LEGENDARY_HERO_CARD("SPI1056"),
    RANDOM_COMMON_EQUIPMENT_CHEST_GREY("SPI1077"),
    RANDOM_RARE_EQUIPMENT_CHEST_GREEN("SPI1078"),
    RANDOM_ELITE_EQUIPMENT_CHEST_TEAL("SPI1079"),
    RANDOM_EPIC_EQUIPMENT_CHEST_BLUE("SPI1080"),
    RANDOM_LEGENDARY_EQUIPMENT_CHEST_PURPLE("SPI1081"),
    RANDOM_COMMON_GEMS_CHEST_GREY("SPI1089"),
    RANDOM_RARE_GEM_CHEST_GREEN("SPI1090"),
    RANDOM_ELITE_GEM_CHEST_TEAL("SPI1091"),
    RANDOM_EPIC_GEM_CHEST_BLUE("SPI1092"),
    RANDOM_LEGENDARY_GEM_CHEST_PURPLE("SPI1093"),
    PROTECTOR_EMBLEM_3DAYS("SPI1124"),
    PROTECTOR_EMBLEM_30DAYS("SPI1125"),
    ARCHMAGE_EMBLEM_3DAYS("SPI1126"),
    ARCHMAGE_EMBLEM_30DAYS("SPI1127");
//    KINGDOM_LEGENDARY_HERO_CARD("SPI1056");

    String id;

    SpecialItem(String id) {
        this.id = id;
    }

    SpecialItem(){

    }

    public static SpecialItem fromID(String id){
        for (SpecialItem value : SpecialItem.values()) {
            if(value.getId().equalsIgnoreCase(id)) return value;
        }

        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public static String getRandom(){
        Random random = new Random();
        return values()[random.nextInt(values().length)].id;
    }
}
