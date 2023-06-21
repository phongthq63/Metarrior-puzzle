package com.bamisu.log.gameserver.module.characters.kingdom;

/**
 * Create by Popeye on 3:14 PM, 2/24/2020
 */
public enum Kingdom {
    DRUID("K1", "Druid"),
    BANISHED("K2", "Banished"),
    DWARF("K3", "Dwarf"),
    ELF("K4", "elf"),
    HUMAN("K5", "Human"),
    GUARDIAN("K6", "Guardian"),
    DARK("K7", "dark");

    String id;
    String name;

    Kingdom(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Kingdom fromName(String name) {
        for (Kingdom element : values()) {
            if (element.getName().equalsIgnoreCase(name)) {
                return element;
            }
        }

        return null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Kingdom fromID(String id) {
        for (Kingdom element : values()) {
            if (element.getId().equalsIgnoreCase(id)) {
                return element;
            }
        }

        return null;
    }
}
